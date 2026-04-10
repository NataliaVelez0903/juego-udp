package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.red.AnalizadorInstantaneaJuego;
import com.proyecto.juegoudp.sonido.GestorSonidos;

/**
 * Gestiona la sincronización del estado de red de la partida con el modelo local.
 *
 * Esta clase se encarga de procesar las instantáneas recibidas desde el servidor,
 * actualizar el estado local del juego y mantener información de sesión
 * relevante, como el identificador del jugador actual, la última secuencia
 * recibida, el tiempo restante de la partida y la cantidad de jugadores requeridos.
 *
 * Además, coordina efectos secundarios asociados a la actualización del estado,
 * como la reproducción de sonidos cuando se detecta un aumento en el puntaje.
 *
 * Al ser una clase de gestión de estado, centraliza la lógica de aplicación
 * de instantáneas de red sobre la partida local.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public final class GestorEstadoRedPartida {

    /**
     * Estado local del juego que será actualizado a partir de las
     * instantáneas recibidas desde la red.
     */
    private final EstadoJuego estadoLocal;

    /**
     * Gestor de sonidos utilizado para reproducir efectos asociados
     * a eventos detectados en la sincronización del estado.
     */
    private final GestorSonidos gestorSonidos;

    /**
     * Nombre del jugador local, utilizado para resolver su identificador
     * al aplicar las instantáneas de la partida.
     */
    private final String nombreJugadorLocal;

    /**
     * Identificador actual del jugador local.
     *
     * Su valor inicial es {@code -1}, indicando que aún no ha sido asignado.
     */
    private int idJugador = -1;

    /**
     * Último número de secuencia procesado correctamente.
     *
     * Se utiliza para ignorar instantáneas repetidas o desactualizadas.
     */
    private long ultimaSecuenciaRecibida = -1;

    /**
     * Tiempo restante de la partida, expresado en segundos.
     *
     * Su valor inicial es {@code -1} mientras no se haya recibido
     * una instantánea válida.
     */
    private int tiempoRestanteSegundos = -1;

    /**
     * Cantidad de jugadores requeridos para la partida.
     *
     * Su valor inicial predeterminado es 2.
     */
    private int jugadoresRequeridos = 2;

    /**
     * Construye un nuevo gestor del estado de red de la partida.
     *
     * @param estadoLocal el estado local del juego que será sincronizado
     * @param gestorSonidos el gestor encargado de reproducir efectos de sonido
     * @param nombreJugadorLocal el nombre del jugador local
     */
    public GestorEstadoRedPartida(EstadoJuego estadoLocal, GestorSonidos gestorSonidos, String nombreJugadorLocal) {
        this.estadoLocal = estadoLocal;
        this.gestorSonidos = gestorSonidos;
        this.nombreJugadorLocal = nombreJugadorLocal;
    }

    /**
     * Establece el identificador del jugador local.
     *
     * @param id el identificador que se asignará al jugador local
     */
    public void establecerIdJugador(int id) {
        this.idJugador = id;
    }

    /**
     * Obtiene el identificador actual del jugador local.
     *
     * @return el identificador del jugador
     */
    public int obtenerIdJugador() {
        return idJugador;
    }

    /**
     * Obtiene el tiempo restante actual de la partida.
     *
     * @return el tiempo restante en segundos
     */
    public int obtenerTiempoRestanteSegundos() {
        return tiempoRestanteSegundos;
    }

    /**
     * Obtiene la cantidad de jugadores requeridos para la partida.
     *
     * @return el número de jugadores requeridos
     */
    public int obtenerJugadoresRequeridos() {
        return jugadoresRequeridos;
    }

    /**
     * Procesa un estado serializado recibido desde el servidor y actualiza
     * el modelo local de la partida.
     *
     * Este método analiza la instantánea recibida, verifica que sea válida
     * y que su número de secuencia sea más reciente que el último procesado.
     * Si la instantánea es aceptada, actualiza el tiempo restante, la cantidad
     * de jugadores requeridos y aplica su contenido sobre el estado local.
     *
     * Además, si la aplicación de la instantánea detecta un aumento
     * en el puntaje global, se reproduce el sonido correspondiente.
     *
     * @param estadoSerializado el estado de la partida recibido desde el servidor
     *                          en formato serializado
     */
    public void recibirEstadoSerializado(String estadoSerializado) {
        AnalizadorInstantaneaJuego.InstantaneaPartida instantanea =
            AnalizadorInstantaneaJuego.analizar(estadoSerializado);
        if (instantanea == null) {
            return;
        }
        if (instantanea.secuencia <= ultimaSecuenciaRecibida) {
            return;
        }
        ultimaSecuenciaRecibida = instantanea.secuencia;
        tiempoRestanteSegundos = instantanea.tiempoRestanteSegundos;
        jugadoresRequeridos = instantanea.jugadoresRequeridos;

        AplicadorInstantaneaPartida.ResultadoAplicacion resultado =
            AplicadorInstantaneaPartida.aplicar(instantanea, estadoLocal, nombreJugadorLocal, idJugador);
        idJugador = resultado.idJugadorResuelto;

        if (resultado.huboAumentoPuntaje) {
            gestorSonidos.reproducirGol(1.0f);
        }
    }
}
