package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proyecto.juegoudp.JuegoPrincipal;

public class PantallaAvatar implements Screen {
    private JuegoPrincipal juego;
    private Stage stage;
    private Skin skin;
    private int avatarSeleccionado = 0;
    private String[] nombres = {"FUERZA 🔴", "VELOCIDAD 🔵", "ARQUERO 🟢", "TRAMPOSO 🟡", "MAGO 🟣", "TANQUE 🟠"};
    private String[] poderes = {"Golpea más fuerte", "Se mueve más rápido", "Puede atajar", "Roba desde lejos", "Teletransporte", "No pierde la pelota"};

    public PantallaAvatar(JuegoPrincipal juego) {
        this.juego = juego;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = crearSkinBasico();
        crearUI();
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

    private void crearUI() {
        Label titulo = new Label("SELECCIONA TU AVATAR", skin);
        titulo.setPosition(512 - titulo.getWidth()/2, 650);
        stage.addActor(titulo);

        for (int i=0; i<nombres.length; i++) {
            final int idx = i;
            int fila = i/3, col = i%3;
            float x = 200 + col*220, y = 500 - fila*100;
            TextButton btn = new TextButton(nombres[i], skin);
            btn.setPosition(x,y);
            btn.setSize(180,60);
            btn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                    avatarSeleccionado = idx;
                }
            });
            stage.addActor(btn);
            Label poder = new Label(poderes[i], skin);
            poder.setPosition(x+10, y-25);
            poder.setFontScale(0.7f);
            stage.addActor(poder);
        }

        TextButton btnConfirmar = new TextButton("COMENZAR", skin);
        btnConfirmar.setPosition(412, 150);
        btnConfirmar.setSize(200,60);
        btnConfirmar.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                juego.setAvatarSeleccionado(avatarSeleccionado);
                juego.iniciarJuego(juego.getConfiguracion().isEsHost(),
                        juego.getConfiguracion().getIpServidor());
            }
        });
        stage.addActor(btnConfirmar);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f,0.1f,0.2f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int w, int h) { stage.getViewport().update(w,h,true); }
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}