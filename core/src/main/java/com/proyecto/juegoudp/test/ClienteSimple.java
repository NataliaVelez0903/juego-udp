package com.proyecto.juegoudp.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteSimple {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress ip = InetAddress.getByName("localhost");
        byte[] data = "PRUEBA".getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, ip, 5000);
        socket.send(packet);
        System.out.println("Enviado: PRUEBA");
        socket.close();
    }
}