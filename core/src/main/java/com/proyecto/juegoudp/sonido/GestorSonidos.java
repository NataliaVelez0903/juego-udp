package com.proyecto.juegoudp.sonido;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/**
 * Clase responsable de gestionar todos los sonidos del juego
 * Principio de Responsabilidad Única (SRP)
 */
public class GestorSonidos {
    private static GestorSonidos instancia;
    private Sound sonidoGol;
    private boolean disponible = true;

    private GestorSonidos() {
        cargarSonidos();
    }

    /**
     * Patrón Singleton para tener una única instancia del gestor
     */
    public static GestorSonidos getInstancia() {
        if (instancia == null) {
            instancia = new GestorSonidos();
        }
        return instancia;
    }

    /**
     * Carga todos los sonidos del juego
     */
    private void cargarSonidos() {
        try {
            FileHandle archivoSonido = Gdx.files.internal("sonidos/gol.wav");
            if (archivoSonido.exists()) {
                sonidoGol = Gdx.audio.newSound(archivoSonido);
                System.out.println("[GestorSonidos] Sonido de gol cargado correctamente");
            } else {
                System.err.println("[GestorSonidos] No se encontró el archivo: sonidos/gol.wav");
                disponible = false;
            }
        } catch (Exception e) {
            System.err.println("[GestorSonidos] Error al cargar el sonido: " + e.getMessage());
            disponible = false;
        }
    }

    /**
     * Reproduce el sonido de gol
     */
    public void reproducirGol() {
        if (disponible && sonidoGol != null) {
            try {
                sonidoGol.play(1.0f); // Volumen al 100%
            } catch (Exception e) {
                System.err.println("[GestorSonidos] Error al reproducir sonido: " + e.getMessage());
            }
        }
    }

    /**
     * Libera los recursos de sonido
     */
    public void dispose() {
        if (sonidoGol != null) {
            sonidoGol.dispose();
            sonidoGol = null;
        }
    }
}
