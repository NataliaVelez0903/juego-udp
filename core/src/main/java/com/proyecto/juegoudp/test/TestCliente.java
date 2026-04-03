package com.proyecto.juegoudp.test;

import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.TipoMensaje;

public class TestCliente {
    public static void main(String[] args) throws Exception {
        ClienteUDP cliente = new ClienteUDP("localhost");
        cliente.setCallbackMensaje(msg -> {
            System.out.println("Cliente recibió: " + msg.getTipo() + " - " + msg.getDatos());
        });
        for (int i = 0; i < 10; i++) {
            Mensaje msg = new Mensaje(TipoMensaje.UNIRSE, 0, 0, 0, 0, 0, 0, "Prueba");
            cliente.enviarMensaje(msg);
            System.out.println("Enviado UNIRSE");
            Thread.sleep(1000);
        }
        System.in.read();
        cliente.cerrar();
    }
}