package com.proyecto.juegoudp.logica;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Ficha;

public class SistemaColisiones {

    private EstadoJuego estadoJuego;
    private float radio = 20f;

    public SistemaColisiones(EstadoJuego estadoJuego) {
        this.estadoJuego = estadoJuego;
    }

    public void actualizar() {

        // recorrer todas las parejas de fichas
        for (int i = 0; i < estadoJuego.getFichas().size(); i++) {

            Ficha a = estadoJuego.getFichas().get(i);

            for (int j = i + 1; j < estadoJuego.getFichas().size(); j++) {

                Ficha b = estadoJuego.getFichas().get(j);

                resolverColision(a, b);
            }
        }
    }

    private void resolverColision(Ficha a, Ficha b) {

        float dx = b.getX() - a.getX();
        float dy = b.getY() - a.getY();

        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        float minDist = radio * 2;

        if (distancia < minDist && distancia > 0) {

            float overlap = minDist - distancia;

            // normalizar vector
            float nx = dx / distancia;
            float ny = dy / distancia;

            // separar fichas (cada una se mueve la mitad)
            a.setX(a.getX() - nx * overlap / 2);
            a.setY(a.getY() - ny * overlap / 2);

            b.setX(b.getX() + nx * overlap / 2);
            b.setY(b.getY() + ny * overlap / 2);
        }
    }
}
