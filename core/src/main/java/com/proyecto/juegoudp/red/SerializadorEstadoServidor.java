package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Implementa la serialización del estado del juego en el servidor.
 *
 * Esta clase construye una representación textual del estado actual
 * de la partida, incluyendo la secuencia del mensaje, la cantidad
 * de jugadores requeridos, el tiempo restante y la información
 * de jugadores y pelotas.
 *
 * El resultado generado sigue el formato STATE|...,
 * pensado para ser enviado mediante broadcast UDP a los clientes.
 *
 * Su propósito es transformar el EstadoJuego en una cadena
 * que pueda ser transmitida por red y posteriormente interpretada
 * por los clientes.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class SerializadorEstadoServidor implements ISerializadorEstadoServidor {

    /**
     * Convierte el estado actual del juego en una cadena serializada.
     *
     * La cadena resultante incluye:
     * - número de secuencia,
     * - cantidad de jugadores requeridos,
     * - tiempo restante de la partida,
     * - registros de jugadores,
     * - registros de pelotas.
     *
     * Los nombres de los jugadores son codificados para permitir
     * su transmisión segura dentro del mensaje de texto.
     *
     * @param secuencia número de secuencia del estado
     * @param jugadoresRequeridos cantidad de jugadores requeridos para la partida
     * @param tiempoRestanteSegundos tiempo restante de la partida en segundos
     * @param estadoJuego estado actual del juego en el servidor
     * @return una cadena serializada con el formato STATE|...
     */
    @Override
    public String serializar(long secuencia, int jugadoresRequeridos, int tiempoRestanteSegundos, EstadoJuego estadoJuego) {
        StringBuilder sb = new StringBuilder("STATE|");
        sb.append(secuencia).append("|");
        sb.append(jugadoresRequeridos).append("|");
        sb.append(tiempoRestanteSegundos).append("|");

        for (Jugador j : estadoJuego.getJugadores().values()) {
            String nomEnc = URLEncoder.encode(j.getNombre(), StandardCharsets.UTF_8);
            sb.append(j.getId()).append(",")
                .append(nomEnc).append(",")
                .append(j.getX()).append(",")
                .append(j.getY()).append(",")
                .append(j.getPuntaje()).append(",")
                .append(j.getAvatarId()).append(",")
                .append(j.isTienePelota() ? 1 : 0).append(",")
                .append(0).append(";");
        }

        sb.append("|");

        for (Pelota p : estadoJuego.getPelotas().values()) {
            sb.append(p.getId()).append(",")
                .append(p.getX()).append(",")
                .append(p.getY()).append(",")
                .append(p.getVx()).append(",")
                .append(p.getVy()).append(",")
                .append(p.getIdJugador()).append(";");
        }

        return sb.toString();
    }
}
