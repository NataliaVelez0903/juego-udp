package com.proyecto.juegoudp.red;
<<<<<<< Updated upstream
// Host, controla el juego y sincroniza todoo
public class ServidorUDP {
}
=======

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
    private Map<String, ClienteInfo> clientes;  // clave = "ip:puerto"
    private EstadoJuego estado;
    private boolean ejecutando;
    private static final int PUERTO = 5000;
    private long ultimoBroadcast = 0;

    // Clase interna para guardar dirección completa
    private static class ClienteInfo {
        InetAddress ip;
        int puerto;
        ClienteInfo(InetAddress ip, int puerto) {
            this.ip = ip;
            this.puerto = puerto;
        }
        String getKey() { return ip.getHostAddress() + ":" + puerto; }
    }

    public ServidorUDP() throws Exception {
        socket = new DatagramSocket(PUERTO);
        socket.setSoTimeout(100);
        clientes = new ConcurrentHashMap<>();
        estado = new EstadoJuego();
        ejecutando = true;
        System.out.println("[Servidor] Iniciado en puerto " + PUERTO);

        // Crear pelotas de ejemplo
        for (int i = 0; i < 6; i++) {
            float x = 512 + (i % 3 - 1) * 100;
            float y = 384 + (i / 3 - 1) * 80;
            estado.agregarPelota(new Pelota(i, x, y));
        }

        // Crear zonas de gol (arcos) para hasta 6 jugadores
        int[] xGoles = {100, 924, 100, 924, 100, 924};
        int[] yGoles = {300, 300, 200, 200, 400, 400};
        for (int i = 0; i < 6; i++) {
            Zona zona = new Zona(i, i+1, xGoles[i], yGoles[i], 80, 120);
            estado.agregarZona(zona);
        }
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

                System.out.println("[Servidor] Recibido: " + texto.substring(0, Math.min(50, texto.length())));

                Mensaje msg = Mensaje.deserializar(texto);
                if (msg == null) {
                    System.out.println("[Servidor] Mensaje nulo, ignorar");
                    continue;
                }

                // Registrar cliente si es nuevo (por IP+puerto)
                if (!clientes.containsKey(clave)) {
                    clientes.put(clave, new ClienteInfo(ip, puerto));
                    System.out.println("[Servidor] Nuevo cliente: " + clave);
                }

                // Procesar mensaje
                switch (msg.getTipo()) {
                    case UNIRSE:
                        int id = clientes.size();
                        Jugador jugador = new Jugador(id, msg.getDatos(), 0);
                        jugador.setX(400 + (float) Math.random() * 200);
                        jugador.setY(300 + (float) Math.random() * 200);
                        estado.agregarJugador(jugador);
                        System.out.println("[Servidor] Nuevo jugador: " + jugador.getNombre() + " (ID " + id + ")");
                        break;
                    case MOVER_JUGADOR:
                        Jugador j = estado.getJugador(msg.getIdJugador());
                        if (j != null) {
                            j.setX(msg.getX());
                            j.setY(msg.getY());
                        }
                        break;
                    case TOMAR_PELOTA:
                        Pelota pTomar = estado.getPelota(msg.getIdObjeto());
                        if (pTomar != null && pTomar.getIdJugador() == -1) {
                            pTomar.setIdJugador(msg.getIdJugador());
                            // Actualizar estado del jugador (tiene pelota)
                            Jugador jug = estado.getJugador(msg.getIdJugador());
                            if (jug != null) jug.setTienePelota(true);
                        }
                        break;
                    case MOVER_PELOTA:
                        Pelota pMover = estado.getPelota(msg.getIdObjeto());
                        if (pMover != null && pMover.getIdJugador() == msg.getIdJugador()) {
                            pMover.setX(msg.getX());
                            pMover.setY(msg.getY());
                        }
                        break;
                    case SOLTAR_PELOTA:
                        Pelota pSoltar = estado.getPelota(msg.getIdObjeto());
                        if (pSoltar != null && pSoltar.getIdJugador() == msg.getIdJugador()) {
                            pSoltar.setIdJugador(-1);
                            pSoltar.setVx(msg.getVx());
                            pSoltar.setVy(msg.getVy());
                            Jugador jug = estado.getJugador(msg.getIdJugador());
                            if (jug != null) jug.setTienePelota(false);
                        }
                        break;
                    default:
                        System.out.println("[Servidor] Tipo no manejado: " + msg.getTipo());
                }

                // Verificar goles después de cada actualización
                verificarGoles();

                // Broadcast periódico cada 50 ms
                long ahora = System.currentTimeMillis();
                if (ahora - ultimoBroadcast > 50) {
                    broadcastEstado();
                    ultimoBroadcast = ahora;
                }

            } catch (java.net.SocketTimeoutException e) {
                // timeout normal
            } catch (Exception e) {
                if (ejecutando) e.printStackTrace();
            }
        }
        socket.close();
    }

    private void verificarGoles() {
        for (Pelota p : estado.getPelotas().values()) {
            // Solo si la pelota está libre (no la tiene un jugador)
            if (p.getIdJugador() != -1) continue;

            for (Zona zona : estado.getZonas().values()) {
                if (zona.contienePunto(p.getX(), p.getY())) {
                    Jugador jugador = estado.getJugador(zona.getIdJugador());
                    if (jugador != null) {
                        jugador.sumarPuntaje(10);
                        System.out.println("⚽ GOL de " + jugador.getNombre() + "! Puntaje: " + jugador.getPuntaje());
                    }
                    // Reiniciar pelota en el centro
                    p.setX(512);
                    p.setY(384);
                    p.setVx(0);
                    p.setVy(0);
                    p.setIdJugador(-1);
                    break; // una pelota solo puede hacer un gol a la vez
                }
            }
        }
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
        // Jugadores: id,nombre,x,y,puntaje,avatarId,tienePelota;
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
        // Pelotas: id,x,y,vx,vy,idJugador;
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

    public void detener() {
        ejecutando = false;
        interrupt();
    }
}
>>>>>>> Stashed changes
