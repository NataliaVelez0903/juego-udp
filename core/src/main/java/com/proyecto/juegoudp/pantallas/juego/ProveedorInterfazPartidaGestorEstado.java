package com.proyecto.juegoudp.pantallas.juego;

/**
 * Adapta {@link GestorEstadoRedPartida} a {@link RenderizadorPartida.ProveedorInterfazPartida}.
 */
public final class ProveedorInterfazPartidaGestorEstado implements RenderizadorPartida.ProveedorInterfazPartida {
    private final GestorEstadoRedPartida gestor;

    public ProveedorInterfazPartidaGestorEstado(GestorEstadoRedPartida gestor) {
        this.gestor = gestor;
    }

    @Override
    public int obtenerIdJugadorLocal() {
        return gestor.obtenerIdJugador();
    }

    @Override
    public int obtenerTiempoRestanteSegundos() {
        return gestor.obtenerTiempoRestanteSegundos();
    }
}
