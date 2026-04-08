package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.modelo.ConfiguracionPartida;
import com.proyecto.juegoudp.utilidades.Constantes;

public class PantallaMenu implements Screen {
    private JuegoPrincipal juego;
    private Stage stage;
    private Skin skin;
    private TextField campoNombre, campoIp, campoJugadores, campoTiempo;
    private Label labelError;
    private boolean modoHost = true;

    public PantallaMenu(JuegoPrincipal juego) {
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
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
        skinBasico.add("default", textFieldStyle);
        return skinBasico;
    }

    private void crearUI() {
        Label titulo = new Label("PELOTEROS - MULTIJUGADOR", skin);
        titulo.setPosition(512 - titulo.getWidth()/2, 650);
        stage.addActor(titulo);

        Label lblNombre = new Label("Tu nombre:", skin);
        lblNombre.setPosition(300, 550);
        stage.addActor(lblNombre);
        campoNombre = new TextField("", skin);
        campoNombre.setPosition(420, 545);
        campoNombre.setSize(250, 30);
        stage.addActor(campoNombre);

        TextButton btnHost = new TextButton("CREAR PARTIDA (SER HOST)", skin);
        btnHost.setPosition(250, 480);
        btnHost.setSize(250, 50);
        btnHost.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                modoHost = true;
                actualizarVisibilidad();
            }
        });
        stage.addActor(btnHost);

        TextButton btnCliente = new TextButton("UNIRSE A PARTIDA", skin);
        btnCliente.setPosition(520, 480);
        btnCliente.setSize(250, 50);
        btnCliente.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                modoHost = false;
                actualizarVisibilidad();
            }
        });
        stage.addActor(btnCliente);

        Label lblJugadores = new Label("Número de jugadores (2-" + Constantes.MAX_JUGADORES + "):", skin);
        lblJugadores.setPosition(300, 410);
        stage.addActor(lblJugadores);
        campoJugadores = new TextField("2", skin);
        campoJugadores.setPosition(480, 405);
        campoJugadores.setSize(100,30);
        stage.addActor(campoJugadores);

        Label lblTiempo = new Label("Tiempo límite (s):", skin);
        lblTiempo.setPosition(300, 360);
        stage.addActor(lblTiempo);
        campoTiempo = new TextField("60", skin);
        campoTiempo.setPosition(480, 355);
        campoTiempo.setSize(100,30);
        stage.addActor(campoTiempo);

        Label lblIp = new Label("IP del Host:", skin);
        lblIp.setPosition(300, 410);
        stage.addActor(lblIp);
        campoIp = new TextField("localhost", skin);
        campoIp.setPosition(420, 405);
        campoIp.setSize(250,30);
        stage.addActor(campoIp);

        TextButton btnIniciar = new TextButton("INICIAR", skin);
        btnIniciar.setPosition(462, 280);
        btnIniciar.setSize(100,50);
        btnIniciar.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                iniciar();
            }
        });
        stage.addActor(btnIniciar);

        labelError = new Label("", skin);
        labelError.setColor(1,0,0,1);
        labelError.setPosition(512 - 100, 220);
        stage.addActor(labelError);

        actualizarVisibilidad();
    }

    private void actualizarVisibilidad() {
        boolean host = modoHost;
        for (var actor : stage.getActors()) {
            if (actor instanceof Label) {
                String txt = ((Label)actor).getText().toString();
                if (txt.startsWith("Número de jugadores (2-") || txt.equals("Tiempo límite (s):"))
                    actor.setVisible(host);
                if (txt.equals("IP del Host:"))
                    actor.setVisible(!host);
            }
        }
        campoJugadores.setVisible(host);
        campoTiempo.setVisible(host);
        campoIp.setVisible(!host);
    }

    private void iniciar() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            labelError.setText("Ingresa un nombre");
            return;
        }
        juego.setNombreJugador(nombre);

        if (modoHost) {
            try {
                int num = Integer.parseInt(campoJugadores.getText().trim());
                if (num < 2) num = 2;
                if (num > Constantes.MAX_JUGADORES) num = Constantes.MAX_JUGADORES;
                int tiempo = Integer.parseInt(campoTiempo.getText().trim());
                if (tiempo < 30) tiempo = 30;
                ConfiguracionPartida config = juego.getConfiguracion();
                config.setNumeroJugadores(num);
                config.setTiempoLimite(tiempo);
                config.setEsHost(true);
                juego.setConfiguracion(config);
                juego.setScreen(new PantallaAvatar(juego));
            } catch (NumberFormatException e) {
                labelError.setText("Número inválido");
            }
        } else {
            String ip = campoIp.getText().trim();
            if (ip.isEmpty()) {
                labelError.setText("Ingresa IP del Host");
                return;
            }
            juego.getConfiguracion().setEsHost(false);
            juego.getConfiguracion().setIpServidor(ip);
            juego.setScreen(new PantallaAvatar(juego));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f,0.2f,0.3f,1);
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