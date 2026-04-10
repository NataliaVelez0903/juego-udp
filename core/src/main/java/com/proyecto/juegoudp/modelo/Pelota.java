package com.proyecto.juegoudp.modelo;

/**
 * Representa una pelota dentro de la partida.
 *
 * Esta clase modela uno de los elementos interactivos principales
 * del juego. Cada pelota posee un identificador único, una posición
 * dentro del escenario, una velocidad en los ejes horizontal y vertical,
 * y un identificador del jugador que la controla en un momento dado.
 *
 * Cuando la pelota no está asociada a ningún jugador, el atributo
 * correspondiente se establece en {-1}, indicando que se encuentra
 * libre dentro del campo de juego.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class Pelota {

    /**
     * Identificador único de la pelota.
     */
    private int id;

    /**
     * Identificador del jugador que controla actualmente la pelota.
     *
     * El valor {-1} indica que la pelota no está siendo
     * controlada por ningún jugador.
     */
    private int idJugador;

    /**
     * Posición horizontal actual de la pelota.
     */
    private float x;

    /**
     * Posición vertical actual de la pelota.
     */
    private float y;

    /**
     * Velocidad horizontal de la pelota.
     */
    private float vx;

    /**
     * Velocidad vertical de la pelota.
     */
    private float vy;

    /**
     * Construye una nueva pelota con un identificador y una posición inicial.
     *
     * Al crearse, la pelota inicia sin jugador asociado, por lo que
     * su controlador se establece en {-1}. Además, su velocidad
     * inicial en ambos ejes es igual a cero.
     *
     * @param id el identificador único de la pelota
     * @param x la posición horizontal inicial
     * @param y la posición vertical inicial
     */
    public Pelota(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.idJugador = -1;
        this.vx = 0;
        this.vy = 0;
    }

    /**
     * Obtiene el identificador de la pelota.
     *
     * @return el identificador de la pelota
     */
    public int getId() {return id;}

    /**
     * Obtiene el identificador del jugador que controla la pelota.
     *
     * @return el identificador del jugador que controla la pelota,
     *         o {-1} si la pelota está libre
     */
    public int getIdJugador() {return idJugador;}

    /**
     * Establece el identificador del jugador que pasa a controlar la pelota.
     *
     * @param idJugador el identificador del jugador controlador,
     *                  o {-1} si la pelota queda libre
     */
    public void setIdJugador(int idJugador) {this.idJugador = idJugador;}

    /**
     * Obtiene la posición horizontal actual de la pelota.
     *
     * @return la coordenada horizontal de la pelota
     */
    public float getX() {return x;}

    /**
     * Establece la posición horizontal de la pelota.
     *
     * @param x la nueva coordenada horizontal
     */
    public void setX(float x) {this.x = x;}

    /**
     * Obtiene la posición vertical actual de la pelota.
     *
     * @return la coordenada vertical de la pelota
     */
    public float getY() {return y;}

    /**
     * Establece la posición vertical de la pelota.
     *
     * @param y la nueva coordenada vertical
     */
    public void setY(float y) {this.y = y;}

    /**
     * Obtiene la velocidad horizontal actual de la pelota.
     *
     * @return la velocidad en el eje horizontal
     */
    public float getVx() {return vx;}

    /**
     * Establece la velocidad horizontal de la pelota.
     *
     * @param vx la nueva velocidad en el eje horizontal
     */
    public void setVx(float vx) {this.vx = vx;}

    /**
     * Obtiene la velocidad vertical actual de la pelota.
     *
     * @return la velocidad en el eje vertical
     */
    public float getVy() {return vy;}

    /**
     * Establece la velocidad vertical de la pelota.
     *
     * @param vy la nueva velocidad en el eje vertical
     */
    public void setVy(float vy) {this.vy = vy;}
}
