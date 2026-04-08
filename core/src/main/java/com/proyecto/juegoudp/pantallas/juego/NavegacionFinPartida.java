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

    public static void irAPantallaFinal(JuegoPrincipal juego, EstadoJuego estadoLocal, int tiempoTotalSegundosConfig) {
        ArrayList<Jugador> ranking = new ArrayList<>(estadoLocal.getJugadores().values());
        ranking.sort(Comparator.comparingInt(Jugador::getPuntaje).reversed());
        String ganador = ranking.isEmpty() ? "Sin ganador" : ranking.get(0).getNombre();
        int puntaje = ranking.isEmpty() ? 0 : ranking.get(0).getPuntaje();
        juego.setScreen(new PantallaFinal(juego, ganador, puntaje, ranking, tiempoTotalSegundosConfig));
    }
}
