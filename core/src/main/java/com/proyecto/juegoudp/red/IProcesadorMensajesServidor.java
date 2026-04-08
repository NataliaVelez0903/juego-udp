package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import java.net.InetAddress;
import java.util.Map;

/**
 * Aplica la lógica del servidor a cada {@link Mensaje} entrante (reglas de juego y unión a la sala).
 */
public interface IProcesadorMensajesServidor {
    void procesar(
            Mensaje mensaje,
            InetAddress ip,
            int puerto,
            String claveCliente,
            EstadoJuego estadoJuego,
            Map<String, Integer> jugadorPorCliente,
            Map<String, ServidorUDP.ClienteInfoPublica> clientes,
            ServidorUDP.Envio envio,
            boolean partidaTerminada
    );
}

