/*package com.proyecto.juegoudp.fisica;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Pelota;

/**
 * Sistema encargado de detectar y resolver colisiones entre fichas.
 *
 * Responsabilidades:
 * - Detectar cuando dos fichas se superponen
 * - Separarlas para evitar que ocupen el mismo espacio
 *
 * Este sistema opera únicamente sobre el estado del juego (EstadoJuego),
 * sin depender de otros sistemas, cumpliendo bajo acoplamiento (SOLID).
 */
/*
public class SistemaColisiones {

    private EstadoJuego estadoJuego;

    // Radio aproximado de cada ficha (para cálculo de colisión)
    private float radio = 20f;

    public SistemaColisiones(EstadoJuego estadoJuego) {
        this.estadoJuego = estadoJuego;
    }


    /**
     * Recorre todas las combinaciones de fichas para verificar colisiones.
     *
     * Se usa un doble bucle evitando repeticiones:
     * - i recorre todas las fichas
     * - j empieza desde i+1 para no comparar la misma pareja dos veces
     *//*
    public void actualizar() {

        // recorrer todas las parejas de fichas
        for (int i = 0; i < estadoJuego.getFichas().size(); i++) {

            Pelota a = estadoJuego.getFichas().get(i);

            for (int j = i + 1; j < estadoJuego.getFichas().size(); j++) {

                Pelota b = estadoJuego.getFichas().get(j);

                resolverColision(a, b);
            }
        }
    }


    /**
     * Detecta y resuelve la colisión entre dos fichas.
     *
     * @param a primera ficha
     * @param b segunda ficha
     */
/*
    private void resolverColision(Pelota a, Pelota b) {
        // Vector de diferencia entre posiciones
        float dx = b.getX() - a.getX();
        float dy = b.getY() - a.getY();
        // Vector de diferencia entre posiciones
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        // Distancia mínima permitida (dos radios)
        float minDist = radio * 2;

        /**
         * Condición de colisión:
         * - distancia menor a la mínima (se están solapando)
         * - distancia mayor que 0 (evita división por cero)
         *//*
        if (distancia < minDist && distancia > 0) {

            // Cantidad de superposición entre las fichas
            float overlap = minDist - distancia;

            // normalizar vector (direccion de separacion)
            float nx = dx / distancia;
            float ny = dy / distancia;

            /**
             * Separar ambas fichas en direcciones opuestas
             * Cada una se mueve la mitad del solapamiento
             *//*
            a.setX(a.getX() - nx * overlap / 2);
            a.setY(a.getY() - ny * overlap / 2);

            b.setX(b.getX() + nx * overlap / 2);
            b.setY(b.getY() + ny * overlap / 2);
        }
    }
}*/
