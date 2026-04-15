package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.List;

public class PantallaMenu implements Screen {

    private static final int VISTA_MENU = 0;
    private static final int VISTA_CREAR = 1;
    private static final int VISTA_UNIRSE = 2;

    private final JuegoPrincipal juego;
    private final Stage stage;
    private final Skin skin;
    private final IFabricaSkin fabricaSkin = new FabricaSkinBasico();
    private final ValidacionMenu validador = new ValidacionMenu();

    private final List<com.badlogic.gdx.scenes.scene2d.Actor> actoresMenu = new ArrayList<>();
    private final List<com.badlogic.gdx.scenes.scene2d.Actor> actoresCrear = new ArrayList<>();
    private final List<com.badlogic.gdx.scenes.scene2d.Actor> actoresUnirse = new ArrayList<>();

    private int vistaActual = VISTA_MENU;
    private int jugadoresSeleccionados = 2;

    private Texture fondoMenu;
    private Texture fondoCrear;
    private Texture fondoUnirse;
    private SpriteBatch batchFondo;

    // Campo nombre compartido
    private TextField campoNombre;

    // Campos crear
    private TextField campoTiempo;
    private TextField campoArbitros;
    private TextField campoHostIp;

    // Campos unirse
    private TextField campoIpServidor;

    // Mensajes
    private Label labelErrorCrear;
    private Label labelErrorUnirse;
    private Label labelBusquedaLan;

    public PantallaMenu(JuegoPrincipal juego) {
        this.juego = juego;
        this.stage = new Stage(new FitViewport(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO));
        this.skin = fabricaSkin.crearSkin();

        Gdx.input.setInputProcessor(stage);

        fondoMenu = new Texture(Gdx.files.internal("images/fondoMenuPrincipal.png"));
        fondoCrear = new Texture(Gdx.files.internal("images/fondoCrearPartida.png"));
        fondoUnirse = new Texture(Gdx.files.internal("images/fondoUnirsePartida.png"));
        batchFondo = new SpriteBatch();

        crearCampoNombreComun();
        crearVistaMenu();
        crearVistaCrear();
        crearVistaUnirse();
        actualizarVisibilidad();
    }

    private void crearCampoNombreComun() {
        campoNombre = new TextField("", skin);
        campoNombre.setPosition(415, 350);
        campoNombre.setSize(320, 44);
        configurarCampo(campoNombre);
        stage.addActor(campoNombre);
    }

    private void crearVistaMenu() {
        TextButton btnIrCrear = new TextButton("", skin);
        btnIrCrear.setPosition(82, 330);
        btnIrCrear.setSize(438, 92);
        hacerBotonInvisible(btnIrCrear);
        btnIrCrear.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                vistaActual = VISTA_CREAR;
                actualizarVisibilidad();
            }
        });
        agregarActorMenu(btnIrCrear);

        TextButton btnIrUnirse = new TextButton("", skin);
        btnIrUnirse.setPosition(565, 330);
        btnIrUnirse.setSize(458, 92);
        hacerBotonInvisible(btnIrUnirse);
        btnIrUnirse.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                vistaActual = VISTA_UNIRSE;
                actualizarVisibilidad();
            }
        });
        agregarActorMenu(btnIrUnirse);

        TextButton btnInfo = new TextButton("", skin);
        btnInfo.setPosition(362, 80);
        btnInfo.setSize(310, 50);
        hacerBotonInvisible(btnInfo);
        btnInfo.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                juego.setScreen(new PantallaInformacion(juego));
            }
        });
        agregarActorMenu(btnInfo);

        TextButton btnSalir = new TextButton("", skin);
        btnSalir.setPosition(425, 35);
        btnSalir.setSize(185, 45);
        hacerBotonInvisible(btnSalir);
        btnSalir.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        agregarActorMenu(btnSalir);
    }

    private void crearVistaCrear() {
        campoTiempo = new TextField("60", skin);
        campoTiempo.setPosition(410, 380);
        campoTiempo.setSize(165, 42);
        configurarCampo(campoTiempo);
        agregarActorCrear(campoTiempo);

        campoArbitros = new TextField("0", skin);
        campoArbitros.setPosition(410, 300);
        campoArbitros.setSize(165, 42);
        configurarCampo(campoArbitros);
        agregarActorCrear(campoArbitros);

        campoHostIp = new TextField("localhost", skin);
        campoHostIp.setPosition(330, 220);
        campoHostIp.setSize(245, 44);
        configurarCampo(campoHostIp);
        agregarActorCrear(campoHostIp);

        TextButton btn2Jugadores = new TextButton("", skin);
        btn2Jugadores.setPosition(560, 220);
        btn2Jugadores.setSize(235, 58);
        hacerBotonInvisible(btn2Jugadores);
        btn2Jugadores.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                jugadoresSeleccionados = 2;
            }
        });
        agregarActorCrear(btn2Jugadores);

        TextButton btn4Jugadores = new TextButton("", skin);
        btn4Jugadores.setPosition(830, 220);
        btn4Jugadores.setSize(235, 58);
        hacerBotonInvisible(btn4Jugadores);
        btn4Jugadores.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                jugadoresSeleccionados = 4;
            }
        });
        agregarActorCrear(btn4Jugadores);

        TextButton btnIniciar = new TextButton("", skin);
        btnIniciar.setPosition(500, 140);
        btnIniciar.setSize(250, 80);
        hacerBotonInvisible(btnIniciar);
        btnIniciar.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                iniciarComoHost();
            }
        });
        agregarActorCrear(btnIniciar);

        TextButton btnInfo = new TextButton("", skin);
        btnInfo.setPosition(495, 80);
        btnInfo.setSize(300, 45);
        hacerBotonInvisible(btnInfo);
        btnInfo.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                juego.setScreen(new PantallaInformacion(juego));
            }
        });
        agregarActorCrear(btnInfo);

        TextButton btnSalir = new TextButton("", skin);
        btnSalir.setPosition(560, 35);
        btnSalir.setSize(170, 40);
        hacerBotonInvisible(btnSalir);
        btnSalir.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        agregarActorCrear(btnSalir);

        TextButton btnVolver = new TextButton("", skin);
        btnVolver.setPosition(20, 20);
        btnVolver.setSize(120, 50);
        hacerBotonInvisible(btnVolver);
        btnVolver.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                vistaActual = VISTA_MENU;
                actualizarVisibilidad();
            }
        });
        agregarActorCrear(btnVolver);

        labelErrorCrear = new Label("", skin);
        labelErrorCrear.setColor(Color.RED);
        labelErrorCrear.setPosition(470, 140);
        agregarActorCrear(labelErrorCrear);
    }

    private void crearVistaUnirse() {
        campoIpServidor = new TextField("", skin);
        campoIpServidor.setPosition(300, 380);
        campoIpServidor.setSize(320, 44);
        configurarCampo(campoIpServidor);
        agregarActorUnirse(campoIpServidor);

        TextButton btnBuscarLan = new TextButton("", skin);
        btnBuscarLan.setPosition(80, 238);
        btnBuscarLan.setSize(1160, 130);
        hacerBotonInvisible(btnBuscarLan);
        btnBuscarLan.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                buscarPartidasLan();
            }
        });
        agregarActorUnirse(btnBuscarLan);

        TextButton btnIniciar = new TextButton("", skin);
        btnIniciar.setPosition(500, 140);
        btnIniciar.setSize(250, 80);
        hacerBotonInvisible(btnIniciar);
        btnIniciar.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                iniciarComoCliente();
            }
        });
        agregarActorUnirse(btnIniciar);

        TextButton btnInfo = new TextButton("", skin);
        btnInfo.setPosition(495, 80);
        btnInfo.setSize(300, 45);
        hacerBotonInvisible(btnInfo);
        btnInfo.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                juego.setScreen(new PantallaInformacion(juego));
            }
        });
        agregarActorUnirse(btnInfo);

        TextButton btnSalir = new TextButton("", skin);
        btnSalir.setPosition(560, 30);
        btnSalir.setSize(170, 40);
        hacerBotonInvisible(btnSalir);
        btnSalir.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        agregarActorUnirse(btnSalir);

        TextButton btnVolver = new TextButton("", skin);
        btnVolver.setPosition(20, 20);
        btnVolver.setSize(120, 50);
        hacerBotonInvisible(btnVolver);
        btnVolver.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                vistaActual = VISTA_MENU;
                actualizarVisibilidad();
            }
        });
        agregarActorUnirse(btnVolver);

        labelErrorUnirse = new Label("", skin);
        labelErrorUnirse.setColor(Color.RED);
        labelErrorUnirse.setPosition(430, 190);
        agregarActorUnirse(labelErrorUnirse);

        labelBusquedaLan = new Label("", skin);
        labelBusquedaLan.setColor(Color.WHITE);
        labelBusquedaLan.setPosition(360, 250);
        agregarActorUnirse(labelBusquedaLan);
    }

    private void iniciarComoHost() {
        String nombre = campoNombre.getText().trim();
        String tiempo = campoTiempo.getText().trim();
        String arbitros = campoArbitros.getText().trim();
        String ipHost = campoHostIp.getText().trim();

        if (nombre.isEmpty()) {
            labelErrorCrear.setText("Ingresa un nombre");
            labelErrorCrear.setPosition(300,490);
            return;
        }

        if (ipHost.isEmpty()) {
            ipHost = "localhost";
        }

        labelErrorCrear.setText("");

        juego.setNombreJugador(nombre);
        ConfiguracionPartida config = juego.getConfiguracion();
        config.setEsHost(true);
        config.setNumeroJugadores(jugadoresSeleccionados);
        config.setTiempoLimite(Integer.parseInt(tiempo));
        config.setNumeroArbitros(Integer.parseInt(arbitros));
        juego.setConfiguracion(config);
        juego.setAvatarSeleccionado(0);

        juego.setScreen(new PantallaEspera(juego, true, ipHost));
    }

    private void iniciarComoCliente() {
        String nombre = campoNombre.getText().trim();
        String ip = campoIpServidor.getText().trim();

        String error = validador.validarCliente(nombre, ip);
        if (!error.isEmpty()) {
            labelErrorUnirse.setText(error);
            return;
        }

        labelErrorUnirse.setText("");

        juego.setNombreJugador(nombre);
        juego.getConfiguracion().setEsHost(false);
        juego.getConfiguracion().setIpServidor(ip);
        juego.setAvatarSeleccionado(0);

        juego.setScreen(new PantallaEspera(juego, false, ip));
    }

    private void buscarPartidasLan() {
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
                labelBusquedaLan.setText("Host encontrado: " + primera.getIpHost());
            });
        }, "lan-search-menu").start();
    }

    private void actualizarVisibilidad() {
        actualizarLista(actoresMenu, vistaActual == VISTA_MENU);
        actualizarLista(actoresCrear, vistaActual == VISTA_CREAR);
        actualizarLista(actoresUnirse, vistaActual == VISTA_UNIRSE);

        // Mostrar siempre el mismo campo de nombre y moverlo según la vista
        campoNombre.setVisible(true);
        campoNombre.setTouchable(Touchable.enabled);

        if (vistaActual == VISTA_MENU) {
            campoNombre.setPosition(300, 460);
            campoNombre.setSize(320, 44);
        } else if (vistaActual == VISTA_CREAR) {
            campoNombre.setPosition(300, 460);
            campoNombre.setSize(320, 44);
        } else if (vistaActual == VISTA_UNIRSE) {
            campoNombre.setPosition(300, 460);
            campoNombre.setSize(320, 44);
        }

        campoNombre.setCursorPosition(campoNombre.getText().length());
    }

    private void actualizarLista(List<com.badlogic.gdx.scenes.scene2d.Actor> lista, boolean visible) {
        for (com.badlogic.gdx.scenes.scene2d.Actor actor : lista) {
            actor.setVisible(visible);
            actor.setTouchable(visible ? Touchable.enabled : Touchable.disabled);
        }
    }

    private void agregarActorMenu(com.badlogic.gdx.scenes.scene2d.Actor actor) {
        actoresMenu.add(actor);
        stage.addActor(actor);
    }

    private void agregarActorCrear(com.badlogic.gdx.scenes.scene2d.Actor actor) {
        actoresCrear.add(actor);
        stage.addActor(actor);
    }

    private void agregarActorUnirse(com.badlogic.gdx.scenes.scene2d.Actor actor) {
        actoresUnirse.add(actor);
        stage.addActor(actor);
    }

    private void configurarCampo(TextField campo) {
        TextField.TextFieldStyle estilo = new TextField.TextFieldStyle(campo.getStyle());
        estilo.background = null;
        estilo.focusedBackground = null;
        estilo.disabledBackground = null;
        estilo.fontColor = Color.BLACK;
        estilo.focusedFontColor = Color.BLACK;
        estilo.disabledFontColor = Color.BLACK;
        campo.setStyle(estilo);
        campo.setAlignment(Align.left);
    }

    private void hacerBotonInvisible(TextButton boton) {
        TextButton.TextButtonStyle estilo = new TextButton.TextButtonStyle(boton.getStyle());
        estilo.up = null;
        estilo.down = null;
        estilo.checked = null;
        estilo.over = null;
        boton.setStyle(estilo);
        boton.getLabel().setVisible(false);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batchFondo.setProjectionMatrix(stage.getViewport().getCamera().combined);
        batchFondo.begin();

        if (vistaActual == VISTA_MENU) {
            batchFondo.draw(fondoMenu, 0, 0, Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO);
        } else if (vistaActual == VISTA_CREAR) {
            batchFondo.draw(fondoCrear, 0, 0, Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO);
        } else {
            batchFondo.draw(fondoUnirse, 0, 0, Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO);
        }

        batchFondo.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        fondoMenu.dispose();
        fondoCrear.dispose();
        fondoUnirse.dispose();
        batchFondo.dispose();
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
