package com.proyecto.juegoudp.pantallas.juego;

/**
 * Abstrae el renderizado de la partida (libGDX) para poder sustituir o testear la vista.
 */
public interface IRenderizadorPartida {
    void render(float delta);
    void resize(int width, int height);
    void dispose();
}

