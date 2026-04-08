package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.utilidades.Constantes;
import java.net.InetAddress;
import java.util.Map;

/**
 * Implementa las reglas del servidor para cada mensaje UDP.
 */
public class ProcesadorMensajesServidor implements IProcesadorMensajesServidor {
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

    private void procesarMoverJugador(Mensaje mensaje, EstadoJuego estadoJuego) {
        Jugador j = estadoJuego.getJugador(mensaje.getIdJugador());
        if (j != null) { j.setX(mensaje.getX()); j.setY(mensaje.getY()); }
    }

    private void procesarTomarPelota(Mensaje mensaje, EstadoJuego estadoJuego) {
        Pelota pelota = estadoJuego.getPelota(mensaje.getIdObjeto());
        if (pelota != null && pelota.getIdJugador() == -1) {
            pelota.setIdJugador(mensaje.getIdJugador());
            Jugador jugador = estadoJuego.getJugador(mensaje.getIdJugador());
            if (jugador != null) jugador.setTienePelota(true);
        }
    }

    private void procesarMoverPelota(Mensaje mensaje, EstadoJuego estadoJuego) {
        Pelota pelota = estadoJuego.getPelota(mensaje.getIdObjeto());
        if (pelota != null && pelota.getIdJugador() == mensaje.getIdJugador()) {
            pelota.setX(mensaje.getX());
            pelota.setY(mensaje.getY());
        }
    }

    private void procesarSoltarPelota(Mensaje mensaje, EstadoJuego estadoJuego) {
        Pelota pelota = estadoJuego.getPelota(mensaje.getIdObjeto());
        if (pelota != null && pelota.getIdJugador() == mensaje.getIdJugador()) {
            pelota.setIdJugador(-1);
            pelota.setVx(mensaje.getVx());
            pelota.setVy(mensaje.getVy());
            Jugador jugador = estadoJuego.getJugador(mensaje.getIdJugador());
            if (jugador != null) jugador.setTienePelota(false);
        }
    }

    private static int parseInt(String valor, int defecto) {
        try { return Integer.parseInt(valor); } catch (Exception e) { return defecto; }
    }

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
        return new String[] { nombre, String.valueOf(avatar) };
    }
}

