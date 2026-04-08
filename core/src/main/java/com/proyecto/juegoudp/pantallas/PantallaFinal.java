package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.modelo.Jugador;
import java.util.List;

/**
 * Pantalla de fin de partida: ganador, ranking por puntaje y tiempo total.
 */
public class PantallaFinal implements Screen {
    private final JuegoPrincipal juego;
    private final Stage stage;
    private final Skin skin;

    public PantallaFinal(JuegoPrincipal juego, String ganador, int puntajeGanador, List<Jugador> ranking, int tiempoTotalSegundos) {
        this.juego = juego;
        this.stage = new Stage(new ScreenViewport());
        this.skin = crearSkinBasico();
        Gdx.input.setInputProcessor(stage);

        Label titulo = new Label("FIN DE PARTIDA", skin);
        titulo.setPosition(512 - titulo.getPrefWidth() / 2, 650);
        stage.addActor(titulo);

        Label lblGanador = new Label("Ganador: " + ganador + " (" + puntajeGanador + " pts)", skin);
        lblGanador.setPosition(280, 590);
        stage.addActor(lblGanador);
        int min = Math.max(0, tiempoTotalSegundos) / 60;
        int seg = Math.max(0, tiempoTotalSegundos) % 60;
        Label lblTiempo = new Label(String.format("Tiempo total: %02d:%02d", min, seg), skin);
        lblTiempo.setPosition(280, 560);
        stage.addActor(lblTiempo);

        int y = 520;
        int pos = 1;
        for (Jugador j : ranking) {
            Label linea = new Label(pos + ". " + j.getNombre() + " - " + j.getPuntaje() + " pts", skin);
            linea.setPosition(320, y);
            stage.addActor(linea);
            y -= 35;
            pos++;
            if (pos > 6) break;
        }

        TextButton btnMenu = new TextButton("VOLVER AL MENU", skin);
        btnMenu.setPosition(420, 140);
        btnMenu.setSize(180, 50);
        btnMenu.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                juego.volverAlMenu();
            }
        });
        stage.addActor(btnMenu);
    }

    private Skin crearSkinBasico() {
        Skin skinBasico = new Skin();
        BitmapFont font = new BitmapFont();
        skinBasico.add("default", font);
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skinBasico.add("default", labelStyle);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        skinBasico.add("default", buttonStyle);
        return skinBasico;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
