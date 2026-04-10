package com.proyecto.juegoudp.red;

/**
 * Enumera los tipos de mensajes intercambiados entre cliente y servidor
 * mediante el protocolo UDP.
 *
 * Cada valor representa una acción, evento o tipo de información
 * utilizado durante la comunicación de la partida, como la unión
 * de jugadores, el movimiento, la interacción con pelotas
 * o la transmisión del estado general del juego.
 *
 * Su propósito es estandarizar los tipos de mensajes reconocidos
 * por el sistema de red del juego.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public enum TipoMensaje {

    /**
     * Solicita la unión de un cliente a la partida.
     */
    UNIRSE,

    /**
     * Respuesta del servidor que informa el identificador asignado
     * al cliente.
     *
     * También puede incluir información adicional en el campo de datos,
     * por ejemplo, cuando la sala está llena.
     */
    TU_ID,

    /**
     * Indica el movimiento de un jugador dentro del escenario.
     */
    MOVER_JUGADOR,

    /**
     * Indica que un jugador intenta tomar una pelota.
     */
    TOMAR_PELOTA,

    /**
     * Indica el movimiento de una pelota controlada por un jugador.
     */
    MOVER_PELOTA,

    /**
     * Indica que un jugador suelta una pelota,
     * incluyendo su velocidad resultante.
     */
    SOLTAR_PELOTA,

    /**
     * Indica la activación de una habilidad especial o poder del avatar.
     *
     * Las coordenadas pueden ser opcionales, por ejemplo en habilidades
     * de teletransporte, y el identificador de objeto puede utilizarse
     * para señalar un objetivo.
     */
    USAR_PODER,

    /**
     * Representa un mensaje que contiene el estado general
     * de la partida.
     */
    STATE
}
