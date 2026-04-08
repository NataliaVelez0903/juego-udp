package com.proyecto.juegoudp.red;

/**
 * Lectura mínima del {@code STATE|} para la sala de espera (sin la pantalla de juego completa).
 */
public final class AnalizadorEstadoUdp {
    private AnalizadorEstadoUdp() {}

    /** Cuenta registros de jugador en el segmento (separados por {@code ;}). */
    public static int contarJugadores(String segmentoJugadores) {
        if (segmentoJugadores == null || segmentoJugadores.isEmpty()) {
            return 0;
        }
        int cantidad = 0;
        for (String trozo : segmentoJugadores.split(";")) {
            if (!trozo.isEmpty()) {
                cantidad++;
            }
        }
        return cantidad;
    }

    /**
     * Formato actual: {@code STATE|seq|req|tiempo|jugadores|pelotas}.
     * Compatibilidad con formatos anteriores con menos campos.
     */
    public static int leerJugadoresRequeridos(String estadoSerializado) {
        if (estadoSerializado == null || !estadoSerializado.startsWith("STATE|")) {
            return 2;
        }
        String[] partes = estadoSerializado.split("\\|", 6);
        if (partes.length >= 5) {
            try {
                return Integer.parseInt(partes[2]);
            } catch (NumberFormatException e) {
                return 2;
            }
        }
        return 2;
    }

    public static String segmentoJugadores(String estadoSerializado) {
        if (estadoSerializado == null || !estadoSerializado.startsWith("STATE|")) {
            return "";
        }
        String[] partes = estadoSerializado.split("\\|", 6);
        if (partes.length >= 6) {
            return partes[4];
        }
        if (partes.length == 5) {
            return partes[3];
        }
        if (partes.length == 4) {
            return partes[2];
        }
        if (partes.length == 3) {
            return partes[1];
        }
        return "";
    }

    public static int leerTiempoRestanteSegundos(String estadoSerializado) {
        if (estadoSerializado == null || !estadoSerializado.startsWith("STATE|")) {
            return 0;
        }
        String[] partes = estadoSerializado.split("\\|", 6);
        if (partes.length >= 6) {
            try {
                return Integer.parseInt(partes[3]);
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }
}
