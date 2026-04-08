package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.EstadoJuego;

/**
 * Serializa el {@link EstadoJuego} del servidor en una instantánea de texto para los clientes.
 */
public interface ISerializadorEstadoServidor {
    String serializar(long secuencia, int jugadoresRequeridos, int tiempoRestanteSegundos, EstadoJuego estadoJuego);
}

