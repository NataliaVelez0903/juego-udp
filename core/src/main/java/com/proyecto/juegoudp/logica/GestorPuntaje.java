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

    public void sumarPunto(int jugadorId) {

        for (Jugador j : estadoJuego.getJugadores()) {

            if (j.getId() == jugadorId) {
                j.sumarPunto();

                System.out.println("Punto para jugador " + jugadorId +
                    " total: " + j.getPuntaje());
            }
        }
    }

}
