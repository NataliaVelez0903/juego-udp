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
import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.ServidorUDP;

public class PantallaEspera implements Screen {
    private JuegoPrincipal juego;
    private Stage stage;
    private Skin skin;
    private Label labelEstado;
    private boolean esHost;
    private String ipServidor;
    private ServidorUDP servidor;
    private ClienteUDP cliente;

    public PantallaEspera(JuegoPrincipal juego, boolean esHost, String ipServidor) {
        this.juego = juego;
        this.esHost = esHost;
        this.ipServidor = ipServidor;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = crearSkinBasico();
        crearUI();
        iniciarConexion();
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
        Label titulo = new Label("Esperando oponentes...", skin);
        titulo.setPosition(512 - titulo.getWidth()/2, 400);
        stage.addActor(titulo);
        labelEstado = new Label("", skin);
        labelEstado.setPosition(512 - 100, 300);
        stage.addActor(labelEstado);
        TextButton btnCancelar = new TextButton("Cancelar", skin);
        btnCancelar.setPosition(512 - 60, 200);
        btnCancelar.setSize(120,40);
        btnCancelar.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                cancelar();
            }
        });
        stage.addActor(btnCancelar);
    }

    private void iniciarConexion() {
        new Thread(() -> {
            try {
                if (esHost) {
                    Gdx.app.postRunnable(() -> labelEstado.setText("Iniciando servidor..."));
                    servidor = new ServidorUDP();
                    servidor.start();
                    String ipLocal = java.net.InetAddress.getLocalHost().getHostAddress();
                    Gdx.app.postRunnable(() -> labelEstado.setText("Servidor listo en IP: " + ipLocal + "\nEsperando jugadores..."));
                    Thread.sleep(3000);
                } else {
                    Gdx.app.postRunnable(() -> labelEstado.setText("Conectando a " + ipServidor + "..."));
                    cliente = new ClienteUDP(ipServidor);
                    Gdx.app.postRunnable(() -> labelEstado.setText("Conectado. Esperando inicio del host..."));
                    Thread.sleep(3000);
                }
                Gdx.app.postRunnable(() -> juego.iniciarJuego(esHost, ipServidor));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> labelEstado.setText("Error: " + e.getMessage()));
            }
        }).start();
    }

    private void cancelar() {
        if (servidor != null) servidor.detener();
        if (cliente != null) cliente.cerrar();
        Gdx.app.postRunnable(() -> juego.volverAlMenu());
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