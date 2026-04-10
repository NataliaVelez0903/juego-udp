package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.red.AnalizadorInstantaneaJuego;

/**
 * Aplica una instantánea recibida desde la red al estado local de la partida.
 *
 * Esta clase se encarga de tomar una instantánea completa del juego,
 * reconstruir con ella el estado local utilizado por la pantalla de partida
 * y devolver información adicional sobre el resultado de dicha aplicación.
 *
 * Su propósito es sincronizar el estado local con la información proveniente
 * de la red, actualizando jugadores, pelotas e identificando efectos
 * relevantes como la resolución del identificador del jugador actual
 * y los cambios en el puntaje global.
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
public final class AplicadorInstantaneaPartida {

    /**
     * Constructor privado para evitar la creación de instancias de esta clase.
     */
    private AplicadorInstantaneaPartida() {}

    /**
     * Aplica una instantánea de la partida sobre el estado local actual.
     *
     * Este método reemplaza la información local de jugadores y pelotas
     * con los datos contenidos en la instantánea recibida. Además,
     * intenta resolver nuevamente el identificador del jugador local
     * a partir de su nombre y determina si el puntaje total del juego
     * aumentó respecto al estado anterior.
     *
     * @param instantanea la instantánea recibida desde la red con el estado actual de la partida
     * @param estadoLocal el estado local que será actualizado con la información de la instantánea
     * @param nombreJugadorLocal el nombre del jugador local, utilizado para resolver su identificador
     * @param idJugadorActual el identificador actual conocido del jugador local
     * @return un objeto con los resultados derivados de la aplicación de la instantánea
     */
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

        ResultadoAplicacion resultado = new ResultadoAplicacion();
        resultado.idJugadorResuelto = idJugadorResuelto;
        resultado.huboAumentoPuntaje = puntajeTotalNuevo > puntajeTotalAnterior;
        return resultado;
    }

    /**
     * Representa el resultado de aplicar una instantánea al estado local.
     *
     * Esta clase agrupa los efectos derivados del proceso de sincronización,
     * específicamente el identificador resuelto del jugador local y la
     * indicación de si el puntaje global aumentó tras aplicar la instantánea.
     */
    public static final class ResultadoAplicacion {

        /**
         * Identificador resuelto del jugador local después de aplicar
         * la instantánea.
         */
        public int idJugadorResuelto;

        /**
         * Indica si el puntaje total de la partida aumentó después
         * de aplicar la instantánea.
         */
        public boolean huboAumentoPuntaje;
    }
}
