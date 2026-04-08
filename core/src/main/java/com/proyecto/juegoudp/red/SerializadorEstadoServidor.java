package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Genera la cadena {@code STATE|...} con jugadores, pelotas y metadatos para broadcast UDP.
 */
public class SerializadorEstadoServidor implements ISerializadorEstadoServidor {
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
                    .append(0).append(";"); // flags reservados
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

