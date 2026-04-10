package com.proyecto.juegoudp.pantallas.juego;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.TipoMensaje;

/**
 * Controla la entrada del jugador durante la partida.
 *
 * Esta clase gestiona la interacción del usuario mediante teclado y ratón,
 * permitiendo el movimiento del jugador y la manipulación de pelotas
 * dentro del escenario.
 *
 * Se encarga de traducir las acciones del usuario en cambios locales
 * del estado del juego y en mensajes enviados al servidor para mantener
 * la sincronización con los demás jugadores.
 *
 * Extiende InputAdapter de libGDX para capturar eventos de entrada.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class ControladorEntradaJuego extends InputAdapter implements IControladorEntradaJuego {

    /**
     * Define el contrato de comunicación entre el controlador de entrada
     * y la pantalla de juego.
     *
     * Esta interfaz permite consultar el estado actual del jugador,
     * actualizar movimientos, gestionar el arrastre de pelotas y
     * notificar acciones relevantes como el control del audio.
     */
    public interface EscuchaEntradaPartida {

        /**
         * Obtiene el identificador del jugador actual.
         *
         * @return el identificador del jugador
         */
        int obtenerIdJugador();

        /**
         * Indica si la partida ha finalizado.
         *
         * @return true si la partida ha terminado; false en caso contrario
         */
        boolean partidaEstaFinalizada();

        /**
         * Configura el estado de movimiento del jugador.
         *
         * @param arriba indica movimiento hacia arriba
         * @param abajo indica movimiento hacia abajo
         * @param izquierda indica movimiento hacia la izquierda
         * @param derecha indica movimiento hacia la derecha
         */
        void configurarMovimiento(boolean arriba, boolean abajo, boolean izquierda, boolean derecha);

        /**
         * Obtiene el estado actual de las teclas de movimiento.
         *
         * @return arreglo de estados de teclas (W, S, A, D)
         */
        boolean[] obtenerEstadoTeclasMovimiento();

        /**
         * Establece la pelota actualmente arrastrada por el jugador.
         *
         * @param pelota la pelota seleccionada
         * @param desplazamientoX desplazamiento horizontal relativo
         * @param desplazamientoY desplazamiento vertical relativo
         */
        void fijarPelotaArrastrada(Pelota pelota, float desplazamientoX, float desplazamientoY);

        /**
         * Obtiene la pelota actualmente arrastrada.
         *
         * @return la pelota en arrastre, o null si no hay
         */
        Pelota obtenerPelotaArrastrada();

        /**
         * Obtiene el desplazamiento horizontal actual.
         *
         * @return desplazamiento en X
         */
        float obtenerDesplazamientoX();

        /**
         * Obtiene el desplazamiento vertical actual.
         *
         * @return desplazamiento en Y
         */
        float obtenerDesplazamientoY();

        /**
         * Define el desplazamiento actual de arrastre.
         *
         * @param desplazamientoX desplazamiento horizontal
         * @param desplazamientoY desplazamiento vertical
         */
        void fijarDesplazamiento(float desplazamientoX, float desplazamientoY);

        /**
         * Registra la última posición de arrastre junto con el instante.
         *
         * @param x posición horizontal
         * @param y posición vertical
         * @param instanteMilisegundos instante en milisegundos
         */
        void registrarUltimoArrastre(float x, float y, long instanteMilisegundos);

        /**
         * Obtiene la última posición horizontal registrada durante el arrastre.
         *
         * @return coordenada X
         */
        float obtenerUltimaPosicionArrastreX();

        /**
         * Obtiene la última posición vertical registrada durante el arrastre.
         *
         * @return coordenada Y
         */
        float obtenerUltimaPosicionArrastreY();

        /**
         * Obtiene el instante del último evento de arrastre.
         *
         * @return instante en milisegundos
         */
        long obtenerInstanteUltimoArrastre();

        /**
         * Limpia el estado de arrastre de la pelota.
         */
        void limpiarArrastrePelota();

        /**
         * Alterna el estado de silencio global del audio.
         */
        void alternarSilencioAudio();
    }

    private final Viewport viewport;
    private final EstadoJuego estadoLocal;
    private final ClienteUDP cliente;
    private final EscuchaEntradaPartida escucha;

    /**
     * Construye el controlador de entrada del juego.
     *
     * @param viewport viewport utilizado para transformar coordenadas de pantalla
     * @param estadoLocal estado local del juego
     * @param cliente cliente UDP para enviar acciones al servidor
     * @param escucha interfaz de comunicación con la pantalla
     */
    public ControladorEntradaJuego(
        Viewport viewport,
        EstadoJuego estadoLocal,
        ClienteUDP cliente,
        EscuchaEntradaPartida escucha
    ) {
        this.viewport = viewport;
        this.estadoLocal = estadoLocal;
        this.cliente = cliente;
        this.escucha = escucha;
    }

    /**
     * Maneja la presión de teclas.
     */
    @Override
    public boolean keyDown(int codigoTecla) {
        if (escucha.partidaEstaFinalizada()) {
            return false;
        }
        if (codigoTecla == Keys.M) {
            escucha.alternarSilencioAudio();
            return true;
        }
        int idJugador = escucha.obtenerIdJugador();
        if (idJugador < 0) {
            return true;
        }

        boolean[] teclas = escucha.obtenerEstadoTeclasMovimiento();
        if (codigoTecla == Keys.W) {
            teclas[0] = true;
        }
        if (codigoTecla == Keys.S) {
            teclas[1] = true;
        }
        if (codigoTecla == Keys.A) {
            teclas[2] = true;
        }
        if (codigoTecla == Keys.D) {
            teclas[3] = true;
        }
        return true;
    }

    /**
     * Maneja la liberación de teclas.
     */
    @Override
    public boolean keyUp(int codigoTecla) {
        boolean[] teclas = escucha.obtenerEstadoTeclasMovimiento();
        if (codigoTecla == Keys.W) {
            teclas[0] = false;
        }
        if (codigoTecla == Keys.S) {
            teclas[1] = false;
        }
        if (codigoTecla == Keys.A) {
            teclas[2] = false;
        }
        if (codigoTecla == Keys.D) {
            teclas[3] = false;
        }
        return true;
    }

    /**
     * Maneja el inicio del toque o clic del ratón.
     */
    @Override
    public boolean touchDown(int posicionPantallaX, int posicionPantallaY, int puntero, int boton) {
        if (escucha.partidaEstaFinalizada()) {
            return false;
        }
        int idJugador = escucha.obtenerIdJugador();
        if (idJugador < 0) {
            return true;
        }

        Vector3 toque = new Vector3(posicionPantallaX, posicionPantallaY, 0);
        viewport.unproject(toque);

        for (Pelota pelota : estadoLocal.getPelotas().values()) {
            if (pelota.getIdJugador() != -1) {
                continue;
            }
            if (Math.hypot(toque.x - pelota.getX(), toque.y - pelota.getY()) < 20) {
                float desplX = pelota.getX() - toque.x;
                float desplY = pelota.getY() - toque.y;
                escucha.fijarPelotaArrastrada(pelota, desplX, desplY);
                escucha.registrarUltimoArrastre(pelota.getX(), pelota.getY(), System.currentTimeMillis());
                cliente.enviarMensaje(new Mensaje(TipoMensaje.TOMAR_PELOTA, idJugador, pelota.getId(), 0, 0, 0, 0, ""));
                break;
            }
        }
        return true;
    }

    /**
     * Maneja el arrastre del ratón.
     */
    @Override
    public boolean touchDragged(int posicionPantallaX, int posicionPantallaY, int puntero) {
        if (escucha.partidaEstaFinalizada()) {
            return false;
        }
        int idJugador = escucha.obtenerIdJugador();
        Pelota pelota = escucha.obtenerPelotaArrastrada();
        if (idJugador < 0 || pelota == null) {
            return true;
        }

        Vector3 toque = new Vector3(posicionPantallaX, posicionPantallaY, 0);
        viewport.unproject(toque);
        float nuevaX = toque.x + escucha.obtenerDesplazamientoX();
        float nuevaY = toque.y + escucha.obtenerDesplazamientoY();
        nuevaX = Math.max(20, Math.min(1004, nuevaX));
        nuevaY = Math.max(20, Math.min(748, nuevaY));
        pelota.setX(nuevaX);
        pelota.setY(nuevaY);
        escucha.registrarUltimoArrastre(nuevaX, nuevaY, System.currentTimeMillis());

        cliente.enviarMensaje(new Mensaje(TipoMensaje.MOVER_PELOTA, idJugador, pelota.getId(), nuevaX, nuevaY, 0, 0, ""));
        return true;
    }

    /**
     * Maneja la liberación del toque o clic del ratón.
     */
    @Override
    public boolean touchUp(int posicionPantallaX, int posicionPantallaY, int puntero, int boton) {
        Pelota pelota = escucha.obtenerPelotaArrastrada();
        if (pelota == null) {
            return true;
        }
        int idJugador = escucha.obtenerIdJugador();
        if (idJugador >= 0) {
            Vector3 toque = new Vector3(posicionPantallaX, posicionPantallaY, 0);
            viewport.unproject(toque);
            float nuevaX = Math.max(20, Math.min(1004, toque.x + escucha.obtenerDesplazamientoX()));
            float nuevaY = Math.max(20, Math.min(748, toque.y + escucha.obtenerDesplazamientoY()));
            long ahora = System.currentTimeMillis();
            float deltaTiempo = Math.max(0.016f, (ahora - escucha.obtenerInstanteUltimoArrastre()) / 1000f);
            float velocidadX = (nuevaX - escucha.obtenerUltimaPosicionArrastreX()) / deltaTiempo;
            float velocidadY = (nuevaY - escucha.obtenerUltimaPosicionArrastreY()) / deltaTiempo;
            cliente.enviarMensaje(new Mensaje(TipoMensaje.SOLTAR_PELOTA, idJugador, pelota.getId(), 0, 0, velocidadX, velocidadY, ""));
        }
        escucha.limpiarArrastrePelota();
        return true;
    }
}
