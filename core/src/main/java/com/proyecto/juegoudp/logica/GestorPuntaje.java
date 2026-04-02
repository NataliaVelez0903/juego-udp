package com.proyecto.juegoudp.logica;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.pantallas.PantallaJuego;


//logica para el puntaje
public class GestorPuntaje {
    private EstadoJuego estadoJuego;

    public GestorPuntaje(EstadoJuego estadoJuego) {
        this.estadoJuego = estadoJuego;
    }

    public void sumarPunto(int jugadorId, int puntos) {

        for (Jugador j : estadoJuego.getJugadores()) {

            if (j.getId() == jugadorId) {
                j.setPuntaje(j.getPuntaje() + puntos);

                System.out.println(
                    "Punto para jugador " + jugadorId +
                        " +" + puntos +
                        " total: " + j.getPuntaje()
                );
            }
        }
    }

}
