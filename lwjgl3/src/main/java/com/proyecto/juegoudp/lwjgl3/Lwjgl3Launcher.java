package com.proyecto.juegoudp.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.proyecto.juegoudp.JuegoPrincipal;
/**
 * Punto de entrada del escritorio: ventana redimensionable; el juego escala el mundo 1024×768 al maximizar o a pantalla completa.
 */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Peloteros - Multijugador UDP");
        config.setWindowedMode(1280, 800);
        config.setResizable(true);
        config.useVsync(true);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new JuegoPrincipal(), config);
    }
}