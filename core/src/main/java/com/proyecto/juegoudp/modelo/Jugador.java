package com.proyecto.juegoudp.modelo;

/**
 * Representa a un jugador dentro de la partida.
 *
 * Esta clase modela la información principal de cada participante
 * del juego, incluyendo su identificador, nombre, avatar seleccionado,
 * puntaje acumulado, posición dentro del escenario y si actualmente
 * posee la pelota.
 *
 * Su propósito es encapsular el estado individual del jugador
 * durante la ejecución de la partida, permitiendo consultar y
 * actualizar sus datos conforme avanza el juego.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class Jugador {

    /**
     * Identificador único del jugador.
     */
    private int id;

    /**
     * Identificador del avatar asignado al jugador.
     */
    private int avatarId;

    /**
     * Puntaje acumulado por el jugador durante la partida.
     */
    private int puntaje;

    /**
     * Nombre del jugador.
     */
    private String nombre;

    /**
     * Posición horizontal actual del jugador dentro del escenario.
     */
    private float x;

    /**
     * Posición vertical actual del jugador dentro del escenario.
     */
    private float y;

    /**
     * Indica si el jugador tiene actualmente la pelota en su poder.
     */
    private boolean tienePelota;

    /**
     * Construye un nuevo jugador con su información básica.
     *
     * <p>Al crearse, el jugador inicia con puntaje en cero, sin
     * posesión de la pelota y con una posición inicial predeterminada
     * en las coordenadas {@code (512, 384)}.</p>
     *
     * @param id el identificador único del jugador
     * @param nombre el nombre del jugador
     * @param avatarId el identificador del avatar seleccionado
     */
    public Jugador(int id, String nombre, int avatarId) {
        this.id = id;
        this.nombre = nombre;
        this.avatarId = avatarId;
        this.puntaje = 0;
        this.tienePelota = false;
        this.x = 512;
        this.y = 384;
    }

    /**
     * Obtiene el identificador del jugador.
     *
     * @return el identificador del jugador
     */
    public int getId() {return id;}

    /**
     * Establece el identificador del jugador.
     *
     * @param id el nuevo identificador del jugador
     */
    public void setId(int id) {this.id = id;}

    /**
     * Obtiene el identificador del avatar del jugador.
     *
     * @return el identificador del avatar
     */
    public int getAvatarId() {return avatarId;}

    /**
     * Establece el identificador del avatar del jugador.
     *
     * @param avatarId el nuevo identificador del avatar
     */
    public void setAvatarId(int avatarId) {this.avatarId = avatarId;}

    /**
     * Obtiene el puntaje actual del jugador.
     *
     * @return el puntaje acumulado
     */
    public int getPuntaje() {return puntaje;}

    /**
     * Establece el puntaje del jugador.
     *
     * @param puntaje el nuevo puntaje del jugador
     */
    public void setPuntaje(int puntaje) {this.puntaje = puntaje;}

    /**
     * Incrementa el puntaje actual del jugador sumando la cantidad indicada.
     *
     * @param puntos la cantidad de puntos que se agregarán al puntaje actual
     */
    public void sumarPuntaje(int puntos) {this.puntaje += puntos;}

    /**
     * Disminuye el puntaje del jugador restando la cantidad indicada,
     * sin permitir que el valor final sea negativo.
     *
     * @param puntos cantidad de puntos a restar
     */
    public void restarPuntaje(int puntos) { this.puntaje = Math.max(0, this.puntaje - puntos); }

    /**
     * Obtiene el nombre del jugador.
     *
     * @return el nombre del jugador
     */
    public String getNombre() {return nombre;}

    /**
     * Establece el nombre del jugador.
     *
     * @param nombre el nuevo nombre del jugador
     */
    public void setNombre(String nombre) {this.nombre = nombre;}

    /**
     * Obtiene la posición horizontal del jugador.
     *
     * @return la coordenada horizontal actual
     */
    public float getX() {return x;}

    /**
     * Establece la posición horizontal del jugador.
     *
     * @param x la nueva coordenada horizontal
     */
    public void setX(float x) {this.x = x;}

    /**
     * Obtiene la posición vertical del jugador.
     *
     * @return la coordenada vertical actual
     */
    public float getY() {return y;}

    /**
     * Establece la posición vertical del jugador.
     *
     * @param y la nueva coordenada vertical
     */
    public void setY(float y) {this.y = y;}

    /**
     * Indica si el jugador tiene actualmente la pelota.
     *
     * @return {true} si el jugador posee la pelota;
     *         {false} en caso contrario
     */
    public boolean isTienePelota() {return tienePelota;}

    /**
     * Define si el jugador tiene la pelota en su poder.
     *
     * @param tienePelota {true} si el jugador posee la pelota;
     *                    {false} en caso contrario
     */
    public void setTienePelota(boolean tienePelota) {this.tienePelota = tienePelota;}
}
