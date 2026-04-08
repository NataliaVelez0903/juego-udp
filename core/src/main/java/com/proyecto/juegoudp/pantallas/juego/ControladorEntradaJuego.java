package com.proyecto.juegoudp.pantallas.juego;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.red.ClienteUdp;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.TipoMensaje;

/**
 * Entrada del jugador: teclado (WASD) y ratón para arrastrar la pelota.
 */
public class ControladorEntradaJuego extends InputAdapter implements IControladorEntradaJuego {

    /**
     * Retroalimentación hacia la pantalla (estado de movimiento y arrastre).
     */
    public interface EscuchaEntradaPartida {
        int obtenerIdJugador();

        boolean partidaEstaFinalizada();

        void configurarMovimiento(boolean arriba, boolean abajo, boolean izquierda, boolean derecha);

        boolean[] obtenerEstadoTeclasMovimiento();

        void fijarPelotaArrastrada(Pelota pelota, float desplazamientoX, float desplazamientoY);

        Pelota obtenerPelotaArrastrada();

        float obtenerDesplazamientoX();

        float obtenerDesplazamientoY();

        void fijarDesplazamiento(float desplazamientoX, float desplazamientoY);

        void registrarUltimoArrastre(float x, float y, long instanteMilisegundos);

        float obtenerUltimaPosicionArrastreX();

        float obtenerUltimaPosicionArrastreY();

        long obtenerInstanteUltimoArrastre();

        void limpiarArrastrePelota();

        /** Tecla M: alterna silencio global (música y efectos). */
        void alternarSilencioAudio();
    }

    private final OrthographicCamera camara;
    private final EstadoJuego estadoLocal;
    private final ClienteUdp cliente;
    private final EscuchaEntradaPartida escucha;

    public ControladorEntradaJuego(
            OrthographicCamera camara,
            EstadoJuego estadoLocal,
            ClienteUdp cliente,
            EscuchaEntradaPartida escucha
    ) {
        this.camara = camara;
        this.estadoLocal = estadoLocal;
        this.cliente = cliente;
        this.escucha = escucha;
    }

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
        camara.unproject(toque);

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
        camara.unproject(toque);
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

    @Override
    public boolean touchUp(int posicionPantallaX, int posicionPantallaY, int puntero, int boton) {
        Pelota pelota = escucha.obtenerPelotaArrastrada();
        if (pelota == null) {
            return true;
        }
        int idJugador = escucha.obtenerIdJugador();
        if (idJugador >= 0) {
            Vector3 toque = new Vector3(posicionPantallaX, posicionPantallaY, 0);
            camara.unproject(toque);
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
