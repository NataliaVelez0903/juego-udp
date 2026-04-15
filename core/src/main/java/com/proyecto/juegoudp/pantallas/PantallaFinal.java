package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.utilidades.Constantes;

import java.util.List;

/**
 * Pantalla final de la partida.
 */
public class PantallaFinal implements Screen {

    private final JuegoPrincipal juego;
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private final Texture fondo;

    public PantallaFinal(JuegoPrincipal juego, String ganador, int puntajeGanador,
                         List<Jugador> ranking, int tiempoTotalSegundos) {
        this.juego = juego;
        this.stage = new Stage(new FitViewport(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO));
        this.skin = crearSkinBasico();
        this.batch = new SpriteBatch();
        this.fondo = new Texture("images/fondo_final.png");

        Gdx.input.setInputProcessor(stage);

        crearUi(ganador, puntajeGanador, ranking, tiempoTotalSegundos);
    }

    private void crearUi(String ganador, int puntajeGanador, List<Jugador> ranking, int tiempoTotalSegundos) {

        Label titulo = new Label("", skin);
        titulo.setPosition((Constantes.ANCHO_MUNDO - titulo.getPrefWidth()) / 2f, 650);
        stage.addActor(titulo);

        Label lblGanador = new Label("Ganador: " + ganador + " (" + puntajeGanador + " pts)", skin);
        lblGanador.setPosition(245, 500);
        stage.addActor(lblGanador);

        int min = Math.max(0, tiempoTotalSegundos) / 60;
        int seg = Math.max(0, tiempoTotalSegundos) % 60;

        Label lblTiempo = new Label(String.format("Tiempo total: %02d:%02d", min, seg), skin);
        lblTiempo.setPosition(245, 430);
        stage.addActor(lblTiempo);

        int y = 500;
        int posicion = 1;

        for (Jugador jugador : ranking) {
            if (posicion > 6) {
                break;
            }

            Label linea = new Label(posicion + ". " + jugador.getNombre() + " - " + jugador.getPuntaje() + " pts", skin);
            linea.setPosition(610, y);
            stage.addActor(linea);

            y -= 55;
            posicion++;
        }

        TextButton btnMenu = new TextButton("", skin);
        btnMenu.setSize(300, 55);
        btnMenu.setPosition((Constantes.ANCHO_MUNDO - btnMenu.getWidth()) / 2f, 95);
        btnMenu.getLabel().setAlignment(Align.center);

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
        font.getData().setScale(1.5f);

        skinBasico.add("default", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skinBasico.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        skinBasico.add("default", buttonStyle);

        return skinBasico;
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