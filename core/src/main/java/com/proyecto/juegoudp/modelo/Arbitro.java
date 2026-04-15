package com.proyecto.juegoudp.modelo;

/**
 * Representa un árbitro (obstáculo móvil) dentro de la partida.
 *
 * El árbitro se mueve de forma autónoma por la pantalla y penaliza
 * a los jugadores al ser tocado.
 */
public class Arbitro {

    private int id;
    private float x;
    private float y;
    private float vx;
    private float vy;

    public Arbitro(int id, float x, float y, float vx, float vy) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getVx() { return vx; }
    public void setVx(float vx) { this.vx = vx; }

    public float getVy() { return vy; }
    public void setVy(float vy) { this.vy = vy; }
}

