package com.proyecto.juegoudp.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.proyecto.juegoudp.JuegoPrincipal;

/**
 * Representa el punto de entrada de la aplicación en entorno de escritorio.
 *
 * Esta clase se encarga de crear y configurar la ventana principal del juego
 * cuando se ejecuta en la plataforma LWJGL3.
 *
 * Aquí se definen propiedades iniciales como el título de la ventana,
 * el tamaño inicial, la posibilidad de redimensionarla, la sincronización
 * vertical y la cantidad máxima de fotogramas por segundo.
 *
 * Una vez aplicada la configuración, se crea la instancia principal del juego.
 *
 * @author Luis
 * @version 1.0
 */
public class Lwjgl3Launcher {

    /**
     * Inicia la ejecución del juego en modo escritorio.
     *
     * Este método configura la ventana principal y lanza la aplicación
     * utilizando la clase principal del juego.
     *
     * @param args argumentos de línea de comandos recibidos al iniciar la aplicación
     */
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
