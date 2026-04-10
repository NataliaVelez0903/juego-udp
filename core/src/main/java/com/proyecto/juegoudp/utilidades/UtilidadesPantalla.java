package com.proyecto.juegoudp.utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Contiene utilidades relacionadas con la configuración y limpieza de la pantalla.
 *
 * Esta clase centraliza operaciones gráficas auxiliares necesarias para preparar
 * el área de renderizado, limpiar el fondo de la ventana y aplicar el viewport
 * correspondiente al mundo lógico del juego.
 *
 * Su propósito es mantener en un solo lugar tareas repetitivas asociadas al
 * manejo visual de la pantalla, especialmente cuando se trabaja con escalado
 * y adaptación a distintos tamaños de ventana.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public final class UtilidadesPantalla {

    /**
     * Evita la creación de instancias de esta clase utilitaria.
     */
    private UtilidadesPantalla() {}

    /**
     * Limpia completamente el fondo de la pantalla y aplica el viewport indicado.
     *
     * Este método ajusta primero el área de dibujo al tamaño real del back buffer,
     * luego establece el color de limpieza usando los componentes recibidos y
     * finalmente limpia el framebuffer antes de aplicar el viewport del mundo lógico.
     *
     * Este procedimiento resulta útil cuando se trabaja con escalado, barras laterales
     * o pantalla completa, ya que garantiza que toda la ventana quede correctamente
     * limpia antes del renderizado.
     *
     * @param viewport viewport que se aplicará después de limpiar la pantalla
     * @param r componente roja del color de fondo
     * @param g componente verde del color de fondo
     * @param b componente azul del color de fondo
     */
    public static void limpiarFondoCompletoYViewport(Viewport viewport, float r, float g, float b) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        Gdx.gl.glClearColor(r, g, b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply(false);
    }
}
