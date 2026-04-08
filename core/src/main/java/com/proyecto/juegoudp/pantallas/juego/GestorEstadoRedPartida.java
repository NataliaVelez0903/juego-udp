package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.red.AnalizadorInstantaneaJuego;
import com.proyecto.juegoudp.sonido.GestorSonidos;

/**
 * Aplica instantáneas de red al modelo local y mantiene datos de sesión (id, tiempo).
 */
public final class GestorEstadoRedPartida {
    private final EstadoJuego estadoLocal;
    private final GestorSonidos gestorSonidos;
    private final String nombreJugadorLocal;

    private int idJugador = -1;
    private long ultimaSecuenciaRecibida = -1;
    private int tiempoRestanteSegundos = -1;

    public GestorEstadoRedPartida(EstadoJuego estadoLocal, GestorSonidos gestorSonidos, String nombreJugadorLocal) {
        this.estadoLocal = estadoLocal;
        this.gestorSonidos = gestorSonidos;
        this.nombreJugadorLocal = nombreJugadorLocal;
    }

    public void establecerIdJugador(int id) {
        this.idJugador = id;
    }

    public int obtenerIdJugador() {
        return idJugador;
    }

    public int obtenerTiempoRestanteSegundos() {
        return tiempoRestanteSegundos;
    }

    /**
     * Procesa un {@code STATE|...} del servidor; ignora duplicados por secuencia.
     */
    public void recibirEstadoSerializado(String estadoSerializado) {
        AnalizadorInstantaneaJuego.InstantaneaPartida instantanea =
                AnalizadorInstantaneaJuego.analizar(estadoSerializado);
        if (instantanea == null) {
            return;
        }
        if (instantanea.secuencia <= ultimaSecuenciaRecibida) {
            return;
        }
        ultimaSecuenciaRecibida = instantanea.secuencia;
        tiempoRestanteSegundos = instantanea.tiempoRestanteSegundos;

        AplicadorInstantaneaPartida.ResultadoAplicacion resultado =
                AplicadorInstantaneaPartida.aplicar(instantanea, estadoLocal, nombreJugadorLocal, idJugador);
        idJugador = resultado.idJugadorResuelto;

        if (resultado.huboAumentoPuntaje) {
            gestorSonidos.reproducirGol(1.0f);
        }
    }
}
