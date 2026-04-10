package com.proyecto.juegoudp.pantallas.espera;

import com.proyecto.juegoudp.red.Mensaje;

/**
 * Define los eventos de comunicación entre la lógica de la sala de espera
 * y la interfaz de usuario.
 *
 * Esta interfaz actúa como un mecanismo de notificación (callback)
 * que permite informar a la pantalla de la sala sobre los distintos
 * eventos que ocurren durante la conexión y preparación de la partida.
 *
 * Todos los métodos se ejecutan en el hilo principal de libGDX,
 * lo que garantiza que las actualizaciones de la interfaz gráfica
 * se realicen de forma segura.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public interface EscuchaSala {

    /**
     * Notifica que ha ocurrido un fallo durante el proceso de conexión
     * o gestión de la sala.
     *
     * @param mensaje descripción del error ocurrido
     */
    void alFallo(String mensaje);

    /**
     * Notifica la asignación del identificador del jugador actual
     * dentro de la partida.
     *
     * @param idJugador identificador asignado al jugador
     */
    void alAsignarIdJugador(int idJugador);

    /**
     * Notifica la actualización del número de jugadores conectados
     * en la sala.
     *
     * @param conectados cantidad de jugadores actualmente conectados
     * @param requeridos cantidad mínima de jugadores necesarios
     *                   para iniciar la partida
     */
    void alActualizarConectados(int conectados, int requeridos);

    /**
     * Indica que se cumplen las condiciones necesarias para iniciar
     * la partida.
     *
     * Este evento normalmente se produce cuando el número de jugadores
     * conectados alcanza el mínimo requerido.
     */
    void alIniciarPartida();

    /**
     * Notifica la recepción de un mensaje desde la red.
     *
     * @param mensaje el mensaje recibido desde el servidor o cliente
     */
    void alRecibirMensaje(Mensaje mensaje);
}
