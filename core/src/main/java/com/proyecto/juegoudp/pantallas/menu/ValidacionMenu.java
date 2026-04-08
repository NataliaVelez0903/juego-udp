package com.proyecto.juegoudp.pantallas.menu;

import com.proyecto.juegoudp.utilidades.Constantes;

/**
 * Valida campos del menú para anfitrión y cliente (nombre, IP, jugadores requeridos y tiempo).
 */
public class ValidacionMenu {
    /**
     * Resultado de una validación: éxito con datos numéricos o error con mensaje.
     */
    public static class Resultado {
        public final boolean ok;
        public final String error;
        public final int jugadores;
        public final int tiempo;

        private Resultado(boolean ok, String error, int jugadores, int tiempo) {
            this.ok = ok;
            this.error = error;
            this.jugadores = jugadores;
            this.tiempo = tiempo;
        }

        public static Resultado error(String mensaje) {
            return new Resultado(false, mensaje, 0, 0);
        }

        public static Resultado ok(int jugadores, int tiempo) {
            return new Resultado(true, "", jugadores, tiempo);
        }
    }

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

        if (jugadores < 2) jugadores = 2;
        if (jugadores > Constantes.MAX_JUGADORES) jugadores = Constantes.MAX_JUGADORES;
        if (tiempo < 30) tiempo = 30;

        return Resultado.ok(jugadores, tiempo);
    }

    public String validarCliente(String nombre, String ip) {
        String nom = (nombre == null) ? "" : nombre.trim();
        if (nom.isEmpty()) return "Ingresa un nombre";
        String ipTxt = (ip == null) ? "" : ip.trim();
        if (ipTxt.isEmpty()) return "Ingresa IP del Host";
        return "";
    }
}

