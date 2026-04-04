package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.modelo.Zona;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorUDP extends Thread {
    private DatagramSocket socket;
    private Map<String, ClienteInfo> clientes;
    private EstadoJuego estado;
    private boolean ejecutando;
    private static final int PUERTO = 5000;
    private long ultimoBroadcast = 0;

    private static class ClienteInfo {
        InetAddress ip;
        int puerto;
        ClienteInfo(InetAddress ip, int puerto) { this.ip = ip; this.puerto = puerto; }
        String getKey() { return ip.getHostAddress() + ":" + puerto; }
    }

    public ServidorUDP() throws Exception {
        socket = new DatagramSocket(PUERTO);
        socket.setSoTimeout(100);
        clientes = new ConcurrentHashMap<>();
        estado = new EstadoJuego();
        ejecutando = true;
        System.out.println("[Servidor] Iniciado en puerto " + PUERTO);
        // Pelotas
        for (int i = 0; i < 6; i++) {
            float x = 512 + (i % 3 - 1) * 100;
            float y = 384 + (i / 3 - 1) * 80;
            estado.agregarPelota(new Pelota(i, x, y));
        }
        /**
         * Zona de gol centrada
         * */
        float yCentro = 384;

        /**
         * Zona izquierdd
         * Z*/
        estado.agregarZona(new Zona(0, 1, 100, yCentro, 80, 80));

        /**
         * Zona derecha
         * */
        estado.agregarZona(new Zona(1, 2, 924, yCentro, 80, 80));
    }

    @Override
    public void run() {
        while (ejecutando) {
            try {
                byte[] buffer = new byte[8192];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String texto = new String(packet.getData(), 0, packet.getLength());
                InetAddress ip = packet.getAddress();
                int puerto = packet.getPort();
                String clave = ip.getHostAddress() + ":" + puerto;

                Mensaje msg = Mensaje.deserializar(texto);
                if (msg == null) continue;

                if (!clientes.containsKey(clave)) {
                    clientes.put(clave, new ClienteInfo(ip, puerto));
                    System.out.println("[Servidor] Nuevo cliente: " + clave);
                }

                switch (msg.getTipo()) {
                    case UNIRSE:
                        int id = clientes.size();
                        Jugador jug = new Jugador(id, msg.getDatos(), 0);
                        jug.setX(400 + (float) Math.random() * 200);
                        jug.setY(300 + (float) Math.random() * 200);
                        estado.agregarJugador(jug);
                        System.out.println("[Servidor] Nuevo jugador: " + jug.getNombre() + " (ID " + id + ")");
                        break;
                    case MOVER_JUGADOR:
                        Jugador j = estado.getJugador(msg.getIdJugador());
                        if (j != null) { j.setX(msg.getX()); j.setY(msg.getY()); }
                        break;
                    case TOMAR_PELOTA:
                        Pelota pTomar = estado.getPelota(msg.getIdObjeto());
                        if (pTomar != null && pTomar.getIdJugador() == -1) {
                            pTomar.setIdJugador(msg.getIdJugador());
                            Jugador jugador = estado.getJugador(msg.getIdJugador());
                            if (jugador != null) jugador.setTienePelota(true);
                        }
                        break;
                    case MOVER_PELOTA:
                        Pelota pMover = estado.getPelota(msg.getIdObjeto());
                        if (pMover != null && pMover.getIdJugador() == msg.getIdJugador()) {
                            pMover.setX(msg.getX()); pMover.setY(msg.getY());
                        }
                        break;
                    case SOLTAR_PELOTA:
                        Pelota pSoltar = estado.getPelota(msg.getIdObjeto());
                        if (pSoltar != null && pSoltar.getIdJugador() == msg.getIdJugador()) {
                            pSoltar.setIdJugador(-1);
                            pSoltar.setVx(msg.getVx()); pSoltar.setVy(msg.getVy());
                            Jugador jugador = estado.getJugador(msg.getIdJugador());
                            if (jugador != null) jugador.setTienePelota(false);
                        }
                        break;
                }

                // Detectar goles
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

                if (System.currentTimeMillis() - ultimoBroadcast > 50) {
                    broadcastEstado();
                    ultimoBroadcast = System.currentTimeMillis();
                }
            } catch (java.net.SocketTimeoutException e) {}
            catch (Exception e) { if(ejecutando) e.printStackTrace(); }
        }
        socket.close();
    }

    private void broadcastEstado() {
        if (clientes.isEmpty()) return;
        String estadoStr = serializarEstado();
        byte[] data = estadoStr.getBytes();
        for (ClienteInfo c : clientes.values()) {
            try {
                DatagramPacket packet = new DatagramPacket(data, data.length, c.ip, c.puerto);
                socket.send(packet);
            } catch (Exception e) {}
        }
    }

    private String serializarEstado() {
        StringBuilder sb = new StringBuilder("STATE|");
        for (Jugador j : estado.getJugadores().values()) {
            sb.append(j.getId()).append(",")
                    .append(j.getNombre()).append(",")
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

    public void detener() { ejecutando = false; interrupt(); }
}
