package com.proyecto.juegoudp.modelo;

/**
 * Representa la configuración inicial de una partida.
 *
 * Esta clase almacena los parámetros seleccionados por el usuario
 * antes de iniciar la sesión de juego, como el número de jugadores,
 * el tiempo límite de la partida, el rol de conexión (host o jugador)
 * y la dirección IP del servidor.
 *
 * Tiene como finalidad centralizar la información de configuración necesaria
 * para preparar la creación o conexión a una partida multijugador.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class ConfiguracionPartida {

    /**
     * Cantidad de jugadores configurados para la partida.
     * Su valor predeterminado es 2.
     */
    private int numeroJugadores = 2;

    /**
     * Tiempo máximo de duración de la partida, expresado en segundos.
     * Su valor predeterminado es 60.
     */
    private float tiempoLimite = 60;

    /**
     * Cantidad de árbitros configurados para la partida.
     * Su valor predeterminado es 0 (sin árbitros).
     */
    private int numeroArbitros = 0;

    /**
     * Indica si el usuario actual actuará como anfitrión de la partida.
     * Si es  true, el usuario crea la partida;
     * si es false, se conectará como cliente.
     */
    private boolean esHost = false;

    /**
     * Dirección IP del servidor al que se conectará el cliente.
     * Este valor puede estar vacío cuando el usuario actúa como anfitrión.
     */
    private String ipServidor = "";

    /**
     * Obtiene la cantidad de jugadores configurada para la partida.
     *
     * @return el número de jugadores
     */
    public int getNumeroJugadores() {return numeroJugadores;}

    /**
     * Establece la cantidad de jugadores para la partida.
     *
     * @param numeroJugadores la cantidad de jugadores que participarán
     */
    public void setNumeroJugadores(int numeroJugadores) {this.numeroJugadores = numeroJugadores;}

    /**
     * Obtiene el tiempo límite configurado para la partida.
     *
     * @return el tiempo máximo de la partida, en segundos
     */
    public float getTiempoLimite() {return tiempoLimite;}

    /**
     * Establece el tiempo límite de duración de la partida.
     *
     * @param tiempoLimite el tiempo máximo de la partida, en segundos
     */
    public void setTiempoLimite(float tiempoLimite) {this.tiempoLimite = tiempoLimite;}

    /**
     * Obtiene la cantidad de árbitros configurada para la partida.
     *
     * @return el número de árbitros
     */
    public int getNumeroArbitros() { return numeroArbitros; }

    /**
     * Establece la cantidad de árbitros para la partida.
     *
     * @param numeroArbitros cantidad de árbitros
     */
    public void setNumeroArbitros(int numeroArbitros) { this.numeroArbitros = numeroArbitros; }

    /**
     * Indica si el usuario actuará como anfitrión de la partida.
     *
     * @return {@code true} si el usuario es anfitrión;
     *         {@code false} en caso contrario
     */
    public boolean isEsHost() {return esHost;}

    /**
     * Define si el usuario actuará como anfitrión de la partida.
     *
     * @param esHost true si el usuario será anfitrión;
     *               false si se conectará como cliente
     */
    public void setEsHost(boolean esHost) {this.esHost = esHost;}

    /**
     * Obtiene la dirección IP del servidor configurado.
     *
     * @return la dirección IP del servidor
     */
    public String getIpServidor() {return ipServidor;}

    /**
     * Establece la dirección IP del servidor al que se conectará el cliente.
     *
     * @param ipServidor la dirección IP del servidor
     */
    public void setIpServidor(String ipServidor) {this.ipServidor = ipServidor;}
}
