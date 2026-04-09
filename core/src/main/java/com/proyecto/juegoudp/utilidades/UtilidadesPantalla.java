package com.proyecto.juegoudp.utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Limpia el framebuffer completo y activa el {@link Viewport} del mundo lógico (barras al escalar / pantalla completa).
 */
public final class UtilidadesPantalla {
    private UtilidadesPantalla() {}

    public static void limpiarFondoCompletoYViewport(Viewport viewport, float r, float g, float b) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        Gdx.gl.glClearColor(r, g, b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply(false);
    }
}
