package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.modelo.ConfiguracionPartida;
import com.proyecto.juegoudp.pantallas.menu.ValidacionMenu;
import com.proyecto.juegoudp.pantallas.ui.FabricaSkinBasico;
import com.proyecto.juegoudp.pantallas.ui.IFabricaSkin;
import com.proyecto.juegoudp.utilidades.Constantes;
import com.proyecto.juegoudp.utilidades.UtilidadesPantalla;

/**
 * Menú principal: nombre, modo anfitrión o cliente, IP, tamaño de sala y duración de partida.
 */
public class PantallaMenu implements Screen {
    private final JuegoPrincipal juego;
    private final Stage stage;
    private final Skin skin;
    private final IFabricaSkin fabricaSkin = new FabricaSkinBasico();
    private final ValidacionMenu validador = new ValidacionMenu();

    private TextField campoNombre, campoIp, campoTiempo;
    private Label labelError;
    private TextButton btnJugadores2, btnJugadores4;
    private boolean modoHost = true;
    private int jugadoresSeleccionados = 2;

    public PantallaMenu(JuegoPrincipal juego) {
        this.juego = juego;
        stage = new Stage(new FitViewport(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO));
        Gdx.input.setInputProcessor(stage);
        skin = fabricaSkin.crearSkin();
        crearUi();
    }

    private void crearUi() {
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

        Label lblJugadores = new Label("Número de jugadores (2 o 4):", skin);
        lblJugadores.setPosition(300, 410);
        stage.addActor(lblJugadores);
        btnJugadores2 = new TextButton("2 JUGADORES", skin);
        btnJugadores2.setPosition(500, 402);
        btnJugadores2.setSize(120, 34);
        btnJugadores2.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                jugadoresSeleccionados = 2;
                labelError.setText("");
            }
        });
        stage.addActor(btnJugadores2);

        btnJugadores4 = new TextButton("4 JUGADORES", skin);
        btnJugadores4.setPosition(630, 402);
        btnJugadores4.setSize(120, 34);
        btnJugadores4.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                jugadoresSeleccionados = 4;
                labelError.setText("");
            }
        });
        stage.addActor(btnJugadores4);

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
                if (txt.startsWith("Número de jugadores (2 o 4)") || txt.equals("Tiempo límite (s):"))
                    actor.setVisible(host);
                if (txt.equals("IP del Host:"))
                    actor.setVisible(!host);
            }
        }
        btnJugadores2.setVisible(host);
        btnJugadores4.setVisible(host);
        campoTiempo.setVisible(host);
        campoIp.setVisible(!host);
    }

    private void iniciar() {
        String nombre = campoNombre.getText().trim();
        if (modoHost) {
            ValidacionMenu.Resultado res = validador.validarHost(
                    nombre,
                    String.valueOf(jugadoresSeleccionados),
                    campoTiempo.getText());
            if (!res.ok) { labelError.setText(res.error); return; }

            juego.setNombreJugador(nombre);
            ConfiguracionPartida config = juego.getConfiguracion();
            config.setNumeroJugadores(res.jugadores);
            config.setTiempoLimite(res.tiempo);
            config.setEsHost(true);
            juego.setConfiguracion(config);
            juego.setAvatarSeleccionado(0);
            juego.setScreen(new PantallaEspera(juego, true, "localhost"));
        } else {
            String ip = campoIp.getText().trim();
            String error = validador.validarCliente(nombre, ip);
            if (!error.isEmpty()) { labelError.setText(error); return; }

            juego.setNombreJugador(nombre);
            juego.getConfiguracion().setEsHost(false);
            juego.getConfiguracion().setIpServidor(ip);
            juego.setAvatarSeleccionado(0);
            juego.setScreen(new PantallaEspera(juego, false, ip));
        }
    }

    @Override
    public void render(float delta) {
        UtilidadesPantalla.limpiarFondoCompletoYViewport(stage.getViewport(), 0.2f, 0.2f, 0.3f);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int w, int h) { stage.getViewport().update(w,h,true); }
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
    @Override public void show() {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}