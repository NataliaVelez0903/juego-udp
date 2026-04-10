package com.proyecto.juegoudp.pantallas.juego;

/**
 * Adapta la información proporcionada por el gestor de estado de red
 * al contrato requerido por el renderizador de la partida.
 *
 * Esta clase actúa como un intermediario entre {@link GestorEstadoRedPartida}
 * y {@link RenderizadorPartida.ProveedorInterfazPartida}, permitiendo que
 * el renderizador consulte los datos necesarios de la partida sin depender
 * directamente de la implementación concreta del gestor.
 *
 * Su propósito es facilitar el desacoplamiento entre la lógica de estado
 * de red y la capa de presentación de la interfaz de juego.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public final class ProveedorInterfazPartidaGestorEstado implements RenderizadorPartida.ProveedorInterfazPartida {

    /**
     * Gestor del estado de red de la partida del cual se obtienen
     * los datos necesarios para la interfaz.
     */
    private final GestorEstadoRedPartida gestor;

    /**
     * Construye un nuevo adaptador de interfaz para la partida.
     *
     * @param gestor el gestor de estado de red que provee la información requerida
     */
    public ProveedorInterfazPartidaGestorEstado(GestorEstadoRedPartida gestor) {
        this.gestor = gestor;
    }

    /**
     * Obtiene el identificador del jugador local.
     *
     * @return el identificador del jugador actual
     */
    @Override
    public int obtenerIdJugadorLocal() {
        return gestor.obtenerIdJugador();
    }

    /**
     * Obtiene el tiempo restante de la partida.
     *
     * @return el tiempo restante en segundos
     */
    @Override
    public int obtenerTiempoRestanteSegundos() {
        return gestor.obtenerTiempoRestanteSegundos();
    }

    /**
     * Obtiene la cantidad de jugadores requeridos para la partida.
     *
     * @return el número de jugadores requeridos
     */
    @Override
    public int obtenerJugadoresRequeridos() {
        return gestor.obtenerJugadoresRequeridos();
    }
}
