package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import java.net.InetAddress;
import java.util.Map;

/**
 * Define el contrato para el procesamiento de mensajes recibidos
 * por el servidor UDP.
 *
 * Esta interfaz abstrae la lógica encargada de interpretar
 * y aplicar cada mensaje entrante dentro del contexto del servidor,
 * incluyendo acciones relacionadas con la unión de jugadores,
 * la actualización del estado del juego y la aplicación
 * de reglas de la partida.
 *
 * Su propósito es desacoplar la recepción de mensajes
 * de la lógica específica que debe ejecutarse en respuesta
 * a cada uno de ellos.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public interface IProcesadorMensajesServidor {

    /**
     * Procesa un mensaje recibido por el servidor y aplica
     * las acciones correspondientes sobre el estado de la partida
     * y las estructuras de clientes conectados.
     *
     * @param mensaje mensaje recibido desde un cliente
     * @param ip dirección IP del cliente emisor
     * @param puerto puerto del cliente emisor
     * @param claveCliente clave única que identifica al cliente
     * @param estadoJuego estado actual del juego en el servidor
     * @param jugadorPorCliente asociación entre cliente y jugador asignado
     * @param clientes mapa de clientes conectados y su información pública
     * @param envio mecanismo utilizado por el servidor para enviar respuestas
     * @param partidaTerminada indica si la partida ya ha finalizado
     */
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
