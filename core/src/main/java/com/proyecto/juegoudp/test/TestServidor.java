package com.proyecto.juegoudp.test;

import com.proyecto.juegoudp.red.ServidorUDP;

public class TestServidor {
    public static void main(String[] args) throws Exception {
        ServidorUDP servidor = new ServidorUDP();
        servidor.start();
        System.out.println("Servidor corriendo. Presiona Enter para detener...");
        System.in.read();
        servidor.detener();
    }
}