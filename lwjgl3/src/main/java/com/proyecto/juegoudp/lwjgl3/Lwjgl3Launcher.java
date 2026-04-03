package com.proyecto.juegoudp.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.proyecto.juegoudp.JuegoPrincipal;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Peloteros - Multijugador UDP");
        config.setWindowedMode(1024, 768);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new JuegoPrincipal(), config);
    }
}