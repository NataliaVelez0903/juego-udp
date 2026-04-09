package com.proyecto.juegoudp.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Estado compartido del juego: jugadores, pelotas, zonas y el árbitro.
 * Actúa como el contenedor principal del modelo de dominio.
 */
public class EstadoJuego {
    private Map<Integer, Jugador> jugadores;
    private Map<Integer, Pelota> pelotas;
    private Map<Integer, Zona> zonas;
    private List<Arbitro> arbitros;

    public EstadoJuego() {
        jugadores = new ConcurrentHashMap<>();
        pelotas = new ConcurrentHashMap<>();
        zonas = new ConcurrentHashMap<>();
        arbitros = new ArrayList<>();

        // Inicializamos los 2 árbitros en posiciones distintas
        arbitros.add(new Arbitro(300, 384));
        arbitros.add(new Arbitro(700, 384));
    }

    public List<Arbitro> getArbitros() {
        return arbitros;
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

    /**
     * Obtiene la entidad del árbitro que supervisa la partida.
     * Retorna el primero de la lista para mantener compatibilidad.
     * @return El {@link Arbitro} actual.
     */
    public Arbitro getArbitro() {
        return arbitros.isEmpty() ? null : arbitros.get(0);
    }

    /**
     * Actualiza la instancia del árbitro (útil para sincronización de red).
     * @param arbitro El nuevo estado del árbitro.
     */
    public void setArbitro(Arbitro arbitro) {
        if (!arbitros.isEmpty()) {
            arbitros.set(0, arbitro);
        } else {
            arbitros.add(arbitro);
        }
    }
}
