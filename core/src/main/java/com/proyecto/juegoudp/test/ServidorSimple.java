package com.proyecto.juegoudp.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ServidorSimple {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(5000);
        System.out.println("Servidor simple escuchando en puerto 5000...");
        byte[] buffer = new byte[1024];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String texto = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Recibido: " + texto);
        }
    }
}