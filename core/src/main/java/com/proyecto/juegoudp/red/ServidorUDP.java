
package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.modelo.Zona;
import com.proyecto.juegoudp.utilidades.Constantes;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servidor autoritativo UDP: recibe mensajes, aplica reglas y difunde el estado serializado.
 */
public class ServidorUDP extends Thread {
    private DatagramSocket conexionDatagrama;
    private final Map<String, ClienteInfo> clientesPorClave;
    /** Evita duplicar jugadores por {@code UNIRSE} repetido (clave ip:puerto → id). */
    private final Map<String, Integer> idJugadorPorCliente;
    private EstadoJuego estadoJuego;
    private boolean activo;
    private long instanteUltimoBroadcast;
    private int siguienteIdJugador = 1;
    private long secuenciaEstado;
    private final int jugadoresRequeridos;
    private final int duracionPartidaSegundos;
    private long instanteInicioPartidaMs = -1;

    private final IProcesadorMensajesServidor procesadorMensajes = new ProcesadorMensajesServidor();
    private final ISerializadorEstadoServidor serializadorEstado = new SerializadorEstadoServidor();
    private final Envio envio = (ip, puerto, mensaje) -> enviarMensajeA(ip, puerto, mensaje);

    /** Dirección y puerto de un cliente conectado por UDP. */
    private static class ClienteInfo {
        final InetAddress ip;
        final int puerto;

        ClienteInfo(InetAddress ip, int puerto) {
            this.ip = ip;
            this.puerto = puerto;
        }
    }

    /** Vista pública del cliente para el procesador (sin exponer la clase interna mutable). */
    public static class ClienteInfoPublica {
        public final InetAddress ip;
        public final int puerto;

        public ClienteInfoPublica(InetAddress ip, int puerto) {
            this.ip = ip;
            this.puerto = puerto;
        }
    }

    /** Abstracción de envío (inyección de dependencias). */
    public interface Envio {
        void enviarA(InetAddress ip, int puerto, Mensaje mensaje);
    }

    public ServidorUDP() throws Exception {
        this(2, 60);
    }

    public ServidorUDP(int jugadoresRequeridosSolicitados) throws Exception {
        this(jugadoresRequeridosSolicitados, 60);
    }

    public ServidorUDP(int jugadoresRequeridosSolicitados, int duracionPartidaSegundosSolicitada) throws Exception {
        jugadoresRequeridos = jugadoresRequeridosSolicitados >= 4 ? 4 : 2;
        if (jugadoresRequeridos > Constantes.MAX_JUGADORES) {
            throw new IllegalArgumentException("MAX_JUGADORES no soporta modo de 4 equipos.");
        }
        duracionPartidaSegundos = Math.max(30, duracionPartidaSegundosSolicitada);
        conexionDatagrama = new DatagramSocket(Constantes.PUERTO_UDP);
        conexionDatagrama.setSoTimeout(100);
        clientesPorClave = new ConcurrentHashMap<>();
        idJugadorPorCliente = new ConcurrentHashMap<>();
        estadoJuego = new EstadoJuego();
        activo = true;
        System.out.println("[Servidor] Iniciado en puerto " + Constantes.PUERTO_UDP
                + " (objetivo lobby: " + jugadoresRequeridos + " jugadores, tiempo: " + duracionPartidaSegundos + "s)");
        for (int i = 0; i < 6; i++) {
            float x = 512 + (i % 3 - 1) * 100;
            float y = 384 + (i / 3 - 1) * 80;
            estadoJuego.agregarPelota(new Pelota(i, x, y));
        }
        float yCentro = 384;
        estadoJuego.agregarZona(new Zona(0, 1, 100, yCentro, 80, 80));
        estadoJuego.agregarZona(new Zona(1, 2, 924, yCentro, 80, 80));
    }

    private void enviarMensajeA(InetAddress ip, int puerto, Mensaje mensaje) {
        try {
            byte[] datos = mensaje.serializar().getBytes(StandardCharsets.UTF_8);
            DatagramPacket paquete = new DatagramPacket(datos, datos.length, ip, puerto);
            conexionDatagrama.send(paquete);
        } catch (Exception e) {
            if (activo) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (activo) {
            try {
                byte[] memoriaRecepcion = new byte[8192];
                DatagramPacket paquete = new DatagramPacket(memoriaRecepcion, memoriaRecepcion.length);
                conexionDatagrama.receive(paquete);
                String texto = new String(paquete.getData(), 0, paquete.getLength(), StandardCharsets.UTF_8);
                InetAddress ip = paquete.getAddress();
                int puerto = paquete.getPort();
                String claveCliente = ip.getHostAddress() + ":" + puerto;

                Mensaje mensaje = Mensaje.deserializar(texto);
                if (mensaje == null) {
                    continue;
                }

                if (!clientesPorClave.containsKey(claveCliente)) {
                    clientesPorClave.put(claveCliente, new ClienteInfo(ip, puerto));
                    System.out.println("[Servidor] Nuevo cliente: " + claveCliente);
                }

                boolean partidaTerminada = tiempoRestanteSegundos() <= 0 && instanteInicioPartidaMs > 0;

                Map<String, ClienteInfoPublica> clientesPublicos = new ConcurrentHashMap<>();
                for (Map.Entry<String, ClienteInfo> entrada : clientesPorClave.entrySet()) {
                    ClienteInfo info = entrada.getValue();
                    clientesPublicos.put(entrada.getKey(), new ClienteInfoPublica(info.ip, info.puerto));
                }
                procesadorMensajes.procesar(
                        mensaje, ip, puerto, claveCliente, estadoJuego, idJugadorPorCliente,
                        clientesPublicos, envio, partidaTerminada);

                if (instanteInicioPartidaMs < 0 && estadoJuego.getJugadores().size() >= jugadoresRequeridos) {
                    instanteInicioPartidaMs = System.currentTimeMillis();
                    System.out.println("[Servidor] Partida iniciada. Duracion: " + duracionPartidaSegundos + "s");
                }

                if (!partidaTerminada) {
                    for (Pelota pelota : estadoJuego.getPelotas().values()) {
                        if (pelota.getIdJugador() != -1) {
                            continue;
                        }
                        for (Zona zona : estadoJuego.getZonas().values()) {
                            if (zona.contienePunto(pelota.getX(), pelota.getY())) {
                                registrarGol(zona);
                                pelota.setX(512);
                                pelota.setY(384);
                                pelota.setVx(0);
                                pelota.setVy(0);
                                pelota.setIdJugador(-1);
                                break;
                            }
                        }
                    }
                }

                if (System.currentTimeMillis() - instanteUltimoBroadcast > 50) {
                    difundirEstado();
                    instanteUltimoBroadcast = System.currentTimeMillis();
                }
            } catch (SocketTimeoutException ignored) {
                // siguiente iteración
            } catch (Exception e) {
                if (activo) {
                    e.printStackTrace();
                }
            }
        }
        conexionDatagrama.close();
    }

    private void difundirEstado() {
        if (clientesPorClave.isEmpty()) {
            return;
        }
        String cadenaEstado = serializarEstado();
        byte[] datos = cadenaEstado.getBytes(StandardCharsets.UTF_8);
        for (ClienteInfo cliente : clientesPorClave.values()) {
            try {
                DatagramPacket paquete = new DatagramPacket(datos, datos.length, cliente.ip, cliente.puerto);
                conexionDatagrama.send(paquete);
            } catch (Exception e) {
                if (activo) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String serializarEstado() {
        return serializadorEstado.serializar(
                ++secuenciaEstado, jugadoresRequeridos, tiempoRestanteSegundos(), estadoJuego);
    }

    private int tiempoRestanteSegundos() {
        if (instanteInicioPartidaMs < 0) {
            return duracionPartidaSegundos;
        }
        long transcurridoMs = System.currentTimeMillis() - instanteInicioPartidaMs;
        int restantes = duracionPartidaSegundos - (int) (transcurridoMs / 1000L);
        return Math.max(0, restantes);
    }

    public void detener() {
        activo = false;
        interrupt();
    }

    private void registrarGol(Zona zona) {
        if (juegaPorEquipos()) {
            int idEquipo = equipoDesdeIdJugador(zona.getIdJugador());
            int puntajeEquipo = 0;
            for (Jugador jugador : estadoJuego.getJugadores().values()) {
                if (equipoDesdeIdJugador(jugador.getId()) == idEquipo) {
                    jugador.sumarPuntaje(10);
                    puntajeEquipo += jugador.getPuntaje();
                }
            }
            String nombreEquipo = idEquipo == 1 ? "Equipo A (J1/J3)" : "Equipo B (J2/J4)";
            System.out.println("⚽ GOL de " + nombreEquipo + "!");
            System.out.println("[Servidor] Puntaje total equipo tras gol: " + puntajeEquipo);
            return;
        }

        Jugador jugador = estadoJuego.getJugador(zona.getIdJugador());
        if (jugador != null) {
            jugador.sumarPuntaje(10);
            System.out.println("⚽ GOL de " + jugador.getNombre() + "!");
        }
    }

    private boolean juegaPorEquipos() {
        return jugadoresRequeridos >= 4;
    }

    private int equipoDesdeIdJugador(int idJugador) {
        return idJugador % 2 == 0 ? 2 : 1;
    }
}
