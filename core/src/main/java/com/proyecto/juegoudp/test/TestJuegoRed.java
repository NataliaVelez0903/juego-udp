package com.proyecto.juegoudp.test;

import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.ServidorUDP;
import com.proyecto.juegoudp.red.TipoMensaje;

public class TestJuegoRed {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            // Modo servidor
            ServidorUDP servidor = new ServidorUDP();
            servidor.start();
            System.out.println("=== SERVIDOR CON MODELO CORRIENDO ===");
            System.out.println("Presiona Enter para detener...");
            System.in.read();
            servidor.detener();
        } else if (args[0].equalsIgnoreCase("cliente")) {
            String nombre = args.length > 1 ? args[1] : "JugadorX";
            ClienteUDP cliente = new ClienteUDP("localhost");
            cliente.setCallbackEstado(estado -> {
                System.out.println("Estado recibido (primeros 100 caracteres): " + estado.substring(0, Math.min(100, estado.length())));
            });
            Mensaje join = new Mensaje(TipoMensaje.UNIRSE, 0, 0, 0, 0, 0, 0, nombre);
            cliente.enviarMensaje(join);
            // Simular movimiento cada segundo
            int paso = 0;
            while (true) {
                Thread.sleep(1000);
                float x = 200 + paso * 20;
                float y = 300;
                Mensaje mover = new Mensaje(TipoMensaje.MOVER_JUGADOR, 1, 0, x, y, 0, 0, "");
                cliente.enviarMensaje(mover);
                System.out.println("Enviado movimiento a (" + x + ", " + y + ")");
                paso++;
                if (paso > 20) break;
            }
            System.out.println("Esperando más estados... (Enter para salir)");
            System.in.read();
            cliente.cerrar();
        }
    }
}