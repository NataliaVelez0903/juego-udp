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
 * Implementa el servidor autoritativo de la partida utilizando el protocolo UDP.
 *
 * Esta clase es responsable de recibir mensajes enviados por los clientes,
 * procesarlos mediante la lógica del servidor y difundir periódicamente
 * el estado actualizado de la partida.
 *
 * Además, administra la conexión de clientes, el estado del juego,
 * el control del tiempo de partida y la detección de eventos
 * como anotaciones en las zonas de gol.
 *
 * Al extender Thread, el servidor se ejecuta en un hilo
 * independiente que mantiene activo el ciclo principal de recepción,
 * actualización y difusión del estado.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class ServidorUDP extends Thread {

    /**
     * Socket UDP utilizado para recibir y enviar paquetes.
     */
    private DatagramSocket conexionDatagrama;

    /**
     * Mapa de clientes conectados, indexados por una clave compuesta
     * por su dirección IP y puerto.
     */
    private final Map<String, ClienteInfo> clientesPorClave;

    /**
     * Mapa que relaciona cada cliente con el identificador del jugador
     * que le fue asignado.
     *
     * Se utiliza para evitar duplicar jugadores cuando un cliente
     * reenvía un mensaje de unión.
     */
    private final Map<String, Integer> idJugadorPorCliente;

    /**
     * Estado actual del juego administrado por el servidor.
     */
    private EstadoJuego estadoJuego;

    /**
     * Indica si el servidor continúa activo.
     */
    private boolean activo;

    /**
     * Instante del último broadcast del estado hacia los clientes.
     */
    private long instanteUltimoBroadcast;

    /**
     * Siguiente identificador de jugador.
     *
     * Aunque está presente como atributo, la asignación efectiva
     * de ids puede depender de la lógica del procesador de mensajes.
     */
    private int siguienteIdJugador = 1;

    /**
     * Número de secuencia del estado enviado a los clientes.
     */
    private long secuenciaEstado;

    /**
     * Cantidad de jugadores requeridos para iniciar la partida.
     */
    private final int jugadoresRequeridos;

    /**
     * Duración total de la partida, en segundos.
     */
    private final int duracionPartidaSegundos;

    /**
     * Instante en milisegundos en el que comenzó la partida.
     *
     * Un valor de {@code -1} indica que la partida aún no ha iniciado.
     */
    private long instanteInicioPartidaMs = -1;

    /**
     * Procesador encargado de aplicar la lógica del servidor
     * a cada mensaje recibido.
     */
    private final IProcesadorMensajesServidor procesadorMensajes = new ProcesadorMensajesServidor();

    /**
     * Serializador encargado de convertir el estado del juego
     * en una cadena para enviarla a los clientes.
     */
    private final ISerializadorEstadoServidor serializadorEstado = new SerializadorEstadoServidor();

    /**
     * Implementación de la abstracción de envío utilizada
     * por el procesador de mensajes.
     */
    private final Envio envio = (ip, puerto, mensaje) -> enviarMensajeA(ip, puerto, mensaje);

    /**
     * Representa la información interna de un cliente conectado por UDP.
     */
    private static class ClienteInfo {

        /**
         * Dirección IP del cliente.
         */
        final InetAddress ip;

        /**
         * Puerto del cliente.
         */
        final int puerto;

        /**
         * Construye la información de un cliente conectado.
         *
         * @param ip dirección IP del cliente
         * @param puerto puerto del cliente
         */
        ClienteInfo(InetAddress ip, int puerto) {
            this.ip = ip;
            this.puerto = puerto;
        }
    }

    /**
     * Representa una vista pública e inmutable de un cliente conectado.
     *
     * Su propósito es exponer información básica del cliente al procesador
     * de mensajes sin revelar directamente la estructura interna mutable.
     */
    public static class ClienteInfoPublica {

        /**
         * Dirección IP del cliente.
         */
        public final InetAddress ip;

        /**
         * Puerto del cliente.
         */
        public final int puerto;

        /**
         * Construye una vista pública de un cliente.
         *
         * @param ip dirección IP del cliente
         * @param puerto puerto del cliente
         */
        public ClienteInfoPublica(InetAddress ip, int puerto) {
            this.ip = ip;
            this.puerto = puerto;
        }
    }

    /**
     * Define una abstracción para el envío de mensajes a clientes.
     *
     * Esta interfaz permite desacoplar la lógica de procesamiento
     * de mensajes del mecanismo concreto utilizado para enviar respuestas.
     */
    public interface Envio {

        /**
         * Envía un mensaje a un cliente determinado.
         *
         * @param ip dirección IP de destino
         * @param puerto puerto de destino
         * @param mensaje mensaje a enviar
         */
        void enviarA(InetAddress ip, int puerto, Mensaje mensaje);
    }

    /**
     * Construye un servidor UDP con configuración predeterminada
     * de 2 jugadores y 60 segundos de duración.
     *
     * @throws Exception si ocurre un error al crear el socket del servidor
     */
    public ServidorUDP() throws Exception {
        this(2, 60);
    }

    /**
     * Construye un servidor UDP indicando la cantidad de jugadores requerida
     * y usando 60 segundos como duración predeterminada.
     *
     * @param jugadoresRequeridosSolicitados cantidad de jugadores solicitada
     * @throws Exception si ocurre un error al crear el socket del servidor
     */
    public ServidorUDP(int jugadoresRequeridosSolicitados) throws Exception {
        this(jugadoresRequeridosSolicitados, 60);
    }

    /**
     * Construye un servidor UDP con la configuración indicada.
     *
     * Este constructor inicializa el socket de red, las estructuras
     * de clientes, el estado del juego, las pelotas iniciales
     * y las zonas de gol.
     *
     * La cantidad de jugadores requerida se normaliza a 2 o 4
     * y la duración mínima de la partida se establece en 30 segundos.
     *
     * @param jugadoresRequeridosSolicitados cantidad de jugadores solicitada
     * @param duracionPartidaSegundosSolicitada duración solicitada de la partida
     * @throws Exception si ocurre un error al crear el socket del servidor
     */
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

    /**
     * Envía un mensaje UDP a una dirección y puerto específicos.
     *
     * @param ip dirección IP del destinatario
     * @param puerto puerto del destinatario
     * @param mensaje mensaje a enviar
     */
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

    /**
     * Ejecuta el ciclo principal del servidor.
     *
     * Este método escucha continuamente mensajes de los clientes,
     * los procesa mediante la lógica del servidor, verifica el inicio
     * o fin de la partida, registra goles cuando corresponde
     * y difunde periódicamente el estado a todos los clientes conectados.
     */
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

    /**
     * Difunde el estado actual del juego a todos los clientes conectados.
     *
     * Si no hay clientes conectados, este método no realiza ninguna acción.
     */
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

    /**
     * Serializa el estado actual del juego para ser enviado a los clientes.
     *
     * @return la cadena serializada del estado
     */
    private String serializarEstado() {
        return serializadorEstado.serializar(
            ++secuenciaEstado, jugadoresRequeridos, tiempoRestanteSegundos(), estadoJuego);
    }

    /**
     * Calcula el tiempo restante de la partida en segundos.
     *
     * Si la partida todavía no ha comenzado, retorna la duración completa
     * configurada para la partida.
     *
     * @return tiempo restante en segundos
     */
    private int tiempoRestanteSegundos() {
        if (instanteInicioPartidaMs < 0) {
            return duracionPartidaSegundos;
        }
        long transcurridoMs = System.currentTimeMillis() - instanteInicioPartidaMs;
        int restantes = duracionPartidaSegundos - (int) (transcurridoMs / 1000L);
        return Math.max(0, restantes);
    }

    /**
     * Detiene la ejecución del servidor.
     *
     * Este método marca al servidor como inactivo e interrumpe
     * el hilo de ejecución.
     */
    public void detener() {
        activo = false;
        interrupt();
    }

    /**
     * Registra un gol a partir de la zona alcanzada por la pelota.
     *
     * En modo por equipos, suma puntos a todos los jugadores
     * pertenecientes al equipo correspondiente. En modo individual,
     * suma puntos únicamente al jugador asociado con la zona.
     *
     * @param zona zona de gol donde se detectó la anotación
     */
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

    /**
     * Indica si la partida actual se juega en modo por equipos.
     *
     * @return {@code true} si la partida es por equipos; false en caso contrario
     */
    private boolean juegaPorEquipos() {
        return jugadoresRequeridos >= 4;
    }

    /**
     * Determina el equipo al que pertenece un jugador según su identificador.
     *
     * Los jugadores con id impar pertenecen al equipo 1
     * y los jugadores con id par pertenecen al equipo 2.
     *
     * @param idJugador identificador del jugador
     * @return el identificador del equipo correspondiente
     */
    private int equipoDesdeIdJugador(int idJugador) {
        return idJugador % 2 == 0 ? 2 : 1;
    }
}
