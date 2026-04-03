package com.proyecto.juegoudp.modelo;

public class Pelota {
    private int id, idJugador;
    private float x, y, vx, vy;

    public Pelota(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.idJugador = -1;
        this.vx = 0;
        this.vy = 0;
    }
    public int getId() { return id; }
    public int getIdJugador() { return idJugador; }
    public void setIdJugador(int idJugador) { this.idJugador = idJugador; }
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getVx() { return vx; }
    public void setVx(float vx) { this.vx = vx; }
    public float getVy() { return vy; }
    public void setVy(float vy) { this.vy = vy; }
}