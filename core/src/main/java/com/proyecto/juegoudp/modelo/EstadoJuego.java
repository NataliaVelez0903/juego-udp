package com.proyecto.juegoudp.modelo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Representa el estado general de una partida en ejecución.
 *
 * Esta clase centraliza la información principal del juego,
 * almacenando los jugadores, las pelotas y las zonas que forman
 * parte de la partida. Su propósito es servir como contenedor del
 * estado compartido del juego, permitiendo registrar y consultar
 * sus elementos durante la ejecución.
 *
 * Las colecciones internas se implementan mediante
 * {ConcurrentHashMap}, lo que permite un acceso seguro
 * en contextos donde distintos procesos o hilos puedan consultar
 * o modificar simultáneamente la información del juego.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */

public class EstadoJuego {

    /**
     * Colección de jugadores registrados en la partida.
     * La clave corresponde al identificador único del jugador.
     */
    private Map<Integer, Jugador> jugadores;

    /**
     * Colección de pelotas disponibles dentro de la partida.
     * La clave corresponde al identificador único de la pelota.
     */
    private Map<Integer, Pelota> pelotas;

    /**
     * Colección de zonas definidas dentro del escenario de juego.
     * La clave corresponde al identificador único de la zona.
     */
    private Map<Integer, Zona> zonas;

    /**
     * Construye un nuevo estado de juego e inicializa las colecciones
     * necesarias para almacenar jugadores, pelotas y zonas.
     *
     * Al crear una instancia, todas las estructuras comienzan vacías
     * y listas para ser utilizadas durante la partida.
     */
    public EstadoJuego() {
        jugadores = new ConcurrentHashMap<>();
        pelotas = new ConcurrentHashMap<>();
        zonas = new ConcurrentHashMap<>();
    }

    /**
     * Agrega un jugador al estado actual del juego.
     *
     * @param j el jugador que se desea registrar
     */
    public void agregarJugador(Jugador j) {jugadores.put(j.getId(), j);}

    /**
     * Obtiene un jugador a partir de su identificador.
     *
     * @param id el identificador del jugador a consultar
     * @return el jugador asociado al identificador indicado,
     *         o {null} si no existe
     */
    public Jugador getJugador(int id) {return jugadores.get(id);}

    /**
     * Obtiene la colección completa de jugadores registrados en la partida.
     *
     * @return un mapa que contiene los jugadores del juego
     */
    public Map<Integer, Jugador> getJugadores() {return jugadores;}

    /**
     * Agrega una pelota al estado actual del juego.
     *
     * @param p la pelota que se desea registrar
     */
    public void agregarPelota(Pelota p) {pelotas.put(p.getId(), p);}

    /**
     * Obtiene una pelota a partir de su identificador.
     *
     * @param id el identificador de la pelota a consultar
     * @return la pelota asociada al identificador indicado,
     *         o {null} si no existe
     */
    public Pelota getPelota(int id) {return pelotas.get(id);}

    /**
     * Obtiene la colección completa de pelotas registradas en la partida.
     *
     * @return un mapa que contiene las pelotas del juego
     */
    public Map<Integer, Pelota> getPelotas() {return pelotas;}

    /**
     * Agrega una zona al estado actual del juego.
     *
     * @param z la zona que se desea registrar
     */
    public void agregarZona(Zona z) {zonas.put(z.getId(), z);}

    /**
     * Obtiene una zona a partir de su identificador.
     *
     * @param id el identificador de la zona a consultar
     * @return la zona asociada al identificador indicado,
     *         o {null} si no existe
     */
    public Zona getZona(int id) {return zonas.get(id);}

    /**
     * Obtiene la colección completa de zonas registradas en la partida.
     *
     * @return un mapa que contiene las zonas del juego
     */
    public Map<Integer, Zona> getZonas() {return zonas;}
}
