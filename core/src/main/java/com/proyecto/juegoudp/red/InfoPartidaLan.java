package com.proyecto.juegoudp.red;

/**
 * Representa una partida detectada en la red local.
 */
public class InfoPartidaLan {
    private final String ipHost;
    private final String nombreHost;
    private final int jugadoresRequeridos;
    private final int tiempoSegundos;

    public InfoPartidaLan(String ipHost, String nombreHost, int jugadoresRequeridos, int tiempoSegundos) {
        this.ipHost = ipHost;
        this.nombreHost = nombreHost;
        this.jugadoresRequeridos = jugadoresRequeridos;
        this.tiempoSegundos = tiempoSegundos;
    }

    public String getIpHost() {
        return ipHost;
    }

    public String getNombreHost() {
        return nombreHost;
    }

    public int getJugadoresRequeridos() {
        return jugadoresRequeridos;
    }

    public int getTiempoSegundos() {
        return tiempoSegundos;
    }
}

