package com.proyecto.juegoudp.modelo;

/**
 * Rectángulo de gol asociado a un jugador; se usa para detectar anotaciones.
 */
public class Zona {
    private int id, idJugador;
    private float x, y, ancho, alto;

    public Zona(int id, int idJugador, float x, float y, float ancho, float alto) {
        this.id = id;
        this.idJugador = idJugador;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }
    public boolean contienePunto(float px, float py) {
        return px >= x - ancho/2 && px <= x + ancho/2 &&
                py >= y - alto/2 && py <= y + alto/2;
    }
    public int getId() { return id; }
    public int getIdJugador() { return idJugador; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getAncho() { return ancho; }
    public float getAlto() { return alto; }
}