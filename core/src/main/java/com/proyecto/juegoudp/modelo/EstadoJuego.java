package com.proyecto.juegoudp.modelo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EstadoJuego {
    private Map<Integer, Jugador> jugadores;
    private Map<Integer, Pelota> pelotas;
    private Map<Integer, Zona> zonas;

    public EstadoJuego() {
        jugadores = new ConcurrentHashMap<>();
        pelotas = new ConcurrentHashMap<>();
        zonas = new ConcurrentHashMap<>();
    }
    public void agregarJugador(Jugador j) { jugadores.put(j.getId(), j); }
    public Jugador getJugador(int id) { return jugadores.get(id); }
    public Map<Integer, Jugador> getJugadores() { return jugadores; }
    public void agregarPelota(Pelota p) { pelotas.put(p.getId(), p); }
    public Pelota getPelota(int id) { return pelotas.get(id); }
    public Map<Integer, Pelota> getPelotas() { return pelotas; }
    public void agregarZona(Zona z) { zonas.put(z.getId(), z); }
    public Zona getZona(int id) { return zonas.get(id); }
    public Map<Integer, Zona> getZonas() { return zonas; }
}