package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.pantallas.espera.ConexionSalaUdp;
import com.proyecto.juegoudp.pantallas.espera.EscuchaSala;
import com.proyecto.juegoudp.pantallas.ui.FabricaSkinBasico;
import com.proyecto.juegoudp.pantallas.ui.IFabricaSkin;
import com.proyecto.juegoudp.utilidades.Constantes;
import com.proyecto.juegoudp.utilidades.UtilidadesPantalla;

/**
 * Representa la pantalla de espera previa al inicio de una partida multijugador.
 *
 * Esta clase gestiona la interfaz visual de la sala de espera, mostrando
 * información sobre el estado de conexión, la cantidad de jugadores conectados
 * y las indicaciones necesarias para iniciar la partida.
 *
 * También coordina la creación y administración de la conexión UDP de la sala,
 * permitiendo tanto el rol de anfitrión como el de cliente.
 *
 * Cuando se alcanza la cantidad requerida de jugadores, esta pantalla
 * desencadena la transición hacia la partida.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class PantallaEspera implements Screen {

    /**
     * Referencia al juego principal.
     */
    private final JuegoPrincipal juego;

    /**
     * Escenario principal utilizado para dibujar y gestionar los elementos
     * de la interfaz.
     */
    private final Stage escenario;

    /**
     * Skin utilizada para dar estilo a los componentes de la interfaz.
     */
    private final Skin apariencia;

    /**
     * Fábrica encargada de crear la skin básica utilizada en esta pantalla.
     */
    private final IFabricaSkin fabricaSkin = new FabricaSkinBasico();

    /**
     * Etiqueta que muestra el estado actual de la conexión y de la sala.
     */
    private final Label etiquetaEstado;

    /**
     * Etiqueta que muestra instrucciones o información adicional al jugador.
     */
    private final Label etiquetaAyuda;

    /**
     * Indica si el jugador actual actúa como anfitrión de la partida.
     */
    private final boolean esAnfitrion;

    /**
     * Dirección IP del servidor al que se conectará el cliente.
     */
    private final String direccionIpServidor;

    /**
     * Componente encargado de gestionar la conexión UDP de la sala.
     */
    private final ConexionSalaUdp conexionSala;

    /**
     * Construye una nueva pantalla de espera.
     *
     * Este constructor inicializa la interfaz gráfica, configura los textos
     * y botones principales, crea la conexión de sala y define la lógica
     * de respuesta ante los eventos recibidos desde la red.
     *
     * @param juego referencia al juego principal
     * @param esAnfitrion indica si el jugador actual actuará como anfitrión
     * @param direccionIpServidor dirección IP del servidor al que se conectará
     *                            el cliente; si es null, se reemplaza por una
     *                            cadena vacía
     */
    public PantallaEspera(JuegoPrincipal juego, boolean esAnfitrion, String direccionIpServidor) {
        this.juego = juego;
        this.esAnfitrion = esAnfitrion;
        this.direccionIpServidor = direccionIpServidor == null ? "" : direccionIpServidor.trim();
        escenario = new Stage(new FitViewport(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO));
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
                juego.iniciarJuegoDesdeEspera(
                    PantallaEspera.this.esAnfitrion,
                    PantallaEspera.this.esAnfitrion ? "localhost" : PantallaEspera.this.direccionIpServidor,
                    conexionSala.obtenerServidor(),
                    conexionSala.obtenerCliente()
                );
            }

            @Override
            public void alRecibirMensaje(Mensaje mensaje) {
                // Reservado para registro o telemetría
            }
        });

        iniciarRed();
    }

    /**
     * Inicia la conexión de red de la sala de espera y actualiza
     * los textos informativos iniciales.
     *
     * Si el jugador es anfitrión, muestra la IP local, el puerto UDP,
     * la cantidad de jugadores objetivo y la duración configurada
     * para la partida. Si es cliente, muestra el intento de conexión
     * hacia el host indicado.
     */
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

    /**
     * Establece el texto inicial del estado de la sala.
     *
     * Muestra la cantidad inicial de jugadores conectados y el mensaje
     * de espera de asignación del identificador del jugador.
     */
    private void labelEstadoInicial() {
        etiquetaEstado.setText("Conectados: 0 / " + conexionSala.obtenerJugadoresRequeridos() + "\nEsperando asignación de id...");
    }

    /**
     * Actualiza el texto de estado de la sala con la información más reciente.
     *
     * Muestra la cantidad actual de jugadores conectados y, si ya fue asignado,
     * el identificador del jugador local.
     */
    private void actualizarTextoEstado() {
        String lineaId = conexionSala.obtenerMiIdentificador() >= 0
            ? "Tu id: " + conexionSala.obtenerMiIdentificador()
            : "Esperando confirmación del servidor...";
        etiquetaEstado.setText("Conectados: " + conexionSala.obtenerJugadoresConectados()
            + " / " + conexionSala.obtenerJugadoresRequeridos() + "\n" + lineaId);
    }

    /**
     * Cancela la espera de la partida y regresa al menú principal.
     *
     * Este método detiene la conexión de la sala y solicita al juego
     * principal la navegación de regreso al menú.
     */
    private void cancelar() {
        conexionSala.detener();
        juego.volverAlMenu();
    }

    /**
     * Se ejecuta cuando la pantalla deja de estar visible.
     *
     * Si la sala no está realizando la transición hacia la partida,
     * se detiene la conexión activa.
     */
    @Override
    public void hide() {
        if (!conexionSala.estaPasandoAPartida()) {
            conexionSala.detener();
        }
    }

    /**
     * Renderiza la pantalla de espera en cada fotograma.
     *
     * Este método limpia el fondo, actualiza el escenario
     * y dibuja todos los componentes visuales de la interfaz.
     *
     * @param deltaSegundos tiempo transcurrido desde el último fotograma
     */
    @Override
    public void render(float deltaSegundos) {
        UtilidadesPantalla.limpiarFondoCompletoYViewport(escenario.getViewport(), 0.1f, 0.1f, 0.2f);
        escenario.act(deltaSegundos);
        escenario.draw();
    }

    /**
     * Ajusta el viewport del escenario cuando cambia el tamaño de la ventana.
     *
     * @param ancho nuevo ancho de la ventana
     * @param alto nuevo alto de la ventana
     */
    @Override
    public void resize(int ancho, int alto) {
        escenario.getViewport().update(ancho, alto, true);
    }

    /**
     * Libera los recursos utilizados por la pantalla.
     *
     * Este método elimina el escenario y la skin utilizados
     * durante la ejecución de la pantalla.
     */
    @Override
    public void dispose() {
        escenario.dispose();
        apariencia.dispose();
    }

    /**
     * Se ejecuta cuando la pantalla pasa a estar visible.
     *
     * Actualiza el viewport del escenario con las dimensiones actuales
     * de la ventana.
     */
    @Override
    public void show() {
        escenario.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    /**
     * Se ejecuta cuando la aplicación entra en pausa.
     */
    @Override
    public void pause() {}

    /**
     * Se ejecuta cuando la aplicación se reanuda después de una pausa.
     */
    @Override
    public void resume() {}
}
