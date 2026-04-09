package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.red.AnalizadorInstantaneaJuego;

/**
 * Aplica una instantánea de red al estado local usado por la pantalla de partida.
 */
public final class AplicadorInstantaneaPartida {
    private AplicadorInstantaneaPartida() {}

    public static ResultadoAplicacion aplicar(
        AnalizadorInstantaneaJuego.InstantaneaPartida instantanea,
        EstadoJuego estadoLocal,
        String nombreJugadorLocal,
        int idJugadorActual
    ) {
        int puntajeTotalAnterior = 0;
        for (Jugador jugador : estadoLocal.getJugadores().values()) {
            puntajeTotalAnterior += jugador.getPuntaje();
        }

        estadoLocal.getJugadores().clear();
        int idJugadorResuelto = idJugadorActual;

        for (AnalizadorInstantaneaJuego.DatoJugadorInstantanea datoJugador : instantanea.jugadores) {
            Jugador jugador = new Jugador(datoJugador.id, datoJugador.nombre, datoJugador.avatarId);
            jugador.setX(datoJugador.x);
            jugador.setY(datoJugador.y);
            jugador.setPuntaje(datoJugador.puntaje);
            jugador.setTienePelota(datoJugador.tienePelota);
            estadoLocal.agregarJugador(jugador);

            if (datoJugador.nombre.equals(nombreJugadorLocal)) {
                idJugadorResuelto = datoJugador.id;
            }
        }

        int puntajeTotalNuevo = 0;
        for (Jugador jugador : estadoLocal.getJugadores().values()) {
            puntajeTotalNuevo += jugador.getPuntaje();
        }

        estadoLocal.getPelotas().clear();
        for (AnalizadorInstantaneaJuego.DatoPelotaInstantanea datoPelota : instantanea.pelotas) {
            Pelota pelota = new Pelota(datoPelota.id, datoPelota.x, datoPelota.y);
            pelota.setVx(datoPelota.vx);
            pelota.setVy(datoPelota.vy);
            pelota.setIdJugador(datoPelota.idJugador);
            estadoLocal.agregarPelota(pelota);
        }

        // --- ACTUALIZACIÓN DEL ÁRBITRO (NUEVO) ---
        // Sincronizamos la posición del árbitro local con los datos recibidos del servidor
        if (estadoLocal.getArbitro() != null) {
            estadoLocal.getArbitro().setX(instantanea.arbitroX);
            estadoLocal.getArbitro().setY(instantanea.arbitroY);
        }

        ResultadoAplicacion resultado = new ResultadoAplicacion();
        resultado.idJugadorResuelto = idJugadorResuelto;
        resultado.huboAumentoPuntaje = puntajeTotalNuevo > puntajeTotalAnterior;
        return resultado;
    }

    /** Efectos secundarios de aplicar una instantánea (id local y si subió el puntaje global). */
    public static final class ResultadoAplicacion {
        public int idJugadorResuelto;
        public boolean huboAumentoPuntaje;
    }
}
