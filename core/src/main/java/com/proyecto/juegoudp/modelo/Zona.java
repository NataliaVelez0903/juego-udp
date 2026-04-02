package com.proyecto.juegoudp.modelo;


import com.badlogic.gdx.graphics.Texture;

/**
 * Representa una zona o base dentro del juego.
 *
 * Cada zona pertenece a un jugador y define el área donde
 * las fichas pueden ser capturadas para sumar puntos.
 *
 * Responsabilidades:
 * - Definir una región rectangular en el mapa
 * - Indicar a qué jugador pertenece
 * - Determinar si una ficha está dentro de la zona
 *
 * Nota:
 * Esta clase es parte del modelo (solo datos), no contiene lógica compleja.
 */
public class Zona {

    private int jugadorId;
    private float x;
    private float y;
    private float ancho;
    private float alto;

    public Zona(int jugadorId, float x, float y, float ancho, float alto) {

        this.jugadorId = jugadorId;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    // Getters

    public int getJugadorId() {
        return jugadorId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }

    /**
     * Verifica si un punto (px, py) está dentro de la zona.
     *
     * @param px posición X del punto
     * @param py posición Y del punto
     * @return true si el punto está dentro de la zona, false en caso contrario
     */

    public boolean contiene (float px, float py){
        return px >= x && px <= x + ancho && py >= y && py <= y + alto;
    }

}
