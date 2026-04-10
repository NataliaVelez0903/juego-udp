package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.modelo.Pelota;
import java.util.function.BooleanSupplier;

/**
 * Implementa la interfaz de escucha de entrada de la partida,
 * delegando las operaciones hacia los componentes encargados
 * del estado de red, el arrastre de pelotas y el control auxiliar
 * de la pantalla.
 *
 * Esta clase actúa como intermediaria entre el controlador de entrada
 * del juego y los objetos que realmente almacenan o gestionan la
 * información relacionada con el jugador, el arrastre de pelotas,
 * el estado de finalización de la partida y el control del audio.
 *
 * Su propósito es desacoplar la lógica del controlador de entrada
 * de las estructuras concretas donde se conserva el estado,
 * facilitando una comunicación más organizada entre componentes.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public final class DelegadoEntradaPartida implements ControladorEntradaJuego.EscuchaEntradaPartida {

    /**
     * Gestor encargado de proporcionar información del estado de red
     * de la partida, como el identificador del jugador actual.
     */
    private final GestorEstadoRedPartida gestorEstado;

    /**
     * Objeto que almacena y administra la información relacionada
     * con el arrastre de pelotas.
     */
    private final DatosArrastrePelota datosArrastre;

    /**
     * Arreglo que representa el estado de las teclas de movimiento
     * utilizadas por el jugador.
     */
    private final boolean[] movimientoTeclas;

    /**
     * Proveedor que indica si la partida ya ha finalizado.
     */
    private final BooleanSupplier partidaFinalizada;

    /**
     * Acción asociada al cambio de estado de silencio del audio.
     */
    private final Runnable accionAlternarSilencioAudio;

    /**
     * Construye una nueva instancia delegada para la entrada de la partida.
     *
     * @param gestorEstado gestor encargado del estado de red del jugador
     * @param datosArrastre objeto que almacena la información del arrastre de pelotas
     * @param movimientoTeclas arreglo que contiene el estado de las teclas de movimiento
     * @param partidaFinalizada proveedor que indica si la partida ha finalizado
     * @param accionAlternarSilencioAudio acción a ejecutar para alternar el silencio del audio;
     *                                    si es null, se reemplaza por una acción vacía
     */
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

    /**
     * Obtiene el identificador del jugador actual.
     *
     * @return el identificador del jugador obtenido desde el gestor de estado
     */
    @Override
    public int obtenerIdJugador() {
        return gestorEstado.obtenerIdJugador();
    }

    /**
     * Indica si la partida ha finalizado.
     *
     * @return true si la partida ha terminado; false en caso contrario
     */
    @Override
    public boolean partidaEstaFinalizada() {
        return partidaFinalizada.getAsBoolean();
    }

    /**
     * Configura el movimiento del jugador.
     *
     * En esta implementación, el controlador modifica directamente
     * el arreglo de teclas de movimiento, por lo que este método
     * no realiza ninguna acción.
     *
     * @param arriba indica movimiento hacia arriba
     * @param abajo indica movimiento hacia abajo
     * @param izquierda indica movimiento hacia la izquierda
     * @param derecha indica movimiento hacia la derecha
     */
    @Override
    public void configurarMovimiento(boolean arriba, boolean abajo, boolean izquierda, boolean derecha) {
        // El controlador escribe directamente en el arreglo WASD.
    }

    /**
     * Obtiene el arreglo con el estado actual de las teclas de movimiento.
     *
     * @return el arreglo de teclas de movimiento
     */
    @Override
    public boolean[] obtenerEstadoTeclasMovimiento() {
        return movimientoTeclas;
    }

    /**
     * Establece la pelota actualmente arrastrada y sus desplazamientos asociados.
     *
     * @param pelota la pelota que entra en arrastre
     * @param desplazamientoX desplazamiento horizontal relativo
     * @param desplazamientoY desplazamiento vertical relativo
     */
    @Override
    public void fijarPelotaArrastrada(Pelota pelota, float desplazamientoX, float desplazamientoY) {
        datosArrastre.fijarPelotaEnArrastre(pelota, desplazamientoX, desplazamientoY);
    }

    /**
     * Obtiene la pelota que se encuentra actualmente en arrastre.
     *
     * @return la pelota en arrastre, o null si no existe
     */
    @Override
    public Pelota obtenerPelotaArrastrada() {
        return datosArrastre.obtenerPelotaEnArrastre();
    }

    /**
     * Obtiene el desplazamiento horizontal actual asociado al arrastre.
     *
     * @return el desplazamiento horizontal
     */
    @Override
    public float obtenerDesplazamientoX() {
        return datosArrastre.obtenerDesplazamientoX();
    }

    /**
     * Obtiene el desplazamiento vertical actual asociado al arrastre.
     *
     * @return el desplazamiento vertical
     */
    @Override
    public float obtenerDesplazamientoY() {
        return datosArrastre.obtenerDesplazamientoY();
    }

    /**
     * Actualiza los desplazamientos actuales asociados al arrastre.
     *
     * @param desplazamientoX nuevo desplazamiento horizontal
     * @param desplazamientoY nuevo desplazamiento vertical
     */
    @Override
    public void fijarDesplazamiento(float desplazamientoX, float desplazamientoY) {
        datosArrastre.fijarDesplazamiento(desplazamientoX, desplazamientoY);
    }

    /**
     * Registra la última posición conocida y el instante del arrastre.
     *
     * @param x posición horizontal registrada
     * @param y posición vertical registrada
     * @param instanteMilisegundos instante del registro en milisegundos
     */
    @Override
    public void registrarUltimoArrastre(float x, float y, long instanteMilisegundos) {
        datosArrastre.registrarUltimoArrastre(x, y, instanteMilisegundos);
    }

    /**
     * Obtiene la última posición horizontal registrada durante el arrastre.
     *
     * @return la última coordenada horizontal registrada
     */
    @Override
    public float obtenerUltimaPosicionArrastreX() {
        return datosArrastre.obtenerUltimaPosicionArrastreX();
    }

    /**
     * Obtiene la última posición vertical registrada durante el arrastre.
     *
     * @return la última coordenada vertical registrada
     */
    @Override
    public float obtenerUltimaPosicionArrastreY() {
        return datosArrastre.obtenerUltimaPosicionArrastreY();
    }

    /**
     * Obtiene el instante del último arrastre registrado.
     *
     * @return el instante en milisegundos del último arrastre
     */
    @Override
    public long obtenerInstanteUltimoArrastre() {
        return datosArrastre.obtenerInstanteUltimoArrastre();
    }

    /**
     * Limpia el estado actual de arrastre de la pelota.
     */
    @Override
    public void limpiarArrastrePelota() {
        datosArrastre.limpiarArrastre();
    }

    /**
     * Ejecuta la acción encargada de alternar el estado de silencio del audio.
     */
    @Override
    public void alternarSilencioAudio() {
        accionAlternarSilencioAudio.run();
    }
}
