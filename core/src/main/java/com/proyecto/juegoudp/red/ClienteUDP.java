package com.proyecto.juegoudp.red;
<<<<<<< Updated upstream
// jugador normal, envia acciones, recibe estados
public class ClienteUDP {
=======

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.function.Consumer;

public class ClienteUDP {
    private DatagramSocket socket;
    private InetAddress servidorIP;
    private int puertoServidor = 5000;
    private boolean ejecutando;
    private Consumer<String> callbackEstado;
    private Consumer<Mensaje> callbackMensaje;

    public ClienteUDP(String ipServidor) throws Exception {
        socket = new DatagramSocket();
        servidorIP = InetAddress.getByName(ipServidor);
        ejecutando = true;
        iniciarEscucha();
        System.out.println("[Cliente] Conectado a " + ipServidor + ":" + puertoServidor);
    }

    private void iniciarEscucha() {
        new Thread(() -> {
            byte[] buffer = new byte[8192];
            while (ejecutando) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String texto = new String(packet.getData(), 0, packet.getLength());
                    if (texto.startsWith("STATE|")) {
                        if (callbackEstado != null) callbackEstado.accept(texto);
                    } else {
                        Mensaje msg = Mensaje.deserializar(texto);
                        if (msg != null && callbackMensaje != null) callbackMensaje.accept(msg);
                    }
                } catch (Exception e) {
                    if (ejecutando) e.printStackTrace();
                }
            }
        }).start();
    }

    public void enviarMensaje(Mensaje msg) {
        try {
            String texto = msg.serializar();
            System.out.println("[Cliente] Enviando: " + texto);
            byte[] data = texto.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, servidorIP, puertoServidor);
            socket.send(packet);
        } catch (Exception e) {
            System.err.println("[Cliente] Error enviando: " + e.getMessage());
        }
    }

    public void setCallbackEstado(Consumer<String> cb) { this.callbackEstado = cb; }
    public void setCallbackMensaje(Consumer<Mensaje> cb) { this.callbackMensaje = cb; }
    public void cerrar() { ejecutando = false; socket.close(); }
>>>>>>> Stashed changes
}
