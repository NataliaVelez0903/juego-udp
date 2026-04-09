package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.pantallas.PantallaFinal;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Única responsabilidad: pasar de partida en curso a la pantalla de resultados.
 */
public final class NavegacionFinPartida {
    private NavegacionFinPartida() {}

    public static void irAPantallaFinal(
            JuegoPrincipal juego,
            EstadoJuego estadoLocal,
            int tiempoTotalSegundosConfig,
            int jugadoresRequeridos
    ) {
        ArrayList<Jugador> ranking = new ArrayList<>(estadoLocal.getJugadores().values());
        ranking.sort(Comparator.comparingInt(Jugador::getPuntaje).reversed());
        String ganador = ranking.isEmpty() ? "Sin ganador" : ranking.get(0).getNombre();
        int puntaje = ranking.isEmpty() ? 0 : ranking.get(0).getPuntaje();
        if (jugadoresRequeridos >= 4) {
            int puntajeEquipoA = 0;
            int puntajeEquipoB = 0;
            for (Jugador jugador : ranking) {
                if (esEquipoA(jugador.getId())) {
                    puntajeEquipoA += jugador.getPuntaje();
                } else {
                    puntajeEquipoB += jugador.getPuntaje();
                }
            }
            if (puntajeEquipoA == puntajeEquipoB) {
                ganador = "Empate de equipos";
                puntaje = puntajeEquipoA;
            } else if (puntajeEquipoA > puntajeEquipoB) {
                ganador = "Equipo A (J1/J3)";
                puntaje = puntajeEquipoA;
            } else {
                ganador = "Equipo B (J2/J4)";
                puntaje = puntajeEquipoB;
            }
        }
        juego.setScreen(new PantallaFinal(juego, ganador, puntaje, ranking, tiempoTotalSegundosConfig));
    }

    private static boolean esEquipoA(int idJugador) {
        return idJugador % 2 != 0;
    }
}
