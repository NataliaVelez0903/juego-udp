package com.proyecto.juegoudp.modelo;
//ficha
public class Ficha {
    private int id;// identificador de la ficha
    private float x;// posicion en el eje x en la pantalla
    private float y;// posicion en el eje y en la pantalla
    private boolean arrastrando;// indica si alguien esta moviendo la ficha
    private int jugadorId;// quien controla la ficha

    public Ficha(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.arrastrando = false;
        this.jugadorId = -1;
    }

    // Getters

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isArrastrando() {
        return arrastrando;
    }

    public int getJugadorId() {
        return jugadorId;
    }

    // Setters

    public void setX(float x) {
        this.x = x;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setArrastrando(boolean arrastrando) {
        this.arrastrando = arrastrando;
    }

    public void setJugadorId(int jugadorId) {
        this.jugadorId = jugadorId;
    }
}
