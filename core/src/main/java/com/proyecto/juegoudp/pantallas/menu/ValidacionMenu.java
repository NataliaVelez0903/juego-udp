package com.proyecto.juegoudp.pantallas.menu;

import com.proyecto.juegoudp.utilidades.Constantes;

/**
 * Realiza la validación de los datos ingresados en el menú principal
 * para crear o unirse a una partida.
 *
 * Esta clase se encarga de verificar los campos necesarios según
 * el modo seleccionado por el usuario, ya sea como anfitrión
 * o como cliente.
 *
 * En el caso del anfitrión, valida el nombre del jugador,
 * la cantidad de jugadores requeridos y el tiempo de partida.
 * En el caso del cliente, valida el nombre del jugador
 * y la dirección IP del anfitrión.
 *
 * Su propósito es centralizar las reglas básicas de validación
 * de entrada antes de iniciar el flujo de conexión del juego.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class ValidacionMenu {

    /**
     * Representa el resultado de una validación realizada en el menú.
     *
     * Esta clase permite encapsular tanto el resultado lógico
     * de la validación como los datos procesados o el mensaje
     * de error correspondiente.
     */
    public static class Resultado {

        /**
         * Indica si la validación fue exitosa.
         */
        public final boolean ok;

        /**
         * Mensaje de error asociado a la validación.
         *
         * Si la validación fue exitosa, este valor será una cadena vacía.
         */
        public final String error;

        /**
         * Cantidad de jugadores validada.
         */
        public final int jugadores;

        /**
         * Tiempo validado de la partida, expresado en segundos.
         */
        public final int tiempo;

        /**
         * Construye un nuevo resultado de validación.
         *
         * @param ok indica si la validación fue exitosa
         * @param error mensaje de error asociado
         * @param jugadores cantidad de jugadores validada
         * @param tiempo tiempo validado de la partida
         */
        private Resultado(boolean ok, String error, int jugadores, int tiempo) {
            this.ok = ok;
            this.error = error;
            this.jugadores = jugadores;
            this.tiempo = tiempo;
        }

        /**
         * Crea un resultado de validación fallido.
         *
         * @param mensaje mensaje que describe el error encontrado
         * @return un resultado con estado de error
         */
        public static Resultado error(String mensaje) {
            return new Resultado(false, mensaje, 0, 0);
        }

        /**
         * Crea un resultado de validación exitoso.
         *
         * @param jugadores cantidad de jugadores validada
         * @param tiempo tiempo validado de la partida
         * @return un resultado válido con los datos procesados
         */
        public static Resultado ok(int jugadores, int tiempo) {
            return new Resultado(true, "", jugadores, tiempo);
        }
    }

    /**
     * Valida los datos ingresados para crear una partida como anfitrión.
     *
     * Este método verifica que el nombre no esté vacío, que la cantidad
     * de jugadores y el tiempo sean valores numéricos válidos
     * y que cumplan las reglas mínimas definidas por la aplicación.
     *
     * También normaliza la cantidad de jugadores a 2 o 4,
     * limita el máximo permitido según las constantes del sistema
     * y asegura que el tiempo mínimo de partida sea de 30 segundos.
     *
     * @param nombre nombre ingresado por el jugador
     * @param jugadoresTxt texto correspondiente a la cantidad de jugadores
     * @param tiempoTxt texto correspondiente al tiempo de partida
     * @return el resultado de la validación con los datos procesados
     *         o con el error correspondiente
     */
    public Resultado validarHost(String nombre, String jugadoresTxt, String tiempoTxt) {
        String nom = (nombre == null) ? "" : nombre.trim();
        if (nom.isEmpty()) return Resultado.error("Ingresa un nombre");

        int jugadores;
        int tiempo;
        try {
            jugadores = Integer.parseInt(jugadoresTxt.trim());
            tiempo = Integer.parseInt(tiempoTxt.trim());
        } catch (Exception e) {
            return Resultado.error("Número inválido");
        }

        if (jugadores <= 2) {
            jugadores = 2;
        } else {
            jugadores = 4;
        }
        if (jugadores > Constantes.MAX_JUGADORES) {
            jugadores = Constantes.MAX_JUGADORES;
        }
        if (tiempo < 30) tiempo = 30;

        return Resultado.ok(jugadores, tiempo);
    }

    /**
     * Valida los datos ingresados para unirse a una partida como cliente.
     *
     * Este método verifica que el nombre del jugador no esté vacío
     * y que la dirección IP del anfitrión haya sido ingresada.
     *
     * @param nombre nombre ingresado por el jugador
     * @param ip dirección IP ingresada para conectarse al anfitrión
     * @return una cadena vacía si la validación es correcta;
     *         en caso contrario, retorna el mensaje de error correspondiente
     */
    public String validarCliente(String nombre, String ip) {
        String nom = (nombre == null) ? "" : nombre.trim();
        if (nom.isEmpty()) return "Ingresa un nombre";
        String ipTxt = (ip == null) ? "" : ip.trim();
        if (ipTxt.isEmpty()) return "Ingresa IP del Host";
        return "";
    }
}
