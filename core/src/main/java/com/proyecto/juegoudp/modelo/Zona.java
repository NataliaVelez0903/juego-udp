package com.proyecto.juegoudp.modelo;

/**
 * Representa una zona de anotación dentro del campo de juego.
 *
 * Esta clase modela un área rectangular asociada a un jugador,
 * utilizada para detectar cuándo una pelota entra en la región
 * correspondiente y, por tanto, se produce una anotación.
 *
 * Cada zona posee un identificador único, el identificador del
 * jugador al que pertenece, una posición central dentro del escenario
 * y sus dimensiones de ancho y alto.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class Zona {

    /**
     * Identificador único de la zona.
     */
    private int id;

    /**
     * Identificador del jugador al que pertenece esta zona.
     */
    private int idJugador;

    /**
     * Coordenada horizontal del centro de la zona.
     */
    private float x;

    /**
     * Coordenada vertical del centro de la zona.
     */
    private float y;

    /**
     * Ancho de la zona rectangular.
     */
    private float ancho;

    /**
     * Alto de la zona rectangular.
     */
    private float alto;

    /**
     * Construye una nueva zona de anotación con su información básica.
     *
     * @param id el identificador único de la zona
     * @param idJugador el identificador del jugador al que pertenece la zona
     * @param x la coordenada horizontal del centro de la zona
     * @param y la coordenada vertical del centro de la zona
     * @param ancho el ancho de la zona
     * @param alto el alto de la zona
     */
    public Zona(int id, int idJugador, float x, float y, float ancho, float alto) {
        this.id = id;
        this.idJugador = idJugador;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    /**
     * Verifica si un punto dado se encuentra dentro de los límites
     * de la zona rectangular.
     *
     * La comprobación se realiza tomando como referencia el centro
     * de la zona y sus dimensiones de ancho y alto.
     *
     * @param px la coordenada horizontal del punto a evaluar
     * @param py la coordenada vertical del punto a evaluar
     * @return {true} si el punto está dentro de la zona;
     *         {false} en caso contrario
     */
    public boolean contienePunto(float px, float py) {
        return px >= x - ancho / 2 && px <= x + ancho / 2 &&
            py >= y - alto / 2 && py <= y + alto / 2;
    }

    /**
     * Obtiene el identificador de la zona.
     *
     * @return el identificador de la zona
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el identificador del jugador al que pertenece la zona.
     *
     * @return el identificador del jugador asociado
     */
    public int getIdJugador() {return idJugador;}

    /**
     * Obtiene la coordenada horizontal del centro de la zona.
     *
     * @return la coordenada horizontal de la zona
     */
    public float getX() {return x;}

    /**
     * Obtiene la coordenada vertical del centro de la zona.
     *
     * @return la coordenada vertical de la zona
     */
    public float getY() {return y;}

    /**
     * Obtiene el ancho de la zona.
     *
     * @return el ancho de la zona
     */
    public float getAncho() {return ancho;}

    /**
     * Obtiene el alto de la zona.
     *
     * @return el alto de la zona
     */
    public float getAlto() {return alto;}
}
