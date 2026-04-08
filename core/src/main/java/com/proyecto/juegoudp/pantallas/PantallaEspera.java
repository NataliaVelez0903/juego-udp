package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.pantallas.espera.ConexionSalaUdp;
import com.proyecto.juegoudp.pantallas.espera.EscuchaSala;
import com.proyecto.juegoudp.pantallas.ui.FabricaSkinBasico;
import com.proyecto.juegoudp.pantallas.ui.IFabricaSkin;
import com.proyecto.juegoudp.utilidades.Constantes;

/**
 * Sala de espera hasta alcanzar el número de jugadores y comenzar la partida por UDP.
 */
public class PantallaEspera implements Screen {
    private final JuegoPrincipal juego;
    private final Stage escenario;
    private final Skin apariencia;
    private final IFabricaSkin fabricaSkin = new FabricaSkinBasico();
    private final Label etiquetaEstado;
    private final Label etiquetaAyuda;
    private final boolean esAnfitrion;
    private final String direccionIpServidor;
    private final ConexionSalaUdp conexionSala;

    public PantallaEspera(JuegoPrincipal juego, boolean esAnfitrion, String direccionIpServidor) {
        this.juego = juego;
        this.esAnfitrion = esAnfitrion;
        this.direccionIpServidor = direccionIpServidor == null ? "" : direccionIpServidor.trim();
        escenario = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(escenario);
        apariencia = fabricaSkin.crearSkin();

        Label titulo = new Label(esAnfitrion ? "Sala del host — esperando jugadores" : "Sala de espera", apariencia);
        titulo.setPosition(512 - titulo.getPrefWidth() / 2, 520);
        escenario.addActor(titulo);

        etiquetaEstado = new Label("Iniciando...", apariencia);
        etiquetaEstado.setPosition(80, 400);
        escenario.addActor(etiquetaEstado);

        etiquetaAyuda = new Label("", apariencia);
        etiquetaAyuda.setFontScale(0.85f);
        etiquetaAyuda.setPosition(80, 340);
        escenario.addActor(etiquetaAyuda);

        TextButton botonCancelar = new TextButton("Cancelar", apariencia);
        botonCancelar.setPosition(452, 180);
        botonCancelar.setSize(120, 40);
        botonCancelar.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                cancelar();
            }
        });
        escenario.addActor(botonCancelar);

        conexionSala = new ConexionSalaUdp(juego, esAnfitrion, this.direccionIpServidor, new EscuchaSala() {
            @Override
            public void alFallo(String mensaje) {
                etiquetaEstado.setText(mensaje);
            }

            @Override
            public void alAsignarIdJugador(int idJugador) {
                actualizarTextoEstado();
            }

            @Override
            public void alActualizarConectados(int conectados, int requeridos) {
                actualizarTextoEstado();
            }

            @Override
            public void alIniciarPartida() {
                juego.iniciarJuegoDesdeEspera(esAnfitrion, esAnfitrion ? "localhost" : PantallaEspera.this.direccionIpServidor,
                        conexionSala.obtenerServidor(), conexionSala.obtenerCliente());
            }

            @Override
            public void alRecibirMensaje(Mensaje mensaje) {
                // Reservado para registro o telemetría
            }
        });

        iniciarRed();
    }

    private void iniciarRed() {
        conexionSala.iniciar();
        if (esAnfitrion) {
            etiquetaAyuda.setText("Otros deben unirse con la IP: " + conexionSala.obtenerDireccionIpLocal()
                    + " (puerto UDP " + Constantes.PUERTO_UDP + ")\n"
                    + "Objetivo: " + conexionSala.obtenerJugadoresRequeridos() + " jugadores conectados.\n"
                    + "Tiempo de partida: " + conexionSala.obtenerDuracionPartidaSegundos() + " s.");
        } else {
            etiquetaAyuda.setText("Conectando al host " + this.direccionIpServidor + " ...");
        }
        labelEstadoInicial();
    }

    private void labelEstadoInicial() {
        etiquetaEstado.setText("Conectados: 0 / " + conexionSala.obtenerJugadoresRequeridos() + "\nEsperando asignación de id...");
    }

    private void actualizarTextoEstado() {
        String lineaId = conexionSala.obtenerMiIdentificador() >= 0
                ? "Tu id: " + conexionSala.obtenerMiIdentificador()
                : "Esperando confirmación del servidor...";
        etiquetaEstado.setText("Conectados: " + conexionSala.obtenerJugadoresConectados()
                + " / " + conexionSala.obtenerJugadoresRequeridos() + "\n" + lineaId);
    }

    private void cancelar() {
        conexionSala.detener();
        juego.volverAlMenu();
    }

    @Override
    public void hide() {
        if (!conexionSala.estaPasandoAPartida()) {
            conexionSala.detener();
        }
    }

    @Override
    public void render(float deltaSegundos) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        escenario.act(deltaSegundos);
        escenario.draw();
    }

    @Override
    public void resize(int ancho, int alto) {
        escenario.getViewport().update(ancho, alto, true);
    }

    @Override
    public void dispose() {
        escenario.dispose();
        apariencia.dispose();
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
