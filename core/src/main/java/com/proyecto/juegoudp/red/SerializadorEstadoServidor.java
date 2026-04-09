package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.Arbitro;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Genera la cadena {@code STATE|...} con jugadores, pelotas y metadatos para broadcast UDP.
 */
public class SerializadorEstadoServidor implements ISerializadorEstadoServidor {

    /**
     * Convierte el estado actual del juego en una cadena de texto plana para su envío por red.
     * El formato final es: {@code STATE|secuencia|jugadoresReq|tiempo|jugadores|pelotas|arbitros}
     * * @param secuencia Número correlativo del paquete.
     * @param jugadoresRequeridos Cantidad de jugadores mínima para la partida.
     * @param tiempoRestanteSegundos Tiempo para el fin de la partida.
     * @param estadoJuego Contenedor de todas las entidades del juego.
     * @return Cadena serializada lista para ser enviada por DatagramPacket.
     */
    @Override
    public String serializar(long secuencia, int jugadoresRequeridos, int tiempoRestanteSegundos, EstadoJuego estadoJuego) {
        StringBuilder sb = new StringBuilder("STATE|");
        sb.append(secuencia).append("|");
        sb.append(jugadoresRequeridos).append("|");
        sb.append(tiempoRestanteSegundos).append("|");

        // Segmento de Jugadores
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

        // Segmento de Pelotas
        for (Pelota p : estadoJuego.getPelotas().values()) {
            sb.append(p.getId()).append(",")
                .append(p.getX()).append(",")
                .append(p.getY()).append(",")
                .append(p.getVx()).append(",")
                .append(p.getVy()).append(",")
                .append(p.getIdJugador()).append(";");
        }
        sb.append("|");

        /**
         * NUEVO: Segmento de Árbitros.
         * Serializa las posiciones (X, Y) de todos los árbitros activos separados por comas.
         * Ejemplo: "x1,y1,x2,y2"
         */
        List<Arbitro> listaArbitros = estadoJuego.getArbitros();
        for (int i = 0; i < listaArbitros.size(); i++) {
            Arbitro arb = listaArbitros.get(i);
            sb.append(arb.getX()).append(",")
                .append(arb.getY());

            // Agregamos coma separadora solo si no es el último elemento
            if (i < listaArbitros.size() - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }
}
