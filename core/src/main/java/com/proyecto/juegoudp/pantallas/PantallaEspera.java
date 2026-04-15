package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.pantallas.espera.ConexionSalaUdp;
import com.proyecto.juegoudp.pantallas.espera.EscuchaSala;
import com.proyecto.juegoudp.pantallas.ui.FabricaSkinBasico;
import com.proyecto.juegoudp.pantallas.ui.IFabricaSkin;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.utilidades.Constantes;
import com.proyecto.juegoudp.utilidades.UtilidadesPantalla;

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

    private final Texture fondo;

    public PantallaEspera(JuegoPrincipal juego, boolean esAnfitrion, String direccionIpServidor) {
        this.juego = juego;
        this.esAnfitrion = esAnfitrion;
        this.direccionIpServidor = direccionIpServidor == null ? "" : direccionIpServidor.trim();

        escenario = new Stage(new FitViewport(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO));
        Gdx.input.setInputProcessor(escenario);
        apariencia = fabricaSkin.crearSkin();

        // Cambia este nombre si tu archivo tiene otro nombre
        fondo = new Texture(Gdx.files.internal("images/sala_Espera.png"));

        etiquetaEstado = new Label("Iniciando...", apariencia);
        etiquetaEstado.setFontScale(1.2f);
        etiquetaEstado.setPosition(300, 380);
        escenario.addActor(etiquetaEstado);

        etiquetaAyuda = new Label("", apariencia);
        etiquetaAyuda.setFontScale(1.1f);
        etiquetaAyuda.setPosition(300, 320);
        escenario.addActor(etiquetaAyuda);

        TextButton botonCancelar = new TextButton("", apariencia);
        botonCancelar.setPosition(430, 90);
        botonCancelar.setSize(320, 80);
        hacerBotonInvisible(botonCancelar);
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
                juego.iniciarJuegoDesdeEspera(
                    PantallaEspera.this.esAnfitrion,
                    PantallaEspera.this.esAnfitrion ? "localhost" : PantallaEspera.this.direccionIpServidor,
                    conexionSala.obtenerServidor(),
                    conexionSala.obtenerCliente()
                );
            }

            @Override
            public void alRecibirMensaje(Mensaje mensaje) {
                // Reservado por ahora
            }
        });

        iniciarRed();
    }

    private void hacerBotonInvisible(TextButton boton) {
        boton.setColor(1, 1, 1, 0);
    }

    private void iniciarRed() {
        conexionSala.iniciar();

        if (esAnfitrion) {
            etiquetaAyuda.setText(
                "Otros deben unirse con la IP: " + conexionSala.obtenerDireccionIpLocal()
                    + " (puerto UDP " + Constantes.PUERTO_UDP + ")\n"
                    + "Objetivo: " + conexionSala.obtenerJugadoresRequeridos() + " jugadores conectados.\n"
                    + "Tiempo de partida: " + conexionSala.obtenerDuracionPartidaSegundos() + " s."
            );
        } else {
            etiquetaAyuda.setText("Conectando al host " + this.direccionIpServidor + " ...");
        }

        labelEstadoInicial();
    }

    private void labelEstadoInicial() {
        etiquetaEstado.setText(
            "Conectados: 0 / " + conexionSala.obtenerJugadoresRequeridos()
                + "\nEsperando asignación de id..."
        );
    }

    private void actualizarTextoEstado() {
        String lineaId = conexionSala.obtenerMiIdentificador() >= 0
            ? "Tu id: " + conexionSala.obtenerMiIdentificador()
            : "Esperando confirmación del servidor...";

        etiquetaEstado.setText(
            "Conectados: " + conexionSala.obtenerJugadoresConectados()
                + " / " + conexionSala.obtenerJugadoresRequeridos()
                + "\n" + lineaId
        );
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
        UtilidadesPantalla.limpiarFondoCompletoYViewport(escenario.getViewport(), 0f, 0f, 0f);

        escenario.getBatch().begin();
        escenario.getBatch().draw(
            fondo,
            0,
            0,
            Constantes.ANCHO_MUNDO,
            Constantes.ALTO_MUNDO
        );
        escenario.getBatch().end();

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
        fondo.dispose();
    }

    @Override
    public void show() {
        escenario.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
}
