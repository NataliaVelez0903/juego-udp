package com.proyecto.juegoudp.red;

import java.util.Locale;

/**
 * Mensaje UDP serializable (tipo, identificadores, posición/velocidad y cadena de datos opcional).
 */
public class Mensaje {
    private TipoMensaje tipo;
    private int idJugador, idObjeto;
    private float x, y, vx, vy;
    private String datos;

    public Mensaje(TipoMensaje tipo, int idJugador, int idObjeto, float x, float y, float vx, float vy, String datos) {
        this.tipo = tipo;
        this.idJugador = idJugador;
        this.idObjeto = idObjeto;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.datos = datos;
    }

    public String serializar() {
        return String.format(Locale.US, "%s|%d|%d|%.2f|%.2f|%.2f|%.2f|%s",
                tipo.name(), idJugador, idObjeto, x, y, vx, vy, datos == null ? "" : datos);
    }

    public static Mensaje deserializar(String linea) {
        String[] p = linea.split("\\|", 8);
        if (p.length < 8) return null;
        try {
            TipoMensaje t = TipoMensaje.valueOf(p[0]);
            int idJ = Integer.parseInt(p[1]);
            int idO = Integer.parseInt(p[2]);
            float x = Float.parseFloat(p[3]);
            float y = Float.parseFloat(p[4]);
            float vx = Float.parseFloat(p[5]);
            float vy = Float.parseFloat(p[6]);
            String d = p[7];
            return new Mensaje(t, idJ, idO, x, y, vx, vy, d);
        } catch (Exception e) {
            return null;
        }
    }

    // Getters
    public TipoMensaje getTipo() { return tipo; }
    public int getIdJugador() { return idJugador; }
    public int getIdObjeto() { return idObjeto; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getVx() { return vx; }
    public float getVy() { return vy; }
    public String getDatos() { return datos; }
}