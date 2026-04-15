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
import com.proyecto.juegoudp.red.BuscadorPartidasLan;
import com.proyecto.juegoudp.red.InfoPartidaLan;
import com.proyecto.juegoudp.utilidades.Constantes;
import com.proyecto.juegoudp.utilidades.UtilidadesPantalla;
import java.util.List;

/**
 * Representa el menú principal del juego.
 *
 * Esta pantalla permite al usuario configurar los parámetros iniciales
 * de la partida, como el nombre del jugador, el modo de juego (anfitrión
 * o cliente), la dirección IP del servidor, la cantidad de jugadores
 * y el tiempo límite de la partida.
 *
 * También se encarga de validar los datos ingresados y realizar
 * la navegación hacia la pantalla de espera correspondiente.
 *
 * Implementa la interfaz {@link Screen} para integrarse con el
 * sistema de pantallas de libGDX.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class PantallaMenu implements Screen {

    private final JuegoPrincipal juego;
    private final Stage stage;
    private final Skin skin;
    private final IFabricaSkin fabricaSkin = new FabricaSkinBasico();
    private final ValidacionMenu validador = new ValidacionMenu();

    /**
     * Campos de entrada del usuario.
     */
    private TextField campoNombre, campoIp, campoTiempo, campoArbitros;

    /**
     * Etiqueta para mostrar mensajes de error.
     */
    private Label labelError;
    private Label labelBusquedaLan;

    /**
     * Botones de selección de número de jugadores.
     */
    private TextButton btnJugadores2, btnJugadores4;
    private TextButton btnBuscarLan;

    /**
     * Indica si el usuario está en modo anfitrión.
     */
    private boolean modoHost = true;

    /**
     * Número de jugadores seleccionados.
     */
    private int jugadoresSeleccionados = 2;

    /**
     * Construye la pantalla del menú principal.
     *
     * Inicializa la interfaz gráfica, configura los componentes
     * y establece el controlador de entrada.
     *
     * @param juego referencia al juego principal
     */
    public PantallaMenu(JuegoPrincipal juego) {
        this.juego = juego;
        stage = new Stage(new FitViewport(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO));
        Gdx.input.setInputProcessor(stage);
        skin = fabricaSkin.crearSkin();
        crearUi();
    }

    /**
     * Crea y posiciona todos los elementos de la interfaz del menú.
     */
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

        Label lblArbitros = new Label("Árbitros:", skin);
        lblArbitros.setPosition(300, 320);
        stage.addActor(lblArbitros);

        campoArbitros = new TextField("0", skin);
        campoArbitros.setPosition(420, 315);
        campoArbitros.setSize(100, 30);
        stage.addActor(campoArbitros);

        Label lblIp = new Label("IP del Host:", skin);
        lblIp.setPosition(300, 410);
        stage.addActor(lblIp);

        campoIp = new TextField("localhost", skin);
        campoIp.setPosition(420, 405);
        campoIp.setSize(250,30);
        stage.addActor(campoIp);

        btnBuscarLan = new TextButton("BUSCAR PARTIDAS LAN", skin);
        btnBuscarLan.setPosition(300, 360);
        btnBuscarLan.setSize(280, 40);
        btnBuscarLan.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                buscarPartidasLan();
            }
        });
        stage.addActor(btnBuscarLan);

        labelBusquedaLan = new Label("", skin);
        labelBusquedaLan.setPosition(300, 330);
        stage.addActor(labelBusquedaLan);

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

        TextButton btnInfo = new TextButton("REGLAS / INFO", skin);
        btnInfo.setPosition(442, 220);
        btnInfo.setSize(140, 42);
        btnInfo.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                juego.setScreen(new PantallaInformacion(juego));
            }
        });
        stage.addActor(btnInfo);

        labelError = new Label("", skin);
        labelError.setColor(1,0,0,1);
        labelError.setPosition(512 - 170, 170);
        stage.addActor(labelError);

        actualizarVisibilidad();
    }

    /**
     * Actualiza la visibilidad de los componentes según el modo seleccionado.
     */
    private void actualizarVisibilidad() {
        boolean host = modoHost;
        for (var actor : stage.getActors()) {
            if (actor instanceof Label) {
                String txt = ((Label)actor).getText().toString();
                if (txt.startsWith("Número de jugadores (2 o 4)") || txt.equals("Tiempo límite (s):") || txt.equals("Árbitros:"))
                    actor.setVisible(host);
                if (txt.equals("IP del Host:"))
                    actor.setVisible(!host);
            }
        }
        btnJugadores2.setVisible(host);
        btnJugadores4.setVisible(host);
        campoTiempo.setVisible(host);
        campoArbitros.setVisible(host);
        campoIp.setVisible(!host);
        btnBuscarLan.setVisible(!host);
        labelBusquedaLan.setVisible(!host);
    }

    private void buscarPartidasLan() {
        labelError.setText("");
        labelBusquedaLan.setText("Buscando partidas en la red local...");
        btnBuscarLan.setDisabled(true);
        new Thread(() -> {
            List<InfoPartidaLan> encontradas = BuscadorPartidasLan.buscar(1800);
            Gdx.app.postRunnable(() -> {
                btnBuscarLan.setDisabled(false);
                if (encontradas.isEmpty()) {
                    labelBusquedaLan.setText("No se encontraron hosts LAN.");
                    return;
                }
                InfoPartidaLan primera = encontradas.get(0);
                campoIp.setText(primera.getIpHost());
                labelBusquedaLan.setText("Host encontrado: " + primera.getNombreHost()
                    + " | IP: " + primera.getIpHost()
                    + " | " + primera.getJugadoresRequeridos() + " jugadores");
            });
        }, "lan-search-menu").start();
    }

    /**
     * Inicia el flujo de creación o unión a una partida.
     *
     * Valida los datos ingresados y redirige a la pantalla de espera.
     */
    private void iniciar() {
        String nombre = campoNombre.getText().trim();

        if (modoHost) {
            ValidacionMenu.Resultado res = validador.validarHost(
                nombre,
                String.valueOf(jugadoresSeleccionados),
                campoTiempo.getText(),
                campoArbitros.getText());

            if (!res.ok) { labelError.setText(res.error); return; }

            juego.setNombreJugador(nombre);
            ConfiguracionPartida config = juego.getConfiguracion();
            config.setNumeroJugadores(res.jugadores);
            config.setTiempoLimite(res.tiempo);
            config.setNumeroArbitros(res.arbitros);
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
