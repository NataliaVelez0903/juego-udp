package com.proyecto.juegoudp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Gdx;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private ShapeRenderer shapeRenderer;
    private float[][] fichas;
    private int fichaSeleccionada = -1;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        shapeRenderer = new ShapeRenderer();
        fichas = new float[][]{{300, 240},{380, 300}, {460, 220}, {540, 280}, {420, 180}
        };

    }

    @Override
    public void render() {
        // detectar clic
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        //
        ScreenUtils.clear(0f, 0.2f, 0.1f, 1f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Zona jugador 1 (izquierda)
        shapeRenderer.setColor(0, 0, 1, 1);
        shapeRenderer.rect(0, 0, 200, 480);
        // Zona jugador 2 (derecha)
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(600, 0, 200, 480);
        // Zona central
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1);
        shapeRenderer.rect(200, 0, 400, 480);
        // Dibujar fichas en el centro
        shapeRenderer.setColor(1, 1, 0, 1); // amarillo
        for (float[] ficha : fichas) {
            shapeRenderer.circle(ficha[0], ficha[1], 20);
        }
        // detectar selección
        if (Gdx.input.justTouched()) {
            for (int i = 0; i < fichas.length; i++) {
                float dx = mouseX - fichas[i][0];
                float dy = mouseY - fichas[i][1];

                if (dx * dx + dy * dy <= 20 * 20) {
                    fichaSeleccionada = i;
                    break;
                }
            }
        }
        // arrastrar ficha
        if (Gdx.input.isTouched() && fichaSeleccionada != -1) {
            fichas[fichaSeleccionada][0] = mouseX;
            fichas[fichaSeleccionada][1] = mouseY;
        }
        //soltar ficha
        if (!Gdx.input.isTouched()) {
            fichaSeleccionada = -1;
        }
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
