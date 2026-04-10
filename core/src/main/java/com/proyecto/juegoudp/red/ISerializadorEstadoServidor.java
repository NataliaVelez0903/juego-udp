package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.EstadoJuego;

/**
 * Define el contrato para la serialización del estado del juego
 * en el servidor.
 *
 * Esta interfaz permite convertir el EstadoJuego en una
 * representación textual que puede ser enviada a los clientes
 * a través de la red.
 *
 * Su propósito es desacoplar la lógica de generación del estado
 * serializado del resto del servidor, permitiendo diferentes
 * estrategias de serialización según las necesidades del sistema.
 *
 * El resultado de la serialización suele utilizarse para construir
 * mensajes del tipo STATE|... que contienen la información
 * actual de la partida.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public interface ISerializadorEstadoServidor {

    /**
     * Convierte el estado actual del juego en una cadena serializada.
     *
     * Esta cadena incluye información relevante como el número
     * de secuencia, la cantidad de jugadores requeridos, el tiempo
     * restante de la partida y los datos completos del estado del juego.
     *
     * @param secuencia número de secuencia del estado
     * @param jugadoresRequeridos cantidad de jugadores necesarios para la partida
     * @param tiempoRestanteSegundos tiempo restante de la partida en segundos
     * @param estadoJuego estado actual del juego en el servidor
     * @return una cadena que representa el estado serializado del juego
     */
    String serializar(long secuencia, int jugadoresRequeridos, int tiempoRestanteSegundos, EstadoJuego estadoJuego);
}
