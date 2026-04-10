package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.pantallas.PantallaFinal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Gestiona la navegación desde la partida en curso hacia la pantalla final.
 *
 * Esta clase utilitaria concentra la lógica necesaria para determinar
 * el resultado final de la partida y realizar la transición hacia
 * la pantalla de resultados.
 *
 * A partir del estado local del juego, calcula el ranking de jugadores,
 * identifica al ganador o equipo ganador y construye la pantalla final
 * con la información correspondiente.
 *
 * Al ser una clase de utilidad, no está pensada para ser instanciada.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public final class NavegacionFinPartida {

    /**
     * Constructor privado para evitar la creación de instancias de esta clase.
     */
    private NavegacionFinPartida() {}

    /**
     * Realiza la transición hacia la pantalla final de la partida.
     *
     * Este método construye el ranking de jugadores a partir del estado local,
     * determina el ganador individual o por equipos según la cantidad de
     * jugadores requeridos y cambia la pantalla actual del juego por la
     * pantalla final con los resultados obtenidos.
     *
     * Si la partida se juega con cuatro o más jugadores, el resultado
     * se calcula por equipos. En caso contrario, se determina
     * el ganador individual con mayor puntaje.
     *
     * @param juego referencia al juego principal
     * @param estadoLocal estado local de la partida con la información de los jugadores
     * @param tiempoTotalSegundosConfig duración total configurada para la partida, en segundos
     * @param jugadoresRequeridos cantidad de jugadores requeridos para la partida
     */
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
                ganador = "Equipo A (" + nombresEquipo(ranking, true) + ")";
                puntaje = puntajeEquipoA;
            } else {
                ganador = "Equipo B (" + nombresEquipo(ranking, false) + ")";
                puntaje = puntajeEquipoB;
            }
        }
        juego.setScreen(new PantallaFinal(juego, ganador, puntaje, ranking, tiempoTotalSegundosConfig));
    }

    /**
     * Determina si un jugador pertenece al equipo A.
     *
     * En esta lógica, un jugador pertenece al equipo A si su identificador
     * es impar.
     *
     * @param idJugador identificador del jugador
     * @return true si el jugador pertenece al equipo A; false en caso contrario
     */
    private static boolean esEquipoA(int idJugador) {
        return idJugador % 2 != 0;
    }

    /**
     * Construye una cadena con los nombres de los jugadores que pertenecen
     * al equipo indicado.
     *
     * Los nombres se concatenan separados por " / ". Si no hay jugadores
     * asociados al equipo solicitado, retorna el símbolo "-".
     *
     * @param jugadores lista de jugadores de la partida
     * @param equipoA indica si se deben obtener los nombres del equipo A;
     *                si es false, se obtienen los del equipo B
     * @return una cadena con los nombres de los jugadores del equipo indicado
     */
    private static String nombresEquipo(List<Jugador> jugadores, boolean equipoA) {
        StringBuilder nombres = new StringBuilder();
        for (Jugador jugador : jugadores) {
            if (esEquipoA(jugador.getId()) != equipoA) {
                continue;
            }
            if (nombres.length() > 0) {
                nombres.append(" / ");
            }
            nombres.append(jugador.getNombre());
        }
        return nombres.length() == 0 ? "-" : nombres.toString();
    }
}
