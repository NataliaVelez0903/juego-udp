package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.modelo.Pelota;
import java.util.function.BooleanSupplier;

/**
 * Conecta el gestor de estado y el arrastre con {@link ControladorEntradaJuego.EscuchaEntradaPartida}.
 */
public final class DelegadoEntradaPartida implements ControladorEntradaJuego.EscuchaEntradaPartida {
    private final GestorEstadoRedPartida gestorEstado;
    private final DatosArrastrePelota datosArrastre;
    private final boolean[] movimientoTeclas;
    private final BooleanSupplier partidaFinalizada;
    private final Runnable accionAlternarSilencioAudio;

    public DelegadoEntradaPartida(
            GestorEstadoRedPartida gestorEstado,
            DatosArrastrePelota datosArrastre,
            boolean[] movimientoTeclas,
            BooleanSupplier partidaFinalizada,
            Runnable accionAlternarSilencioAudio
    ) {
        this.gestorEstado = gestorEstado;
        this.datosArrastre = datosArrastre;
        this.movimientoTeclas = movimientoTeclas;
        this.partidaFinalizada = partidaFinalizada;
        this.accionAlternarSilencioAudio = accionAlternarSilencioAudio != null ? accionAlternarSilencioAudio : () -> {};
    }

    @Override
    public int obtenerIdJugador() {
        return gestorEstado.obtenerIdJugador();
    }

    @Override
    public boolean partidaEstaFinalizada() {
        return partidaFinalizada.getAsBoolean();
    }

    @Override
    public void configurarMovimiento(boolean arriba, boolean abajo, boolean izquierda, boolean derecha) {
        // El controlador escribe directamente en el arreglo WASD.
    }

    @Override
    public boolean[] obtenerEstadoTeclasMovimiento() {
        return movimientoTeclas;
    }

    @Override
    public void fijarPelotaArrastrada(Pelota pelota, float desplazamientoX, float desplazamientoY) {
        datosArrastre.fijarPelotaEnArrastre(pelota, desplazamientoX, desplazamientoY);
    }

    @Override
    public Pelota obtenerPelotaArrastrada() {
        return datosArrastre.obtenerPelotaEnArrastre();
    }

    @Override
    public float obtenerDesplazamientoX() {
        return datosArrastre.obtenerDesplazamientoX();
    }

    @Override
    public float obtenerDesplazamientoY() {
        return datosArrastre.obtenerDesplazamientoY();
    }

    @Override
    public void fijarDesplazamiento(float desplazamientoX, float desplazamientoY) {
        datosArrastre.fijarDesplazamiento(desplazamientoX, desplazamientoY);
    }

    @Override
    public void registrarUltimoArrastre(float x, float y, long instanteMilisegundos) {
        datosArrastre.registrarUltimoArrastre(x, y, instanteMilisegundos);
    }

    @Override
    public float obtenerUltimaPosicionArrastreX() {
        return datosArrastre.obtenerUltimaPosicionArrastreX();
    }

    @Override
    public float obtenerUltimaPosicionArrastreY() {
        return datosArrastre.obtenerUltimaPosicionArrastreY();
    }

    @Override
    public long obtenerInstanteUltimoArrastre() {
        return datosArrastre.obtenerInstanteUltimoArrastre();
    }

    @Override
    public void limpiarArrastrePelota() {
        datosArrastre.limpiarArrastre();
    }

    @Override
    public void alternarSilencioAudio() {
        accionAlternarSilencioAudio.run();
    }
}
