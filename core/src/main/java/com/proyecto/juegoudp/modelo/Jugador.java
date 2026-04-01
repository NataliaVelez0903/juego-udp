package com.proyecto.juegoudp.modelo;


/**
 * Representa un jugador dentro del juego.
 *
 * Responsabilidades:
 * - Identificar al jugador
 * - Almacenar su nombre
 * - Gestionar su puntaje durante la partida
 *
 * Nota:
 * Esta clase forma parte del modelo (solo datos),
 * no contiene lógica de juego compleja.
 */
public class Jugador {
    private int id;
    private String nombre;
    private int puntaje;

    public Jugador(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.puntaje = 0;
    }

    // Getters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    /**
     * Incrementa el puntaje del jugador en 1.
     *
     * Se utiliza cuando el jugador captura una ficha.
     */
    public void sumarPunto (){
        this.puntaje++;
    }
}
