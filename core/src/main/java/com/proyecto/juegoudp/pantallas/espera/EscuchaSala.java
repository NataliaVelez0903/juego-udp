package com.proyecto.juegoudp.pantallas.espera;

import com.proyecto.juegoudp.red.Mensaje;

/**
 * Eventos de la sala de espera hacia la pantalla (hilo de libGDX).
 */
public interface EscuchaSala {
    void alFallo(String mensaje);
    void alAsignarIdJugador(int idJugador);
    void alActualizarConectados(int conectados, int requeridos);
    void alIniciarPartida();
    void alRecibirMensaje(Mensaje mensaje);
}
