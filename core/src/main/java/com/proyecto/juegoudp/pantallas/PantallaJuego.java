package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.proyecto.juegoudp.red.ClienteUdp;
import com.proyecto.juegoudp.sonido.GestorSonidos;

/**
 * Orquesta el ciclo de vida de la pantalla de partida; delega red, estado, entrada, dibujo y movimiento.
 */
public class PantallaJuego implements Screen {
    private final JuegoPrincipal juego;
    private final GestorSonidos gestorSonidos;
    private final OrthographicCamera camara;
    private final ShapeRenderer dibujadorFormas;
    private final SpriteBatch loteSprites;
    private final BitmapFont fuente;

    private final EstadoJuego estadoLocal;
    private final GestorEstadoRedPartida gestorEstado;
    private final DatosArrastrePelota datosArrastre = new DatosArrastrePelota();
    private final boolean[] estadoTeclasMovimiento = new boolean[4];
    private final float velocidadMovimiento = 300f;

    private ClienteUdp cliente;
    private ServidorUdp servidor;
    private IRenderizadorPartida renderizador;
    private ControladorEntradaJuego controladorEntrada;
    private MovimientoJugadorLocal movimientoLocal;

    private boolean partidaFinalizada;

    public PantallaJuego(JuegoPrincipal juego, boolean esAnfitrion, String direccionIpServidor, String nombre, int idAvatar) {
        this(juego, esAnfitrion, direccionIpServidor, nombre, idAvatar, null, null);
    }

    public PantallaJuego(
            JuegoPrincipal juego,
            boolean esAnfitrion,
            String direccionIpServidor,
            String nombre,
            int idAvatar,
            ServidorUdp servidorExistente,
            ClienteUdp clienteExistente
    ) {
        this.juego = juego;
        this.estadoLocal = new EstadoJuego();
        this.gestorEstado = new GestorEstadoRedPartida(estadoLocal, GestorSonidos.getInstancia(), nombre);

        this.dibujadorFormas = new ShapeRenderer();
        this.loteSprites = new SpriteBatch();
        this.fuente = new BitmapFont();
        this.camara = new OrthographicCamera(1024, 768);
        camara.setToOrtho(false);

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

            controladorEntrada = new ControladorEntradaJuego(camara, estadoLocal, cliente,
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

    @Override
    public void render(float deltaSegundos) {
        if (!partidaFinalizada && gestorEstado.obtenerTiempoRestanteSegundos() == 0) {
            partidaFinalizada = true;
            NavegacionFinPartida.irAPantallaFinal(juego, estadoLocal, (int) juego.getConfiguracion().getTiempoLimite());
            return;
        }

        movimientoLocal.actualizar(deltaSegundos, gestorEstado.obtenerIdJugador(), partidaFinalizada);
        if (renderizador != null) {
            renderizador.render(deltaSegundos);
        }
    }

    @Override
    public void resize(int ancho, int alto) {
        if (renderizador != null) {
            renderizador.resize(ancho, alto);
        }
    }

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

    @Override
    public void show() {
        if (gestorSonidos != null) {
            gestorSonidos.iniciarMusicaFondo();
        }
    }

    @Override
    public void pause() {
        if (gestorSonidos != null) {
            gestorSonidos.pausarMusicaFondo();
        }
    }

    @Override
    public void resume() {
        if (gestorSonidos != null) {
            gestorSonidos.reanudarMusicaFondo();
        }
    }

    @Override
    public void hide() {
        if (gestorSonidos != null) {
            gestorSonidos.detenerMusicaFondo();
        }
    }
}
