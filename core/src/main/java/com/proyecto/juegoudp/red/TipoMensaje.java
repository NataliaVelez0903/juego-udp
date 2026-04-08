package com.proyecto.juegoudp.red;

/**
 * Tipos de mensajes UDP entre cliente y servidor.
 */
public enum TipoMensaje {
    UNIRSE,
    /** Respuesta del servidor con el id asignado al cliente (idJugador; datos opcional si sala llena). */
    TU_ID,
    MOVER_JUGADOR,
    TOMAR_PELOTA,
    MOVER_PELOTA,
    SOLTAR_PELOTA,
    /** Activación de habilidad del avatar. x/y opcional (teleport), idObjeto puede ser objetivo (jugador). */
    USAR_PODER,
    STATE
}