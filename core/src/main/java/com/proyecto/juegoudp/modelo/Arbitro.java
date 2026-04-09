package com.proyecto.juegoudp.modelo;

import com.badlogic.gdx.math.MathUtils;

/**
 * Representa la entidad encargada de supervisar el juego.
 * El árbitro persigue constantemente al jugador más cercano y aplica penalizaciones
 * si logra alcanzarlo.
 * @author Sebastian<sebastian.villanedag@autonoma.edu.co>
 * @author Natalia<natalia.velezo@autonoma.edu.co>
 * @author Luis<luisc.gallegom@autonoma.edu.co>
 * @author Juan<juanj.giraldot@autonoma.edu.co>
 *  @version 1.0
 *  @since 04/04/2026
 */
public class Arbitro {
    private float x, y;
    private float vx, vy;
    private float velocidad = 120f;
    private float tiempoCambioDireccion = 0;
    private final float radioColision = 25f; // Ajusta según el tamaño de tu imagen

    public Arbitro(float x, float y) {
        this.x = x;
        this.y = y;
        cambiarDireccionAleatoria();
    }

    /**
     * Mueve al árbitro de forma aleatoria por el mapa.
     */
    public void actualizarAleatorio(float delta) {
        x += vx * delta;
        y += vy * delta;

        // Rebote en bordes (Pantalla 1024x768)
        if (x < 30 || x > 994) { vx = -vx; x = MathUtils.clamp(x, 30, 994); }
        if (y < 30 || y > 738) { vy = -vy; y = MathUtils.clamp(y, 30, 738); }

        tiempoCambioDireccion -= delta;
        if (tiempoCambioDireccion <= 0) {
            cambiarDireccionAleatoria();
        }
    }

    /**
     * Verifica si el árbitro tocó a un jugador y le resta puntos.
     */
    public boolean verificarColision(Jugador j) {
        float distancia = (float) Math.hypot(j.getX() - x, j.getY() - y);
        if (distancia < radioColision) {
            // Penalización: Restar 10 puntos (mínimo 0)
            j.setPuntaje(Math.max(0, j.getPuntaje() - 10));
            return true;
        }
        return false;
    }

    private void cambiarDireccionAleatoria() {
        float angulo = MathUtils.random(0, MathUtils.PI2);
        vx = MathUtils.cos(angulo) * velocidad;
        vy = MathUtils.sin(angulo) * velocidad;
        tiempoCambioDireccion = MathUtils.random(1.5f, 4.0f);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }

    public void setTiempoCambioDireccion(float tiempoCambioDireccion) {
        this.tiempoCambioDireccion = tiempoCambioDireccion;
    }

    public float getVx() {
        return vx;
    }

    public float getVy() {
        return vy;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public float getTiempoCambioDireccion() {
        return tiempoCambioDireccion;
    }

    public float getRadioColision() {
        return radioColision;
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
