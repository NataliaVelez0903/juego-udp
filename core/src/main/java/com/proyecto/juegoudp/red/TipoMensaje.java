
package com.proyecto.juegoudp.red;

public enum TipoMensaje {
    UNIRSE,
    /** Respuesta del servidor con el id asignado al cliente (idJugador; datos opcional si sala llena). */
    TU_ID,
    MOVER_JUGADOR,
    TOMAR_PELOTA,
    MOVER_PELOTA,
    SOLTAR_PELOTA,
    STATE
}