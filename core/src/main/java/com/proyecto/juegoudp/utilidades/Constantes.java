package com.proyecto.juegoudp.utilidades;

/**
 * Valores fijos de red y juego (puerto UDP, máximo de jugadores, frecuencia de envío).
 */
public final class Constantes {
    private Constantes() {}

    public static final int MAX_JUGADORES = 4;
    public static final int PUERTO_UDP = 5000;
    /** Envíos de posición por segundo (evita saturar UDP). */
    public static final float ENVIOS_RED_POR_SEGUNDO = 18f;
}
