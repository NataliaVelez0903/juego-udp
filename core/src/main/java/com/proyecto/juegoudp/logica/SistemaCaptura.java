package com.proyecto.juegoudp.logica;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Ficha;
import com.proyecto.juegoudp.modelo.Zona;

/**
 * Sistema encargado de detectar cuando una ficha entra en una zona.
 *
 * Responsabilidades:
 * - Verificar si una ficha se encuentra dentro de una zona
 * - Marcar la ficha como capturada
 * - Asociar la ficha al jugador correspondiente
 *
 * Nota:
 * Este sistema NO depende de otros sistemas, solo del EstadoJuego,
 * siguiendo el principio de bajo acoplamiento (SOLID).
 */
public class SistemaCaptura {

    private EstadoJuego estadoJuego;

    public SistemaCaptura(EstadoJuego estadoJuego) {
        this.estadoJuego = estadoJuego;
    }

    /**
     * Recorre todas las fichas del juego y verifica si alguna ha entrado en una zona.
     * Si una ficha entra en una zona:
     * - Se marca como capturada
     * - Se asigna el jugador propietario de la zona
     * - Se evita que vuelva a capturarse nuevamente
     */
    public void actualizar (){
        for (Ficha f : estadoJuego.getFichas()){
            // Evita procesar fichas que ya fueron capturadas previamente
            if (f.isCapturada()) continue;
            for (Zona z : estadoJuego.getZonas()) {
                // Verifica si la posición de la ficha está dentro de la zona
                if (z.contiene(f.getX(), f.getY())){
                    // marca la ficha como capturada
                    f.setCapturada(true);
                    // Asocia la ficha al jugador dueño de la zona
                    f.setCapturadaPor(z.getJugadorId());

                    System.out.println("ficha capturada por jugador" + z.getJugadorId());
                    // Verifica si la posición de la ficha está dentro de la zona
                    break;
                }
            }
        }
    }
}

