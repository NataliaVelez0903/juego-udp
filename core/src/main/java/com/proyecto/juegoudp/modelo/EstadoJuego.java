package com.proyecto.juegoudp.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el estado global del juego en un momento dado.
 *
 * Esta clase es el núcleo del sistema, ya que todos los sistemas
 * (arrastre, colisiones, captura, red, etc.) leen y modifican
 * esta información.
 *
 * Responsabilidades:
 * - Almacenar todas las entidades del juego (fichas, zonas, etc.)
 * - Servir como punto central de comunicación entre sistemas
 *
 * Diseño:
 * - Bajo acoplamiento: los sistemas no se comunican entre sí,
 *   solo a través de EstadoJuego.
 * - Alta cohesión: esta clase solo representa datos del juego.
 */
public class EstadoJuego {
    // Lista de todas las fichas activas en el juego
    private List<Ficha> fichas = new ArrayList<>();
    // Lista de todas las fichas activas en el juego
    private List<Zona> zonas;

    public EstadoJuego(){
        fichas = new ArrayList<>();
        zonas = new ArrayList<>();
    }

    // Getters
    public List<Zona> getZonas() {
        return zonas;
    }

    public List<Ficha> getFichas(){
        return fichas;
    }


    public void agregarFicha (Ficha ficha){
        fichas.add(ficha);
    }

    public void agregarZona (Zona zona){
        zonas.add(zona);
    }

}
