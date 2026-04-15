package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.modelo.ConfiguracionPartida;
import com.proyecto.juegoudp.pantallas.menu.ValidacionMenu;
import com.proyecto.juegoudp.pantallas.ui.FabricaSkinBasico;
import com.proyecto.juegoudp.pantallas.ui.IFabricaSkin;
import com.proyecto.juegoudp.red.BuscadorPartidasLan;
import com.proyecto.juegoudp.red.InfoPartidaLan;
import com.proyecto.juegoudp.utilidades.Constantes;

import java.util.List;

public class PantallaMenu implements Screen {

    private final JuegoPrincipal juego;
    private final Stage stage;
    private final Skin skin;
    private final IFabricaSkin fabricaSkin = new FabricaSkinBasico();
    private final ValidacionMenu validador = new ValidacionMenu();

    private TextField campoNombre;
    private TextField campoHostIp;
    private TextField campoIpServidor;

    private Label labelError;
    private Label labelBusquedaLan;

    private boolean modoHost = true;
    private int jugadoresSeleccionados = 2;

    private Texture fondoMenu;
    private SpriteBatch batchFondo;

    public PantallaMenu(JuegoPrincipal juego) {
        this.juego = juego;
        this.stage = new Stage(new FitViewport(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO));
        Gdx.input.setInputProcessor(stage);
        this.skin = fabricaSkin.crearSkin();

        fondoMenu = new Texture(Gdx.files.internal("images/imagen_fondo_nuevo.png"));
        batchFondo = new SpriteBatch();

        crearUi();
        actualizarModoVisual();
    }

    private void crearUi() {
        // ===== CAMPO NOMBRE =====
        campoNombre = new TextField("", skin);
        campoNombre.setPosition(415, 480);
        campoNombre.setSize(470, 46);
        configurarCampoSobreImagen(campoNombre);
        stage.addActor(campoNombre);

        // ===== CAMPO HOST IP (izquierdo) =====
        campoHostIp = new TextField("localhost", skin);
        campoHostIp.setPosition(380, 215);
        campoHostIp.setSize(205, 42);
        configurarCampoSobreImagen(campoHostIp);
        campoHostIp.setDisabled(true);
        stage.addActor(campoHostIp);

        // ===== CAMPO IP SERVIDOR (derecho) =====
        campoIpServidor = new TextField("", skin);
        campoIpServidor.setPosition(760, 215);
        campoIpServidor.setSize(240, 42);
        configurarCampoSobreImagen(campoIpServidor);
        stage.addActor(campoIpServidor);

        // ===== BOTÓN HOST =====
        TextButton btnHost = new TextButton("", skin);
        btnHost.setPosition(80, 315);
        btnHost.setSize(438, 92);
        hacerBotonInvisible(btnHost);
        btnHost.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                modoHost = true;
                actualizarModoVisual();
            }
        });
        stage.addActor(btnHost);

        // ===== BOTÓN CLIENTE =====
        TextButton btnCliente = new TextButton("", skin);
        btnCliente.setPosition(565, 315);
        btnCliente.setSize(458, 92);
        hacerBotonInvisible(btnCliente);
        btnCliente.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                modoHost = false;
                actualizarModoVisual();
            }
        });
        stage.addActor(btnCliente);

        // ===== BOTÓN INICIAR =====
        TextButton btnIniciar = new TextButton("", skin);
        btnIniciar.setPosition(432, 156);
        btnIniciar.setSize(170, 62);
        hacerBotonInvisible(btnIniciar);
        btnIniciar.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                iniciar();
            }
        });
        stage.addActor(btnIniciar);

        // ===== BOTÓN REGLAS / INFO =====
        TextButton btnInfo = new TextButton("", skin);
        btnInfo.setPosition(362, 92);
        btnInfo.setSize(310, 50);
        hacerBotonInvisible(btnInfo);
        btnInfo.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                juego.setScreen(new PantallaInformacion(juego));
            }
        });
        stage.addActor(btnInfo);

        // ===== BOTÓN SALIR =====
        TextButton btnSalir = new TextButton("", skin);
        btnSalir.setPosition(425, 36);
        btnSalir.setSize(185, 45);
        hacerBotonInvisible(btnSalir);
        btnSalir.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        stage.addActor(btnSalir);

        // ===== BOTÓN BUSCAR LAN SOBRE CUADRO DERECHO =====
        TextButton btnBuscarLan = new TextButton("", skin);
        btnBuscarLan.setPosition(780, 260);
        btnBuscarLan.setSize(240, 42);
        hacerBotonInvisible(btnBuscarLan);
        btnBuscarLan.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (!modoHost) {
                    buscarPartidasLan();
                }
            }
        });
        stage.addActor(btnBuscarLan);

        // ===== MENSAJES =====
        labelError = new Label("", skin);
        labelError.setColor(Color.RED);
        labelError.setPosition(390, 225);
        stage.addActor(labelError);

        labelBusquedaLan = new Label("", skin);
        labelBusquedaLan.setColor(Color.WHITE);
        labelBusquedaLan.setPosition(700, 225);
        stage.addActor(labelBusquedaLan);
    }

    private void actualizarModoVisual() {
        labelError.setText("");
        labelBusquedaLan.setText("");

        if (modoHost) {
            campoHostIp.setText("localhost");
            campoHostIp.setVisible(true);

            campoIpServidor.setText("");
            campoIpServidor.setVisible(false);
        } else {
            campoHostIp.setVisible(false);
            campoIpServidor.setVisible(true);
            campoIpServidor.setCursorPosition(campoIpServidor.getText().length());
        }
    }

    private void configurarCampoSobreImagen(TextField campo) {
        TextField.TextFieldStyle estiloBase = skin.get(TextField.TextFieldStyle.class);
        TextField.TextFieldStyle estiloLimpio = new TextField.TextFieldStyle(estiloBase);

        estiloLimpio.background = null;
        estiloLimpio.focusedBackground = null;
        estiloLimpio.disabledBackground = null;
        estiloLimpio.fontColor = Color.BLACK;
        estiloLimpio.focusedFontColor = Color.BLACK;
        estiloLimpio.disabledFontColor = Color.BLACK;
        estiloLimpio.messageFontColor = Color.GRAY;

        campo.setStyle(estiloLimpio);
        campo.setAlignment(Align.left);
        campo.setCursorPosition(campo.getText().length());
    }

    private void hacerBotonInvisible(TextButton boton) {
        TextButton.TextButtonStyle estiloBase = skin.get(TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle estiloInvisible = new TextButton.TextButtonStyle(estiloBase);

        estiloInvisible.up = null;
        estiloInvisible.down = null;
        estiloInvisible.checked = null;
        estiloInvisible.over = null;

        boton.setStyle(estiloInvisible);
        boton.getLabel().setVisible(false);
    }

    private void buscarPartidasLan() {
        labelError.setText("");
        labelBusquedaLan.setText("Buscando...");

        new Thread(() -> {
            List<InfoPartidaLan> encontradas = BuscadorPartidasLan.buscar(1800);
            Gdx.app.postRunnable(() -> {
                if (encontradas.isEmpty()) {
                    labelBusquedaLan.setText("No se encontraron hosts LAN.");
                    return;
                }

                InfoPartidaLan primera = encontradas.get(0);
                campoIpServidor.setText(primera.getIpHost());
                labelBusquedaLan.setText("Host encontrado");
            });
        }, "lan-search-menu").start();
    }

    private void iniciar() {
        String nombre = campoNombre.getText().trim();

        if (modoHost) {
            if (nombre.isEmpty()) {

                labelError.setText("Ingresa un nombre");
                labelError.setPosition(415,480);
                return;
            }

            juego.setNombreJugador(nombre);
            ConfiguracionPartida config = juego.getConfiguracion();
            config.setNumeroJugadores(jugadoresSeleccionados);
            config.setTiempoLimite(60);
            config.setNumeroArbitros(0);
            config.setEsHost(true);
            juego.setConfiguracion(config);
            juego.setAvatarSeleccionado(0);

            juego.setScreen(new PantallaEspera(juego, true, "localhost"));
        } else {
            String ip = campoIpServidor.getText().trim();
            String error = validador.validarCliente(nombre, ip);

            if (!error.isEmpty()) {
                labelError.setText(error);
                return;
            }

            juego.setNombreJugador(nombre);
            juego.getConfiguracion().setEsHost(false);
            juego.getConfiguracion().setIpServidor(ip);
            juego.setAvatarSeleccionado(0);

            juego.setScreen(new PantallaEspera(juego, false, ip));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batchFondo.setProjectionMatrix(stage.getViewport().getCamera().combined);
        batchFondo.begin();
        batchFondo.draw(fondoMenu, 0, 0, Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO);
        batchFondo.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        fondoMenu.dispose();
        batchFondo.dispose();
    }

    @Override
    public void show() {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
