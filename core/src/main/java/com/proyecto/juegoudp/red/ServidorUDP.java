package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.modelo.Zona;
import com.proyecto.juegoudp.utilidades.Constantes;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorUDP extends Thread {
    private DatagramSocket socket;
    private Map<String, ClienteInfo> clientes;
    /** Clave ip:puerto → id de jugador ya registrado (evita duplicar por UNIRSE repetido). */
    private Map<String, Integer> jugadorPorCliente;
    private EstadoJuego estado;
    private boolean ejecutando;
    private static final int PUERTO = 5000;
    private long ultimoBroadcast = 0;
    /** Alineado con {@link Zona#getIdJugador()} (zonas 1 y 2 en el tablero). */
    private int siguienteIdJugador = 1;
    private long seqEstado = 0;
    private final int jugadoresRequeridos;
    private final int duracionPartidaSegundos;
    private long instanteInicioPartidaMs = -1;

    private static class ClienteInfo {
        InetAddress ip;
        int puerto;
        ClienteInfo(InetAddress ip, int puerto) { this.ip = ip; this.puerto = puerto; }
    }

    public ServidorUDP() throws Exception {
        this(2, 60);
    }

    /**
     * @param jugadoresRequeridosSolicitados cantidad de jugadores para pasar de lobby a partida (2..{@link Constantes#MAX_JUGADORES})
     */
    public ServidorUDP(int jugadoresRequeridosSolicitados) throws Exception {
        this(jugadoresRequeridosSolicitados, 60);
    }

    public ServidorUDP(int jugadoresRequeridosSolicitados, int duracionPartidaSegundosSolicitada) throws Exception {
        jugadoresRequeridos = Math.max(2, Math.min(jugadoresRequeridosSolicitados, Constantes.MAX_JUGADORES));
        duracionPartidaSegundos = Math.max(30, duracionPartidaSegundosSolicitada);
        socket = new DatagramSocket(PUERTO);
        socket.setSoTimeout(100);
        clientes = new ConcurrentHashMap<>();
        jugadorPorCliente = new ConcurrentHashMap<>();
        estado = new EstadoJuego();
        ejecutando = true;
        System.out.println("[Servidor] Iniciado en puerto " + PUERTO + " (objetivo lobby: " + jugadoresRequeridos + " jugadores, tiempo: " + duracionPartidaSegundos + "s)");
        for (int i = 0; i < 6; i++) {
            float x = 512 + (i % 3 - 1) * 100;
            float y = 384 + (i / 3 - 1) * 80;
            estado.agregarPelota(new Pelota(i, x, y));
        }
        float yCentro = 384;
        estado.agregarZona(new Zona(0, 1, 100, yCentro, 80, 80));
        estado.agregarZona(new Zona(1, 2, 924, yCentro, 80, 80));
    }

    private void enviarMensajeA(InetAddress ip, int puerto, Mensaje msg) {
        try {
            byte[] data = msg.serializar().getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, ip, puerto);
            socket.send(packet);
        } catch (Exception e) {
            if (ejecutando) e.printStackTrace();
        }
    }

    private static String[] parseNombreYAvatar(String datos) {
        String nombre = "Jugador";
        int avatar = 0;
        if (datos != null && !datos.isEmpty()) {
            int tab = datos.indexOf('\t');
            if (tab >= 0) {
                nombre = datos.substring(0, tab).trim();
                if (nombre.isEmpty()) nombre = "Jugador";
                try {
                    avatar = Integer.parseInt(datos.substring(tab + 1).trim());
                } catch (NumberFormatException ignored) {}
            } else {
                nombre = datos.trim().isEmpty() ? "Jugador" : datos.trim();
            }
        }
        return new String[] { nombre, String.valueOf(avatar) };
    }

    @Override
    public void run() {
        while (ejecutando) {
            try {
                byte[] buffer = new byte[8192];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String texto = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                InetAddress ip = packet.getAddress();
                int puerto = packet.getPort();
                String clave = ip.getHostAddress() + ":" + puerto;

                Mensaje msg = Mensaje.deserializar(texto);
                if (msg == null) continue;

                if (!clientes.containsKey(clave)) {
                    clientes.put(clave, new ClienteInfo(ip, puerto));
                    System.out.println("[Servidor] Nuevo cliente: " + clave);
                }

                boolean partidaTerminada = tiempoRestanteSegundos() <= 0 && instanteInicioPartidaMs > 0;

                switch (msg.getTipo()) {
                    case UNIRSE: {
                        Integer idPrevio = jugadorPorCliente.get(clave);
                        if (idPrevio != null) {
                            Jugador existente = estado.getJugador(idPrevio);
                            if (existente != null) {
                                String[] na = parseNombreYAvatar(msg.getDatos());
                                existente.setNombre(na[0]);
                                try {
                                    existente.setAvatarId(Integer.parseInt(na[1]));
                                } catch (NumberFormatException ignored) {}
                            }
                            enviarMensajeA(ip, puerto, new Mensaje(TipoMensaje.TU_ID, idPrevio, 0, 0, 0, 0, 0, ""));
                            broadcastEstado();
                            ultimoBroadcast = System.currentTimeMillis();
                            break;
                        }
                        if (estado.getJugadores().size() >= Constantes.MAX_JUGADORES) {
                            enviarMensajeA(ip, puerto, new Mensaje(TipoMensaje.TU_ID, -1, 0, 0, 0, 0, 0, "SALA_LLENA"));
                            break;
                        }
                        String[] na = parseNombreYAvatar(msg.getDatos());
                        int id = siguienteIdJugador++;
                        int avatarId = 0;
                        try {
                            avatarId = Integer.parseInt(na[1]);
                        } catch (NumberFormatException ignored) {}
                        Jugador jug = new Jugador(id, na[0], avatarId);
                        jug.setX(400 + (float) Math.random() * 200);
                        jug.setY(300 + (float) Math.random() * 200);
                        estado.agregarJugador(jug);
                        jugadorPorCliente.put(clave, id);
                        System.out.println("[Servidor] Nuevo jugador: " + jug.getNombre() + " (ID " + id + ")");
                        enviarMensajeA(ip, puerto, new Mensaje(TipoMensaje.TU_ID, id, 0, 0, 0, 0, 0, ""));
                        broadcastEstado();
                        ultimoBroadcast = System.currentTimeMillis();
                        break;
                    }
                    case MOVER_JUGADOR: {
                        if (partidaTerminada) break;
                        Jugador j = estado.getJugador(msg.getIdJugador());
                        if (j != null) { j.setX(msg.getX()); j.setY(msg.getY()); }
                        break;
                    }
                    case TOMAR_PELOTA: {
                        if (partidaTerminada) break;
                        Pelota pTomar = estado.getPelota(msg.getIdObjeto());
                        if (pTomar != null && pTomar.getIdJugador() == -1) {
                            pTomar.setIdJugador(msg.getIdJugador());
                            Jugador jugador = estado.getJugador(msg.getIdJugador());
                            if (jugador != null) jugador.setTienePelota(true);
                        }
                        break;
                    }
                    case MOVER_PELOTA: {
                        if (partidaTerminada) break;
                        Pelota pMover = estado.getPelota(msg.getIdObjeto());
                        if (pMover != null && pMover.getIdJugador() == msg.getIdJugador()) {
                            pMover.setX(msg.getX()); pMover.setY(msg.getY());
                        }
                        break;
                    }
                    case SOLTAR_PELOTA: {
                        if (partidaTerminada) break;
                        Pelota pSoltar = estado.getPelota(msg.getIdObjeto());
                        if (pSoltar != null && pSoltar.getIdJugador() == msg.getIdJugador()) {
                            pSoltar.setIdJugador(-1);
                            pSoltar.setVx(msg.getVx()); pSoltar.setVy(msg.getVy());
                            Jugador jugador = estado.getJugador(msg.getIdJugador());
                            if (jugador != null) jugador.setTienePelota(false);
                        }
                        break;
                    }
                    default:
                        break;
                }

                if (instanteInicioPartidaMs < 0 && estado.getJugadores().size() >= jugadoresRequeridos) {
                    instanteInicioPartidaMs = System.currentTimeMillis();
                    System.out.println("[Servidor] Partida iniciada. Duracion: " + duracionPartidaSegundos + "s");
                }

                if (!partidaTerminada) {
                    for (Pelota p : estado.getPelotas().values()) {
                        if (p.getIdJugador() != -1) continue;
                        for (Zona zona : estado.getZonas().values()) {
                            if (zona.contienePunto(p.getX(), p.getY())) {
                                Jugador jugador = estado.getJugador(zona.getIdJugador());
                                if (jugador != null) {
                                    jugador.sumarPuntaje(10);
                                    System.out.println("⚽ GOL de " + jugador.getNombre() + "!");
                                }
                                p.setX(512); p.setY(384);
                                p.setVx(0); p.setVy(0);
                                p.setIdJugador(-1);
                                break;
                            }
                        }
                    }
                }

                if (System.currentTimeMillis() - ultimoBroadcast > 50) {
                    broadcastEstado();
                    ultimoBroadcast = System.currentTimeMillis();
                }
            } catch (java.net.SocketTimeoutException e) {
                // siguiente iteración
            } catch (Exception e) {
                if (ejecutando) e.printStackTrace();
            }
        }
        socket.close();
    }

    private void broadcastEstado() {
        if (clientes.isEmpty()) return;
        String estadoStr = serializarEstado();
        byte[] data = estadoStr.getBytes(StandardCharsets.UTF_8);
        for (ClienteInfo c : clientes.values()) {
            try {
                DatagramPacket packet = new DatagramPacket(data, data.length, c.ip, c.puerto);
                socket.send(packet);
            } catch (Exception e) {
                if (ejecutando) e.printStackTrace();
            }
        }
    }

    private String serializarEstado() {
        StringBuilder sb = new StringBuilder("STATE|");
        sb.append(++seqEstado).append("|");
        sb.append(jugadoresRequeridos).append("|");
        sb.append(tiempoRestanteSegundos()).append("|");
        for (Jugador j : estado.getJugadores().values()) {
            String nomEnc = URLEncoder.encode(j.getNombre(), StandardCharsets.UTF_8);
            sb.append(j.getId()).append(",")
                    .append(nomEnc).append(",")
                    .append(j.getX()).append(",")
                    .append(j.getY()).append(",")
                    .append(j.getPuntaje()).append(",")
                    .append(j.getAvatarId()).append(",")
                    .append(j.isTienePelota() ? 1 : 0).append(";");
        }
        sb.append("|");
        for (Pelota p : estado.getPelotas().values()) {
            sb.append(p.getId()).append(",")
                    .append(p.getX()).append(",")
                    .append(p.getY()).append(",")
                    .append(p.getVx()).append(",")
                    .append(p.getVy()).append(",")
                    .append(p.getIdJugador()).append(";");
        }
        return sb.toString();
    }

    private int tiempoRestanteSegundos() {
        if (instanteInicioPartidaMs < 0) return duracionPartidaSegundos;
        long transcurridoMs = System.currentTimeMillis() - instanteInicioPartidaMs;
        int restantes = duracionPartidaSegundos - (int) (transcurridoMs / 1000L);
        return Math.max(0, restantes);
    }

    public void detener() { ejecutando = false; interrupt(); }
}
