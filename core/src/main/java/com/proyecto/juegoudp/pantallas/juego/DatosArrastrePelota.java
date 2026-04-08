package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.modelo.Pelota;

/**
 * Estado de arrastre de pelota con el ratón.
 */
public final class DatosArrastrePelota {
    private Pelota pelotaEnArrastre;
    private float desplazamientoX;
    private float desplazamientoY;
    private float ultimaPosicionArrastreX;
    private float ultimaPosicionArrastreY;
    private long instanteUltimoArrastreMs;

    public Pelota obtenerPelotaEnArrastre() {
        return pelotaEnArrastre;
    }

    public void fijarPelotaEnArrastre(Pelota pelota, float desplazamientoX, float desplazamientoY) {
        this.pelotaEnArrastre = pelota;
        this.desplazamientoX = desplazamientoX;
        this.desplazamientoY = desplazamientoY;
    }

    public float obtenerDesplazamientoX() {
        return desplazamientoX;
    }

    public float obtenerDesplazamientoY() {
        return desplazamientoY;
    }

    public void fijarDesplazamiento(float desplazamientoX, float desplazamientoY) {
        this.desplazamientoX = desplazamientoX;
        this.desplazamientoY = desplazamientoY;
    }

    public void registrarUltimoArrastre(float x, float y, long instanteMilisegundos) {
        ultimaPosicionArrastreX = x;
        ultimaPosicionArrastreY = y;
        instanteUltimoArrastreMs = instanteMilisegundos;
    }

    public float obtenerUltimaPosicionArrastreX() {
        return ultimaPosicionArrastreX;
    }

    public float obtenerUltimaPosicionArrastreY() {
        return ultimaPosicionArrastreY;
    }

    public long obtenerInstanteUltimoArrastre() {
        return instanteUltimoArrastreMs;
    }

    public void limpiarArrastre() {
        pelotaEnArrastre = null;
    }
}
