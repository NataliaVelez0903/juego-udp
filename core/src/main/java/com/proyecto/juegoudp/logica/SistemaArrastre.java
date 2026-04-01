package com.proyecto.juegoudp.logica;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Ficha;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
// import com.proyecto.juegoudp.red.ClienteUDP; // 🔒 DESACTIVADO

public class SistemaArrastre extends InputAdapter {
    private OrthographicCamera camara;
    private Vector3 touch = new Vector3();
    private EstadoJuego estadoJuego;

    // private ClienteUDP clienteUDP; // 🔒 DESACTIVADO

    private Ficha fichaSeleccionada;
    private float offsetX, offsetY;

    private int miJugadorId;

    public SistemaArrastre(EstadoJuego estadoJuego, int miJugadorId,OrthographicCamera camara) {
        this.estadoJuego = estadoJuego;
        this.miJugadorId = miJugadorId;
        this.camara = camara;
        Gdx.input.setInputProcessor(this);

        // 🔒 UDP DESACTIVADO
        // clienteUDP.escuchar(this::procesarMensaje);
    }

    // =========================
    // INPUT
    // =========================

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        seleccionarFicha(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        moverFicha(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        soltarFicha();
        return true;
    }

    // =========================
    // LOGICA DE ARRASTRE
    // =========================

    private void seleccionarFicha(int screenX, int screenY) {

        touch.set(screenX, screenY, 0);
        camara.unproject(touch); // 🔥 clave

        float mouseX = touch.x;
        float mouseY = touch.y;

        for (Ficha f : estadoJuego.getFichas()) {

            float dx = mouseX - f.getX();
            float dy = mouseY - f.getY();

            if (Math.sqrt(dx * dx + dy * dy) <= 20) {

                if (f.getJugadorId() == -1 || f.getJugadorId() == miJugadorId) {

                    fichaSeleccionada = f;
                    f.setArrastrando(true);
                    f.setJugadorId(miJugadorId);

                    offsetX = mouseX - f.getX();
                    offsetY = mouseY - f.getY();
                }
                break;
            }
        }
    }

    private void moverFicha(int screenX, int screenY) {

        if (fichaSeleccionada != null && fichaSeleccionada.isArrastrando()) {

            touch.set(screenX, screenY, 0);
            camara.unproject(touch);

            float mouseX = touch.x;
            float mouseY = touch.y;

            fichaSeleccionada.setX(mouseX - offsetX);
            fichaSeleccionada.setY(mouseY - offsetY);
        }
    }

    private void soltarFicha() {
        if (fichaSeleccionada != null) {

            fichaSeleccionada.setArrastrando(false);

            // 🔒 UDP DESACTIVADO
            /*
            clienteUDP.enviar(
                "RELEASE|" + fichaSeleccionada.getId() + "|" + miJugadorId
            );
            */

            fichaSeleccionada = null;
        }
    }

    // =========================
    // RED (DESACTIVADA)
    // =========================

    /*
    private void procesarMensaje(String msg) {
        String[] partes = msg.split("\\|");

        switch (partes[0]) {
            case "MOVE":
                procesarMovimiento(partes);
                break;

            case "RELEASE":
                procesarRelease(partes);
                break;
        }
    }

    private void procesarMovimiento(String[] partes) {
        int id = Integer.parseInt(partes[1]);
        float x = Float.parseFloat(partes[2]);
        float y = Float.parseFloat(partes[3]);
        int jugadorId = Integer.parseInt(partes[4]);

        for (Ficha f : estadoJuego.getFichas()) {
            if (f.getId() == id) {
                if (jugadorId != miJugadorId) {
                    f.setX(x);
                    f.setY(y);
                    f.setJugadorId(jugadorId);
                }
                break;
            }
        }
    }

    private void procesarRelease(String[] partes) {
        int id = Integer.parseInt(partes[1]);

        for (Ficha f : estadoJuego.getFichas()) {
            if (f.getId() == id) {
                f.setArrastrando(false);
                f.setJugadorId(-1);
                break;
            }
        }
    }
    */
}
