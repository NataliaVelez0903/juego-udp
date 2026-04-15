package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.pantallas.ui.FabricaSkinBasico;
import com.proyecto.juegoudp.pantallas.ui.IFabricaSkin;
import com.proyecto.juegoudp.utilidades.Constantes;

public class PantallaInformacion implements Screen {
    private final JuegoPrincipal juego;
    private final Stage stage;
    private final Skin skin;
    private final IFabricaSkin fabricaSkin = new FabricaSkinBasico();

    private final SpriteBatch batch;
    private final Texture fondo;
    private TextButton botonVolver;

    public PantallaInformacion(JuegoPrincipal juego) {
        this.juego = juego;
        this.stage = new Stage(new FitViewport(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO));
        this.batch = new SpriteBatch();
        this.fondo = new Texture("images/fondo_informacion.png");

        Gdx.input.setInputProcessor(stage);
        this.skin = fabricaSkin.crearSkin();

        crearUi();
    }

    private void crearUi() {
        botonVolver = new TextButton("", skin);
        botonVolver.setSize(260, 55);
        botonVolver.setPosition(
                (Constantes.ANCHO_MUNDO - botonVolver.getWidth()) / 2f,
                55
        );
        botonVolver.getLabel().setAlignment(Align.center);

        botonVolver.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                juego.volverAlMenu();
            }
        });

        stage.addActor(botonVolver);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        batch.begin();
        batch.draw(fondo, 0, 0, Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        fondo.dispose();
    }
}