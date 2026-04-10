package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.pantallas.juego.ControladorEntradaJuego;
import com.proyecto.juegoudp.pantallas.juego.DatosArrastrePelota;
import com.proyecto.juegoudp.pantallas.juego.DelegadoEntradaPartida;
import com.proyecto.juegoudp.pantallas.juego.GestorEstadoRedPartida;
import com.proyecto.juegoudp.pantallas.juego.IRenderizadorPartida;
import com.proyecto.juegoudp.pantallas.juego.InicializadorRedPartida;
import com.proyecto.juegoudp.pantallas.juego.MovimientoJugadorLocal;
import com.proyecto.juegoudp.pantallas.juego.NavegacionFinPartida;
import com.proyecto.juegoudp.pantallas.juego.ProveedorInterfazPartidaGestorEstado;
import com.proyecto.juegoudp.pantallas.juego.RenderizadorPartida;
import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.ServidorUDP;
import com.proyecto.juegoudp.sonido.GestorSonidos;
import com.proyecto.juegoudp.utilidades.Constantes;
import com.proyecto.juegoudp.utilidades.UtilidadesPantalla;

/**
 * Representa la pantalla principal de la partida en curso.
 *
 * Esta clase coordina el ciclo de vida de la pantalla de juego,
 * integrando los componentes encargados de la red, el estado local,
 * la entrada del usuario, el renderizado visual, el movimiento
 * del jugador y la navegación al finalizar la partida.
 *
 * Su propósito es actuar como punto central de orquestación
 * durante la ejecución de una partida multijugador.
 *
 * Implementa la interfaz {@link Screen} de libGDX para integrarse
 * con el sistema de pantallas del juego.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class PantallaJuego implements Screen {

    /**
     * Referencia al juego principal.
     */
    private final JuegoPrincipal juego;

    /**
     * Gestor de sonidos utilizado durante la partida.
     */
    private final GestorSonidos gestorSonidos;

    /**
     * Cámara ortográfica utilizada para visualizar el mundo del juego.
     */
    private final OrthographicCamera camara;

    /**
     * Viewport utilizado para adaptar la visualización del mundo
     * a la ventana del juego.
     */
    private final FitViewport viewport;

    /**
     * Renderizador de formas utilizado para el dibujo de elementos básicos.
     */
    private final ShapeRenderer dibujadorFormas;

    /**
     * Lote de sprites utilizado para el renderizado de texturas e imágenes.
     */
    private final SpriteBatch loteSprites;

    /**
     * Fuente utilizada para dibujar texto en pantalla.
     */
    private final BitmapFont fuente;

    /**
     * Estado local de la partida.
     */
    private final EstadoJuego estadoLocal;

    /**
     * Gestor encargado de aplicar y mantener el estado recibido desde la red.
     */
    private final GestorEstadoRedPartida gestorEstado;

    /**
     * Datos asociados al arrastre de la pelota con el ratón.
     */
    private final DatosArrastrePelota datosArrastre = new DatosArrastrePelota();

    /**
     * Estado de las teclas de movimiento del jugador.
     */
    private final boolean[] estadoTeclasMovimiento = new boolean[4];

    /**
     * Velocidad de movimiento local del jugador, expresada en píxeles por segundo.
     */
    private final float velocidadMovimiento = 300f;

    /**
     * Cliente UDP utilizado para la comunicación de red.
     */
    private ClienteUDP cliente;

    /**
     * Servidor UDP asociado a la partida cuando el jugador actúa como anfitrión.
     */
    private ServidorUDP servidor;

    /**
     * Componente encargado del renderizado visual de la partida.
     */
    private IRenderizadorPartida renderizador;

    /**
     * Controlador de entrada del jugador.
     */
    private ControladorEntradaJuego controladorEntrada;

    /**
     * Componente encargado del movimiento local del jugador.
     */
    private MovimientoJugadorLocal movimientoLocal;

    /**
     * Indica si la partida ya ha finalizado.
     */
    private boolean partidaFinalizada;

    /**
     * Construye una nueva pantalla de juego sin reutilizar conexiones previas.
     *
     * @param juego referencia al juego principal
     * @param esAnfitrion indica si el jugador actúa como anfitrión
     * @param direccionIpServidor dirección IP del servidor
     * @param nombre nombre del jugador local
     * @param idAvatar identificador del avatar seleccionado
     */
    public PantallaJuego(JuegoPrincipal juego, boolean esAnfitrion, String direccionIpServidor, String nombre, int idAvatar) {
        this(juego, esAnfitrion, direccionIpServidor, nombre, idAvatar, null, null);
    }

    /**
     * Construye una nueva pantalla de juego.
     *
     * Este constructor inicializa el estado local, los componentes
     * gráficos, el sistema de sonido, la conexión de red, el renderizador,
     * el controlador de entrada y el movimiento del jugador.
     *
     * También permite reutilizar instancias previas del servidor o cliente UDP
     * cuando la partida proviene de una sala de espera ya conectada.
     *
     * @param juego referencia al juego principal
     * @param esAnfitrion indica si el jugador actúa como anfitrión
     * @param direccionIpServidor dirección IP del servidor
     * @param nombre nombre del jugador local
     * @param idAvatar identificador del avatar seleccionado
     * @param servidorExistente servidor UDP existente, si se desea reutilizar
     * @param clienteExistente cliente UDP existente, si se desea reutilizar
     */
    public PantallaJuego(
        JuegoPrincipal juego,
        boolean esAnfitrion,
        String direccionIpServidor,
        String nombre,
        int idAvatar,
        ServidorUDP servidorExistente,
        ClienteUDP clienteExistente
    ) {
        this.juego = juego;
        this.estadoLocal = new EstadoJuego();
        this.gestorEstado = new GestorEstadoRedPartida(estadoLocal, GestorSonidos.getInstancia(), nombre);

        this.dibujadorFormas = new ShapeRenderer();
        this.loteSprites = new SpriteBatch();
        this.fuente = new BitmapFont();
        this.camara = new OrthographicCamera(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO);
        camara.setToOrtho(false);
        this.viewport = new FitViewport(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO, camara);
        viewport.apply(false);

        this.gestorSonidos = GestorSonidos.getInstancia();
        gestorSonidos.iniciarMusicaFondo();

        try {
            InicializadorRedPartida.ResultadoConexion conexion = InicializadorRedPartida.conectar(
                esAnfitrion,
                direccionIpServidor,
                nombre,
                idAvatar,
                servidorExistente,
                clienteExistente,
                gestorEstado::recibirEstadoSerializado,
                (idJugador, datosError) -> {
                    if (idJugador < 0) {
                        System.out.println("[PantallaJuego] No se pudo unir: " + datosError);
                        juego.volverAlMenu();
                        return;
                    }
                    gestorEstado.establecerIdJugador(idJugador);
                    System.out.println("[PantallaJuego] Asignado id jugador=" + idJugador + " (UDP)");
                }
            );
            this.cliente = conexion.cliente;
            this.servidor = conexion.servidor;

            renderizador = new RenderizadorPartida(
                camara, dibujadorFormas, loteSprites, fuente, estadoLocal, gestorSonidos,
                new ProveedorInterfazPartidaGestorEstado(gestorEstado));

            movimientoLocal = new MovimientoJugadorLocal(estadoLocal, cliente, estadoTeclasMovimiento, velocidadMovimiento);

            controladorEntrada = new ControladorEntradaJuego(viewport, estadoLocal, cliente,
                new DelegadoEntradaPartida(
                    gestorEstado,
                    datosArrastre,
                    estadoTeclasMovimiento,
                    () -> partidaFinalizada,
                    () -> gestorSonidos.alternarSilencio()));

            Gdx.input.setInputProcessor(controladorEntrada);
        } catch (Exception e) {
            e.printStackTrace();
            juego.volverAlMenu();
        }
    }

    /**
     * Actualiza y renderiza la partida en cada fotograma.
     *
     * Este método verifica si la partida ha finalizado por tiempo,
     * realiza la navegación a la pantalla final si corresponde,
     * actualiza el movimiento local del jugador y delega el renderizado
     * de la escena al componente correspondiente.
     *
     * @param deltaSegundos tiempo transcurrido desde el último fotograma
     */
    @Override
    public void render(float deltaSegundos) {
        if (!partidaFinalizada && gestorEstado.obtenerTiempoRestanteSegundos() == 0) {
            partidaFinalizada = true;
            NavegacionFinPartida.irAPantallaFinal(
                juego,
                estadoLocal,
                (int) juego.getConfiguracion().getTiempoLimite(),
                gestorEstado.obtenerJugadoresRequeridos());
            return;
        }

        movimientoLocal.actualizar(deltaSegundos, gestorEstado.obtenerIdJugador(), partidaFinalizada);
        UtilidadesPantalla.limpiarFondoCompletoYViewport(viewport, 0f, 0f, 0f);
        if (renderizador != null) {
            renderizador.render(deltaSegundos);
        }
    }

    /**
     * Ajusta el viewport cuando cambia el tamaño de la ventana.
     *
     * @param ancho nuevo ancho de la ventana
     * @param alto nuevo alto de la ventana
     */
    @Override
    public void resize(int ancho, int alto) {
        viewport.update(ancho, alto, true);
    }

    /**
     * Libera los recursos utilizados por la pantalla de juego.
     *
     * Este método dispone los recursos gráficos, cierra la conexión
     * de red activa, detiene el servidor si existe, libera el renderizador
     * y cierra el gestor de sonidos.
     */
    @Override
    public void dispose() {
        dibujadorFormas.dispose();
        loteSprites.dispose();
        fuente.dispose();
        if (cliente != null) {
            cliente.cerrar();
        }
        if (servidor != null) {
            servidor.detener();
        }
        if (renderizador != null) {
            renderizador.dispose();
        }
        if (gestorSonidos != null) {
            gestorSonidos.dispose();
        }
    }

    /**
     * Se ejecuta cuando la pantalla pasa a estar visible.
     *
     * Actualiza el viewport y reinicia la música de fondo si el sistema
     * de sonido está disponible.
     */
    @Override
    public void show() {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        if (gestorSonidos != null) {
            gestorSonidos.iniciarMusicaFondo();
        }
    }

    /**
     * Se ejecuta cuando la aplicación entra en pausa.
     *
     * Pausa la música de fondo si el sistema de sonido está disponible.
     */
    @Override
    public void pause() {
        if (gestorSonidos != null) {
            gestorSonidos.pausarMusicaFondo();
        }
    }

    /**
     * Se ejecuta cuando la aplicación se reanuda después de una pausa.
     *
     * Reanuda la música de fondo si el sistema de sonido está disponible.
     */
    @Override
    public void resume() {
        if (gestorSonidos != null) {
            gestorSonidos.reanudarMusicaFondo();
        }
    }

    /**
     * Se ejecuta cuando la pantalla deja de estar visible.
     *
     * Detiene la música de fondo si el sistema de sonido está disponible.
     */
    @Override
    public void hide() {
        if (gestorSonidos != null) {
            gestorSonidos.detenerMusicaFondo();
        }
    }
}
