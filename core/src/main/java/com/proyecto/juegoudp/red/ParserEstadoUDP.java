package com.proyecto.juegoudp.red;

/**
 * Lectura mínima del snapshot STATE para lobby (sin depender de pantalla de juego).
 */
public final class ParserEstadoUDP {
    private ParserEstadoUDP() {}

    /** Cuenta registros de jugador en el segmento (separados por ;). */
    public static int contarJugadores(String segmentoJugadores) {
        if (segmentoJugadores == null || segmentoJugadores.isEmpty()) return 0;
        int n = 0;
        for (String s : segmentoJugadores.split(";")) {
            if (!s.isEmpty()) n++;
        }
        return n;
    }

    /**
     * Formato actual: {@code STATE|seq|req|jugadores|pelotas} (5+ segmentos al partir por | con límite).
     * Formato previo: {@code STATE|seq|jugadores|pelotas}
     * Legado: {@code STATE|jugadores|pelotas}
     */
    public static int leerJugadoresRequeridos(String estado) {
        if (estado == null || !estado.startsWith("STATE|")) return 2;
        String[] p = estado.split("\\|", 6);
        if (p.length >= 5) {
            try {
                return Integer.parseInt(p[2]);
            } catch (NumberFormatException e) {
                return 2;
            }
        }
        return 2;
    }

    public static String segmentoJugadores(String estado) {
        if (estado == null || !estado.startsWith("STATE|")) return "";
        String[] p = estado.split("\\|", 6);
        if (p.length >= 5) return p[3];
        if (p.length == 4) return p[2];
        if (p.length == 3) return p[1];
        return "";
    }
}
