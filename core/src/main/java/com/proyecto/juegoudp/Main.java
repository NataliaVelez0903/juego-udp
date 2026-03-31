package com.proyecto.juegoudp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render() {
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
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
