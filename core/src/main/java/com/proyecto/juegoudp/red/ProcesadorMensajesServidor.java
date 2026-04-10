package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.utilidades.Constantes;
import java.net.InetAddress;
import java.util.Map;

/**
 * Implementa la lógica del servidor para procesar los mensajes UDP entrantes.
 *
 * Esta clase se encarga de interpretar cada mensaje recibido desde los clientes
 * y aplicar las reglas correspondientes sobre el estado del juego.
 *
 * Maneja acciones como:
 * - Unión de jugadores a la partida.
 * - Movimiento de jugadores.
 * - Interacción con pelotas (tomar, mover y soltar).
 *
 * Su propósito es centralizar la lógica del servidor relacionada con el
 * procesamiento de mensajes, manteniendo el estado del juego sincronizado.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class ProcesadorMensajesServidor implements IProcesadorMensajesServidor {

    /**
     * Procesa un mensaje recibido y aplica la lógica correspondiente.
     *
     * Dependiendo del tipo de mensaje, delega la ejecución
     * al método específico que implementa la acción.
     */
    @Override
    public void procesar(
        Mensaje mensaje,
        InetAddress ip,
        int puerto,
        String claveCliente,
        EstadoJuego estadoJuego,
        Map<String, Integer> jugadorPorCliente,
        Map<String, ServidorUDP.ClienteInfoPublica> clientes,
        ServidorUDP.Envio envio,
        boolean partidaTerminada
    ) {
        switch (mensaje.getTipo()) {
            case UNIRSE:
                procesarUnirse(mensaje, ip, puerto, claveCliente, estadoJuego, jugadorPorCliente, envio);
                break;
            case MOVER_JUGADOR:
                if (!partidaTerminada) procesarMoverJugador(mensaje, estadoJuego);
                break;
            case TOMAR_PELOTA:
                if (!partidaTerminada) procesarTomarPelota(mensaje, estadoJuego);
                break;
            case MOVER_PELOTA:
                if (!partidaTerminada) procesarMoverPelota(mensaje, estadoJuego);
                break;
            case SOLTAR_PELOTA:
                if (!partidaTerminada) procesarSoltarPelota(mensaje, estadoJuego);
                break;
            default:
                break;
        }
    }

    /**
     * Procesa la solicitud de unión de un cliente a la partida.
     *
     * Asigna un identificador al jugador, valida si ya existe
     * o si la sala está llena, y envía la respuesta correspondiente.
     */
    private void procesarUnirse(
        Mensaje mensaje,
        InetAddress ip,
        int puerto,
        String claveCliente,
        EstadoJuego estadoJuego,
        Map<String, Integer> jugadorPorCliente,
        ServidorUDP.Envio envio
    ) {
        Integer idPrevio = jugadorPorCliente.get(claveCliente);
        if (idPrevio != null) {
            envio.enviarA(ip, puerto, new Mensaje(TipoMensaje.TU_ID, idPrevio, 0, 0, 0, 0, 0, ""));
            return;
        }

        if (estadoJuego.getJugadores().size() >= Constantes.MAX_JUGADORES) {
            envio.enviarA(ip, puerto, new Mensaje(TipoMensaje.TU_ID, -1, 0, 0, 0, 0, 0, "SALA_LLENA"));
            return;
        }

        String[] na = parseNombreYAvatar(mensaje.getDatos());
        int id = estadoJuego.getJugadores().size() + 1;
        int avatarId = parseInt(na[1], 0);

        Jugador jug = new Jugador(id, na[0], avatarId);
        jug.setX(400 + (float) Math.random() * 200);
        jug.setY(300 + (float) Math.random() * 200);

        estadoJuego.agregarJugador(jug);
        jugadorPorCliente.put(claveCliente, id);

        envio.enviarA(ip, puerto, new Mensaje(TipoMensaje.TU_ID, id, 0, 0, 0, 0, 0, ""));
    }

    /**
     * Actualiza la posición de un jugador en el estado del juego.
     */
    private void procesarMoverJugador(Mensaje mensaje, EstadoJuego estadoJuego) {
        Jugador j = estadoJuego.getJugador(mensaje.getIdJugador());
        if (j != null) {
            j.setX(mensaje.getX());
            j.setY(mensaje.getY());
        }
    }

    /**
     * Asigna una pelota a un jugador si esta se encuentra libre.
     */
    private void procesarTomarPelota(Mensaje mensaje, EstadoJuego estadoJuego) {
        Pelota pelota = estadoJuego.getPelota(mensaje.getIdObjeto());
        if (pelota != null && pelota.getIdJugador() == -1) {
            pelota.setIdJugador(mensaje.getIdJugador());
            Jugador jugador = estadoJuego.getJugador(mensaje.getIdJugador());
            if (jugador != null) {
                jugador.setTienePelota(true);
            }
        }
    }

    /**
     * Actualiza la posición de una pelota controlada por un jugador.
     */
    private void procesarMoverPelota(Mensaje mensaje, EstadoJuego estadoJuego) {
        Pelota pelota = estadoJuego.getPelota(mensaje.getIdObjeto());
        if (pelota != null && pelota.getIdJugador() == mensaje.getIdJugador()) {
            pelota.setX(mensaje.getX());
            pelota.setY(mensaje.getY());
        }
    }

    /**
     * Libera una pelota y aplica su velocidad.
     */
    private void procesarSoltarPelota(Mensaje mensaje, EstadoJuego estadoJuego) {
        Pelota pelota = estadoJuego.getPelota(mensaje.getIdObjeto());
        if (pelota != null && pelota.getIdJugador() == mensaje.getIdJugador()) {
            pelota.setIdJugador(-1);
            pelota.setVx(mensaje.getVx());
            pelota.setVy(mensaje.getVy());

            Jugador jugador = estadoJuego.getJugador(mensaje.getIdJugador());
            if (jugador != null) {
                jugador.setTienePelota(false);
            }
        }
    }

    /**
     * Convierte una cadena a entero con valor por defecto en caso de error.
     */
    private static int parseInt(String valor, int defecto) {
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            return defecto;
        }
    }

    /**
     * Extrae el nombre del jugador y el identificador de avatar
     * desde la cadena de datos recibida.
     *
     * El formato esperado es: "nombre\tavatarId".
     */
    private static String[] parseNombreYAvatar(String datos) {
        String nombre = "Jugador";
        int avatar = 0;

        if (datos != null && !datos.isEmpty()) {
            int tab = datos.indexOf('\t');
            if (tab >= 0) {
                nombre = datos.substring(0, tab).trim();
                if (nombre.isEmpty()) nombre = "Jugador";
                avatar = parseInt(datos.substring(tab + 1).trim(), 0);
            } else {
                nombre = datos.trim().isEmpty() ? "Jugador" : datos.trim();
            }
        }

        return new String[]{nombre, String.valueOf(avatar)};
    }
}
