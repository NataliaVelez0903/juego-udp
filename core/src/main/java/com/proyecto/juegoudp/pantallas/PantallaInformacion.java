package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.pantallas.ui.FabricaSkinBasico;
import com.proyecto.juegoudp.pantallas.ui.IFabricaSkin;
import com.proyecto.juegoudp.utilidades.Constantes;
import com.proyecto.juegoudp.utilidades.UtilidadesPantalla;

/**
 * Pantalla informativa con reglas y descripción del juego.
 */
public class PantallaInformacion implements Screen {
    private final JuegoPrincipal juego;
    private final Stage stage;
    private final Skin skin;
    private final IFabricaSkin fabricaSkin = new FabricaSkinBasico();

    public PantallaInformacion(JuegoPrincipal juego) {
        this.juego = juego;
        this.stage = new Stage(new FitViewport(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO));
        Gdx.input.setInputProcessor(stage);
        this.skin = fabricaSkin.crearSkin();
        crearUi();
    }

    private void crearUi() {
        Label titulo = new Label("INFORMACION DEL JUEGO", skin);
        titulo.setPosition(512 - titulo.getPrefWidth() / 2f, 670);
        stage.addActor(titulo);

        String texto = ""
                + "PELOTEROS es un juego multijugador en tiempo real.\n\n"
                + "OBJETIVO DEL JUEGO:\n"
                + "- Lleva la mayor cantidad de pelotas a tu zona de gol.\n"
                + "- Gana el jugador o equipo con mas puntos al final de la partida.\n\n"
                + "CONTROLES:\n"
                + "- WASD: mover jugador\n"
                + "- Mouse: tomar, arrastrar y soltar pelota\n"
                + "- M: activar o silenciar sonido\n\n"
                + "REGLAS DEL JUEGO:\n"
                + "- Cada jugador controla un solo personaje.\n"
                + "- Solo puedes mover una pelota a la vez.\n"
                + "- Para tomar una pelota debes hacer clic sobre ella.\n"
                + "- Si sueltas la pelota, cualquier jugador puede tomarla.\n"
                + "- Cada pelota que entre en tu zona de gol suma puntos.\n"
                + "- No puedes anotar en la zona de otro jugador.\n"
                + "- Los arbitros recorren la cancha constantemente.\n"
                + "- Si un arbitro te toca, perderas puntos.\n"
                + "- La partida termina cuando el tiempo se agota.\n"
                + "- Gana quien tenga mas puntos al final.\n\n"
                + "MODOS DE JUEGO:\n"
                + "- Host: crea la partida y configura jugadores, tiempo y arbitros.\n"
                + "- Cliente: se une a una partida por IP o por busqueda LAN.";

        Label contenido = new Label(texto, skin);
        contenido.setWrap(true);
        contenido.setWidth(800);
        contenido.setPosition(112, 160);
        stage.addActor(contenido);

        TextButton botonVolver = new TextButton("VOLVER AL MENU", skin);
        botonVolver.setSize(200, 45);
        botonVolver.setPosition(412, 70);
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
        UtilidadesPantalla.limpiarFondoCompletoYViewport(stage.getViewport(), 0.12f, 0.12f, 0.2f);
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
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}