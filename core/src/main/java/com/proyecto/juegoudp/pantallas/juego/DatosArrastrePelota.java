package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.modelo.Pelota;

/**
 * Representa la información asociada al arrastre de una pelota con el ratón.
 *
 * Esta clase almacena el estado temporal necesario para gestionar
 * la interacción de arrastre durante la partida, incluyendo la pelota
 * seleccionada, los desplazamientos relativos respecto al punto de toque
 * y los últimos datos registrados del movimiento realizado.
 *
 * Su propósito es conservar la información necesaria para calcular
 * correctamente la posición de la pelota mientras se arrastra y,
 * posteriormente, estimar valores relacionados con su liberación,
 * como la velocidad resultante del movimiento.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public final class DatosArrastrePelota {

    /**
     * Pelota que actualmente se encuentra en proceso de arrastre.
     */
    private Pelota pelotaEnArrastre;

    /**
     * Desplazamiento horizontal entre el punto de contacto y la posición
     * real de la pelota al iniciar el arrastre.
     */
    private float desplazamientoX;

    /**
     * Desplazamiento vertical entre el punto de contacto y la posición
     * real de la pelota al iniciar el arrastre.
     */
    private float desplazamientoY;

    /**
     * Última posición horizontal registrada durante el arrastre.
     */
    private float ultimaPosicionArrastreX;

    /**
     * Última posición vertical registrada durante el arrastre.
     */
    private float ultimaPosicionArrastreY;

    /**
     * Instante, en milisegundos, en que se registró el último movimiento
     * de arrastre.
     */
    private long instanteUltimoArrastreMs;

    /**
     * Obtiene la pelota que se encuentra actualmente en arrastre.
     *
     * @return la pelota en arrastre, o null si no existe una pelota seleccionada
     */
    public Pelota obtenerPelotaEnArrastre() {
        return pelotaEnArrastre;
    }

    /**
     * Establece la pelota actualmente arrastrada junto con los
     * desplazamientos relativos asociados al inicio del arrastre.
     *
     * @param pelota la pelota que pasa a estar en arrastre
     * @param desplazamientoX desplazamiento horizontal relativo
     * @param desplazamientoY desplazamiento vertical relativo
     */
    public void fijarPelotaEnArrastre(Pelota pelota, float desplazamientoX, float desplazamientoY) {
        this.pelotaEnArrastre = pelota;
        this.desplazamientoX = desplazamientoX;
        this.desplazamientoY = desplazamientoY;
    }

    /**
     * Obtiene el desplazamiento horizontal actual del arrastre.
     *
     * @return el desplazamiento horizontal
     */
    public float obtenerDesplazamientoX() {
        return desplazamientoX;
    }

    /**
     * Obtiene el desplazamiento vertical actual del arrastre.
     *
     * @return el desplazamiento vertical
     */
    public float obtenerDesplazamientoY() {
        return desplazamientoY;
    }

    /**
     * Actualiza los desplazamientos asociados al arrastre actual.
     *
     * @param desplazamientoX nuevo desplazamiento horizontal
     * @param desplazamientoY nuevo desplazamiento vertical
     */
    public void fijarDesplazamiento(float desplazamientoX, float desplazamientoY) {
        this.desplazamientoX = desplazamientoX;
        this.desplazamientoY = desplazamientoY;
    }

    /**
     * Registra la última posición conocida de la pelota durante el arrastre
     * y el instante en que dicho movimiento ocurrió.
     *
     * @param x posición horizontal registrada
     * @param y posición vertical registrada
     * @param instanteMilisegundos instante del registro en milisegundos
     */
    public void registrarUltimoArrastre(float x, float y, long instanteMilisegundos) {
        ultimaPosicionArrastreX = x;
        ultimaPosicionArrastreY = y;
        instanteUltimoArrastreMs = instanteMilisegundos;
    }

    /**
     * Obtiene la última posición horizontal registrada durante el arrastre.
     *
     * @return la última coordenada horizontal registrada
     */
    public float obtenerUltimaPosicionArrastreX() {return ultimaPosicionArrastreX;}

    /**
     * Obtiene la última posición vertical registrada durante el arrastre.
     *
     * @return la última coordenada vertical registrada
     */
    public float obtenerUltimaPosicionArrastreY() {return ultimaPosicionArrastreY;}

    /**
     * Obtiene el instante del último movimiento registrado durante el arrastre.
     *
     * @return el instante en milisegundos del último arrastre registrado
     */
    public long obtenerInstanteUltimoArrastre() {return instanteUltimoArrastreMs;}

    /**
     * Limpia el estado actual de arrastre.
     *
     * Este método elimina la referencia a la pelota que se encontraba
     * en arrastre, indicando que ya no existe una operación de arrastre activa.
     */
    public void limpiarArrastre() {pelotaEnArrastre = null;}
}
