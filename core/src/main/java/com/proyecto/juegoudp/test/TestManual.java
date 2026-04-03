package com.proyecto.juegoudp.test;

import com.proyecto.juegoudp.red.ServidorUDP;

public class TestManual {
    public static void main(String[] args) throws Exception {
        new ServidorUDP().start();
        System.out.println("Servidor manual iniciado");
        Thread.sleep(30000);
    }
}