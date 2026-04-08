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
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.ParserEstadoUDP;
import com.proyecto.juegoudp.red.ServidorUDP;
import com.proyecto.juegoudp.red.TipoMensaje;
import com.proyecto.juegoudp.utilidades.Constantes;

public class PantallaEspera implements Screen {
    private final JuegoPrincipal juego;
    private final Stage stage;
    private final Skin skin;
    private final Label labelEstado;
    private final Label labelAyuda;
    private final boolean esHost;
    private final String ipServidor;
    private ServidorUDP servidor;
    private ClienteUDP cliente;
    private int miId = -1;
    private int conectados;
    private int requeridos = 2;
    private int tiempoPartidaSeg = 60;
    /** Evita cerrar red al pasar a partida reutilizando sockets. */
    private boolean pasandoAPartida;

    public PantallaEspera(JuegoPrincipal juego, boolean esHost, String ipServidor) {
        this.juego = juego;
        this.esHost = esHost;
        this.ipServidor = ipServidor == null ? "" : ipServidor.trim();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = crearSkinBasico();

        Label titulo = new Label(esHost ? "Sala del host — esperando jugadores" : "Sala de espera", skin);
        titulo.setPosition(512 - titulo.getPrefWidth() / 2, 520);
        stage.addActor(titulo);

        labelEstado = new Label("Iniciando...", skin);
        labelEstado.setPosition(80, 400);
        stage.addActor(labelEstado);

        labelAyuda = new Label("", skin);
        labelAyuda.setFontScale(0.85f);
        labelAyuda.setPosition(80, 340);
        stage.addActor(labelAyuda);

        TextButton btnCancelar = new TextButton("Cancelar", skin);
        btnCancelar.setPosition(452, 180);
        btnCancelar.setSize(120, 40);
        btnCancelar.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                cancelar();
            }
        });
        stage.addActor(btnCancelar);

        iniciarRed();
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

    private void iniciarRed() {
        try {
            requeridos = Math.max(2, Math.min(juego.getConfiguracion().getNumeroJugadores(), Constantes.MAX_JUGADORES));
            tiempoPartidaSeg = Math.max(30, (int) juego.getConfiguracion().getTiempoLimite());

            if (esHost) {
                servidor = new ServidorUDP(requeridos, tiempoPartidaSeg);
                servidor.start();
                cliente = new ClienteUDP("localhost");
                String ipLocal;
                try {
                    ipLocal = java.net.InetAddress.getLocalHost().getHostAddress();
                } catch (Exception e) {
                    ipLocal = "?";
                }
                labelAyuda.setText("Otros deben unirse con la IP: " + ipLocal + " (puerto UDP " + 5000 + ")\n"
                        + "Objetivo: " + requeridos + " jugadores conectados.\n"
                        + "Tiempo de partida: " + tiempoPartidaSeg + " s.");
            } else {
                if (this.ipServidor.isEmpty()) {
                    labelEstado.setText("Error: falta IP del host.");
                    return;
                }
                cliente = new ClienteUDP(this.ipServidor);
                labelAyuda.setText("Conectando al host " + this.ipServidor + " ...");
            }

            cliente.setCallbackEstado(estado -> Gdx.app.postRunnable(() -> onEstadoRecibido(estado)));
            cliente.setCallbackMensaje(msg -> Gdx.app.postRunnable(() -> onMensaje(msg)));

            String datosUnirse = juego.getNombreJugador() + "\t" + juego.getAvatarSeleccionado();
            cliente.enviarMensaje(new Mensaje(TipoMensaje.UNIRSE, 0, 0, 0, 0, 0, 0, datosUnirse));

            labelEstado.setText("Conectados: 0 / " + requeridos + "\nEsperando asignación de id...");
        } catch (Exception e) {
            labelEstado.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onMensaje(Mensaje msg) {
        if (msg.getTipo() != TipoMensaje.TU_ID) return;
        if (msg.getIdJugador() < 0) {
            labelEstado.setText("No se pudo unir: " + msg.getDatos());
            detenerRed();
            return;
        }
        miId = msg.getIdJugador();
        actualizarTextoEstado();
        intentarPasarAPartida();
    }

    private void onEstadoRecibido(String estado) {
        if (!estado.startsWith("STATE|")) return;
        conectados = ParserEstadoUDP.contarJugadores(ParserEstadoUDP.segmentoJugadores(estado));
        int req = ParserEstadoUDP.leerJugadoresRequeridos(estado);
        if (req > 0) requeridos = req;
        actualizarTextoEstado();
        intentarPasarAPartida();
    }

    private void actualizarTextoEstado() {
        String lineaId = miId >= 0 ? "Tu id: " + miId : "Esperando confirmación del servidor...";
        labelEstado.setText("Conectados: " + conectados + " / " + requeridos + "\n" + lineaId);
    }

    private void intentarPasarAPartida() {
        if (pasandoAPartida) return;
        if (miId < 0) return;
        if (conectados < requeridos) return;
        pasandoAPartida = true;
        juego.iniciarJuegoDesdeEspera(esHost, esHost ? "localhost" : ipServidor, servidor, cliente);
    }

    private void detenerRed() {
        if (pasandoAPartida) return;
        if (servidor != null) {
            servidor.detener();
            servidor = null;
        }
        if (cliente != null) {
            cliente.cerrar();
            cliente = null;
        }
    }

    private void cancelar() {
        detenerRed();
        juego.volverAlMenu();
    }

    @Override
    public void hide() {
        if (!pasandoAPartida) detenerRed();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
