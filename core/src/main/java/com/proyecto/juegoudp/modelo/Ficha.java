package com.proyecto.juegoudp.modelo;

/**
 * Representa una ficha dentro del juego.
 *
 * Responsabilidades:
 * - Mantener su posición en el tablero
 * - Indicar si está siendo arrastrada
 * - Identificar qué jugador la controla
 * - Indicar si ya fue capturada y por quién
 *
 * Nota:
 * Esta clase solo contiene datos (modelo), no lógica del juego,
 * siguiendo el principio de separación de responsabilidades (SOLID).
 */
public class Ficha {

    private int id;// identificador de la ficha
    private float x;// posicion en el eje x en la pantalla
    private float y;// posicion en el eje y en la pantalla
    private boolean arrastrando;// indica si alguien esta moviendo la ficha
    private int jugadorId;// quien controla la ficha

    private boolean capturada;
    private int capturadaPor;

    public Ficha(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.arrastrando = false;
        this.jugadorId = -1; // se inicia así para mostrar que nadie lo esta controlando
        this.capturada = false;
        this.capturadaPor = -1;
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

    public int getCapturadaPor() {
        return capturadaPor;
    }
    public boolean isCapturada(){
        return capturada;
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

    public void setCapturada(boolean capturada) {
        this.capturada = capturada;
    }

    public void setCapturadaPor(int capturadaPor) {
        this.capturadaPor = capturadaPor;
    }
}
