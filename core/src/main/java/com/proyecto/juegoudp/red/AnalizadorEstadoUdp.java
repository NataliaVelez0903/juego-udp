package com.proyecto.juegoudp.red;

/**
 * Proporciona utilidades para interpretar información básica
 * del estado serializado recibido por UDP.
 *
 * Esta clase permite extraer datos mínimos del mensaje de estado
 * utilizado en la sala de espera, sin necesidad de procesar
 * la estructura completa de la partida.
 *
 * Su propósito es facilitar la lectura de información como
 * la cantidad de jugadores conectados, la cantidad de jugadores
 * requeridos, el segmento de jugadores y el tiempo restante,
 * a partir de una cadena con formato {STATE|...}.
 *
 * Al ser una clase de utilidad, no está pensada para ser instanciada.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public final class AnalizadorEstadoUdp {

    /**
     * Constructor privado para evitar la creación de instancias
     * de esta clase utilitaria.
     */
    private AnalizadorEstadoUdp() {}

    /**
     * Cuenta la cantidad de jugadores presentes en un segmento
     * serializado de jugadores.
     *
     * El conteo se realiza separando los registros por punto y coma
     * y contabilizando únicamente los fragmentos no vacíos.
     *
     * @param segmentoJugadores cadena que contiene los registros
     *                          de jugadores separados por;
     * @return la cantidad de jugadores detectados en el segmento
     */
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
     * Lee la cantidad de jugadores requeridos desde un estado serializado.
     *
     * El formato actual esperado es:
     * {STATE|seq|req|tiempo|jugadores|pelotas}
     *
     * Este método también contempla compatibilidad con formatos
     * anteriores que contienen una menor cantidad de campos.
     *
     * Si el valor no puede leerse correctamente, retorna 2
     * como valor predeterminado.
     *
     * @param estadoSerializado cadena que representa el estado serializado
     * @return la cantidad de jugadores requeridos leída desde el estado,
     *         o 2 si no puede determinarse
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

    /**
     * Extrae el segmento correspondiente a los jugadores
     * desde un estado serializado.
     *
     * Este método contempla distintos formatos de compatibilidad
     * según la cantidad de partes presentes en la cadena.
     *
     * @param estadoSerializado cadena que representa el estado serializado
     * @return el segmento correspondiente a los jugadores,
     *         o una cadena vacía si no puede obtenerse
     */
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

    /**
     * Lee el tiempo restante de la partida desde un estado serializado.
     *
     * Si el tiempo no puede interpretarse correctamente, retorna 0
     * como valor predeterminado.
     *
     * @param estadoSerializado cadena que representa el estado serializado
     * @return el tiempo restante en segundos,
     *         o 0 si no puede determinarse
     */
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
