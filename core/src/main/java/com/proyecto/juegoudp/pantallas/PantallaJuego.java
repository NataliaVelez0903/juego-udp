package com.proyecto.juegoudp.pantallas;

<<<<<<< Updated upstream
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
/**
 * Libreria para usar sonidos
 * */
import com.badlogic.gdx.audio.Sound;
import com.proyecto.juegoudp.logica.GestorPuntaje;
import com.proyecto.juegoudp.logica.SistemaArrastre;
import com.proyecto.juegoudp.logica.SistemaCaptura;
import com.proyecto.juegoudp.logica.SistemaColisiones;
import com.proyecto.juegoudp.logica.SonidoCaptura;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Ficha;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Zona;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.proyecto.juegoudp.logica.SistemaTiempo;


/**
 * Pantalla principal del juego.
 *
 * Responsabilidades:
 * - Renderizar todos los elementos visuales (fondo, fichas, zonas)
 * - Ejecutar los sistemas del juego en cada frame
 * - Coordinar el flujo general del juego
 *
 * Nota:
 * Esta clase NO contiene lógica del juego, solo coordina y dibuja.
 */
public class PantallaJuego extends ScreenAdapter {

    private OrthographicCamera camara;
    /**
     * Efecto de sonido para sunar puntaje
     * */
    private Sound sonidoPuntaje;
    private SpriteBatch batch;
    // para el fondo
    private Texture fondo;
    // texturas u iconos de las fichas
    private Texture texturaFichaAguardiente,texturaFichaCerbeza;
    /**
     * Texturas para las zonas
     * */
    private Texture zona1,zona2,zona3,zona4;
    private ShapeRenderer renderizador;
    private EstadoJuego estadoJuego;

    // Se instancia un objeto de nuestro sistema de arrastre
    private SistemaArrastre sistemaArrastre;


    //Se instancia un objeto para las colisiones
    private SistemaColisiones sistemaColisiones;
    private SistemaCaptura sistemaCaptura; //
    private GestorPuntaje gestorPuntaje;
=======
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.ServidorUDP;
import com.proyecto.juegoudp.red.TipoMensaje;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.modelo.EstadoJuego;

public class PantallaJuego implements Screen {
    private JuegoPrincipal juego;
    private OrthographicCamera camera;
    private ShapeRenderer shape;
    private SpriteBatch batch;
>>>>>>> Stashed changes
    private BitmapFont font;
    private ClienteUDP cliente;
    private ServidorUDP servidor;
    private EstadoJuego estadoLocal;
    private int miId = -1;
    private String miNombre;
    private boolean esHost;
    private float velocidad = 300f;
    private boolean up, down, left, right;
    private float[][] colores = {{1,0,0},{0,0,1},{0,1,0},{1,1,0},{1,0,1},{0,1,1}};

<<<<<<< Updated upstream
    //Instancia de objeto SistemaTiempo para el cotronometro del juego
    private SistemaTiempo sistemaTiempo;


    public PantallaJuego() {
        inicializar();
    }

    private void inicializar() {

        // Estado del juego
        estadoJuego = new EstadoJuego();
        /**
         * Se inicializa el sonido para el puntaje
         * */
        sonidoPuntaje = Gdx.audio.newSound(Gdx.files.internal("sonidos/sobeloPuntaje.wav"));
        SonidoCaptura efecto = new SonidoCaptura(sonidoPuntaje);
        //crear zona de juego
        // Esquinas
        estadoJuego.agregarZona(new Zona(1, 0, 0, 100, 100));
        estadoJuego.agregarZona(new Zona(2, 700, 0, 100, 100));
        estadoJuego.agregarZona(new Zona(3, 0, 500, 100, 100));
        estadoJuego.agregarZona(new Zona(4, 700, 500, 100, 100));
        /**
         * Imagenes para las 4 zonas
         * */
        zona1 = new Texture("images/pcristiano.png");
        zona2 = new Texture("images/pasprillaa.png");
        zona3 = new Texture("images/pdayroo.png");
        zona4 = new Texture("images/pnatalia.png");
        // iniciar gestor de puntaje

        gestorPuntaje = new GestorPuntaje(estadoJuego);
        sistemaCaptura = new SistemaCaptura(estadoJuego, gestorPuntaje,efecto);

        //Cronometro
        sistemaTiempo = new SistemaTiempo(60);

        // Fondo
        batch = new SpriteBatch();
        fondo = new Texture("images/fondo.jpg");

        // Textura ficha
        texturaFichaAguardiente = new Texture("images/aguardienteAmarillo.png");
        texturaFichaCerbeza = new Texture("images/cerbeza.png");
        // JUGADORES
        estadoJuego.agregarJugador(new Jugador(1, "jugador 1"));
        estadoJuego.agregarJugador(new Jugador(2, "jugador 2"));

        // Crear Fichas
        crearFichas();

        // Camara
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 800, 600);

        int miJugadorId = 1;
        sistemaArrastre = new SistemaArrastre(estadoJuego, miJugadorId, camara);
        sistemaColisiones = new SistemaColisiones(estadoJuego);
     //   sistemaCaptura = new SistemaCaptura(estadoJuego, gestorPuntaje,efecto); // 🔥 IMPORTANTE

        renderizador = new ShapeRenderer();

        font = new BitmapFont();
        font.setColor(1,1,1,1);
        font.getData().setScale(1.7f);

    }

    private void crearFichas() {
        estadoJuego.agregarFicha(new Ficha(1, 100f, 100f,1));
        estadoJuego.agregarFicha(new Ficha(2, 200f, 200f,2));
        estadoJuego.agregarFicha(new Ficha(3, 300f, 150f,3));
=======
    // Variables para arrastrar pelotas
    private Pelota pelotaArrastrada = null;
    private float offsetX, offsetY;

    public PantallaJuego(JuegoPrincipal juego, boolean esHost, String ipServidor, String nombre, int avatarId) {
        this.juego = juego;
        this.esHost = esHost;
        this.miNombre = nombre;
        this.estadoLocal = new EstadoJuego();
        this.shape = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.camera = new OrthographicCamera(1024, 768);
        camera.setToOrtho(false);

        try {
            if (esHost) {
                System.out.println("[PantallaJuego] Iniciando servidor (host)...");
                servidor = new ServidorUDP();
                servidor.start();
                Thread.sleep(1000);
                cliente = new ClienteUDP("localhost");
            } else {
                System.out.println("[PantallaJuego] Conectando a servidor remoto: " + ipServidor);
                cliente = new ClienteUDP(ipServidor);
            }

            cliente.setCallbackEstado(estadoSerializado -> {
                Gdx.app.postRunnable(() -> actualizarEstado(estadoSerializado));
            });

            Mensaje join = new Mensaje(TipoMensaje.UNIRSE, 0, 0, 0, 0, 0, 0, miNombre);
            cliente.enviarMensaje(join);
            System.out.println("[PantallaJuego] Enviado UNIRSE con nombre: " + miNombre);

            // Configurar controles de teclado (WASD)
            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean keyDown(int k) {
                    if (k==Keys.W) up=true;
                    if (k==Keys.S) down=true;
                    if (k==Keys.A) left=true;
                    if (k==Keys.D) right=true;
                    return true;
                }
                @Override
                public boolean keyUp(int k) {
                    if (k==Keys.W) up=false;
                    if (k==Keys.S) down=false;
                    if (k==Keys.A) left=false;
                    if (k==Keys.D) right=false;
                    return true;
                }

                // Controles del mouse para arrastrar pelotas
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    Vector3 touch = new Vector3(screenX, screenY, 0);
                    camera.unproject(touch);
                    // Buscar pelota tocada (que esté libre)
                    for (Pelota p : estadoLocal.getPelotas().values()) {
                        if (p.getIdJugador() == -1 && Math.hypot(touch.x - p.getX(), touch.y - p.getY()) < 20) {
                            pelotaArrastrada = p;
                            offsetX = p.getX() - touch.x;
                            offsetY = p.getY() - touch.y;
                            // Enviar mensaje de tomar pelota al servidor
                            Mensaje tomar = new Mensaje(TipoMensaje.TOMAR_PELOTA, miId, p.getId(), 0, 0, 0, 0, "");
                            cliente.enviarMensaje(tomar);
                            break;
                        }
                    }
                    return true;
                }

                @Override
                public boolean touchDragged(int screenX, int screenY, int pointer) {
                    if (pelotaArrastrada != null) {
                        Vector3 touch = new Vector3(screenX, screenY, 0);
                        camera.unproject(touch);
                        float nuevaX = touch.x + offsetX;
                        float nuevaY = touch.y + offsetY;
                        // Limitar dentro de la pantalla
                        nuevaX = Math.max(20, Math.min(1004, nuevaX));
                        nuevaY = Math.max(20, Math.min(748, nuevaY));
                        // Enviar movimiento de pelota al servidor
                        Mensaje mover = new Mensaje(TipoMensaje.MOVER_PELOTA, miId, pelotaArrastrada.getId(), nuevaX, nuevaY, 0, 0, "");
                        cliente.enviarMensaje(mover);
                        // Actualizar localmente para respuesta inmediata
                        pelotaArrastrada.setX(nuevaX);
                        pelotaArrastrada.setY(nuevaY);
                    }
                    return true;
                }

                @Override
                public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                    if (pelotaArrastrada != null) {
                        // Al soltar, enviamos velocidad nula (se puede mejorar para que tenga impulso)
                        Mensaje soltar = new Mensaje(TipoMensaje.SOLTAR_PELOTA, miId, pelotaArrastrada.getId(), 0, 0, 0, 0, "");
                        cliente.enviarMensaje(soltar);
                        pelotaArrastrada = null;
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            juego.volverAlMenu();
        }
    }

    private void actualizarEstado(String estado) {
        String[] partes = estado.split("\\|");
        if (partes.length < 3) return;
        // Jugadores
        estadoLocal.getJugadores().clear();
        String[] jugs = partes[1].split(";");
        for (String j : jugs) {
            if (j.isEmpty()) continue;
            String[] d = j.split(",");
            if (d.length >= 7) {
                int id = Integer.parseInt(d[0]);
                String nom = d[1];
                float x = Float.parseFloat(d[2]);
                float y = Float.parseFloat(d[3]);
                int pts = Integer.parseInt(d[4]);
                int av = Integer.parseInt(d[5]);
                boolean tiene = Integer.parseInt(d[6]) == 1;
                Jugador jug = new Jugador(id, nom, av);
                jug.setX(x); jug.setY(y);
                jug.setPuntaje(pts);
                jug.setTienePelota(tiene);
                estadoLocal.agregarJugador(jug);
                if (nom.equals(miNombre)) miId = id;
            }
        }
        // Pelotas
        estadoLocal.getPelotas().clear();
        String[] pels = partes[2].split(";");
        for (String p : pels) {
            if (p.isEmpty()) continue;
            String[] d = p.split(",");
            if (d.length >= 6) {
                int id = Integer.parseInt(d[0]);
                float x = Float.parseFloat(d[1]);
                float y = Float.parseFloat(d[2]);
                float vx = Float.parseFloat(d[3]);
                float vy = Float.parseFloat(d[4]);
                int jugId = Integer.parseInt(d[5]);
                Pelota pel = new Pelota(id, x, y);
                pel.setVx(vx); pel.setVy(vy);
                pel.setIdJugador(jugId);
                estadoLocal.agregarPelota(pel);
            }
        }
>>>>>>> Stashed changes
    }

    @Override
    public void render(float delta) {
        // Movimiento del jugador local (WASD)
        if (miId != -1) {
            float dx = 0, dy = 0;
            if (up) dy += velocidad * delta;
            if (down) dy -= velocidad * delta;
            if (right) dx += velocidad * delta;
            if (left) dx -= velocidad * delta;
            if (dx != 0 || dy != 0) {
                Jugador yo = estadoLocal.getJugador(miId);
                if (yo != null) {
                    float nx = yo.getX() + dx;
                    float ny = yo.getY() + dy;
                    nx = Math.max(20, Math.min(1004, nx));
                    ny = Math.max(20, Math.min(748, ny));
                    yo.setX(nx); yo.setY(ny);
                    Mensaje mover = new Mensaje(TipoMensaje.MOVER_JUGADOR, miId, 0, nx, ny, 0, 0, "");
                    cliente.enviarMensaje(mover);
                }
            }
        }

<<<<<<< Updated upstream
        limpiarPantalla();

        // Se encarga de las colisiones
        sistemaColisiones.actualizar();
        sistemaCaptura.actualizar();

        //Actualizar el cronometro
        sistemaTiempo.actualizar(delta);
=======
        // Dibujar fondo y elementos
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar zonas de gol (arcos) - solo para referencia visual
        shape.setColor(0.8f, 0.8f, 0.8f, 0.5f);
        for (int i = 0; i < 6; i++) {
            float x = (i < 3) ? 100 : 924;
            float y = (i % 3) * 120 + 200;
            shape.rect(x - 40, y - 60, 80, 120);
        }
>>>>>>> Stashed changes

        // Dibujar jugadores
        for (Jugador j : estadoLocal.getJugadores().values()) {
            float[] c = colores[j.getAvatarId() % colores.length];
            shape.setColor(c[0], c[1], c[2], 1);
            shape.circle(j.getX(), j.getY(), 20);
            if (j.isTienePelota()) {
                shape.setColor(1, 1, 1, 1);
                shape.circle(j.getX() + 15, j.getY() + 15, 8);
            }
        }
        // Dibujar pelotas
        shape.setColor(1, 0.8f, 0, 1);
        for (Pelota p : estadoLocal.getPelotas().values()) {
            shape.circle(p.getX(), p.getY(), 12);
        }
        shape.end();

<<<<<<< Updated upstream
        // =========================
        // Dibujar fondo
        // =========================
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        batch.draw(fondo, 0, 0, 800, 600);

        // =========================
        // Zonas
        // =========================
        // Dibujar zonas con imagen
        for (Zona z : estadoJuego.getZonas()) {

            Texture texturaZona = null;

            if (z.getJugadorId() == 1) texturaZona = zona1;
            if (z.getJugadorId() == 2) texturaZona = zona2;
            if (z.getJugadorId() == 3) texturaZona = zona3;
            if (z.getJugadorId() == 4) texturaZona = zona4;

            if (texturaZona != null) {
                batch.draw(
                    texturaZona,
                    z.getX(),
                    z.getY(),
                    z.getAncho(),
                    z.getAlto()
                );
            }
        }

        for (Ficha ficha : estadoJuego.getFichas()) {

            // No dibujar capturadas
            if (ficha.isCapturada()) continue;

            float size = 100; // tamaño de la ficha

            if (ficha.isArrastrando()) {
                batch.setColor(1, 0, 0, 1);
            } else {
                batch.setColor(1, 1, 1, 1);
            }

            Texture texturaActual;

            if (ficha.getValor() == 1) {
                texturaActual = texturaFichaAguardiente;
            } else if (ficha.getValor() == 2) {
                texturaActual = texturaFichaCerbeza;
            } else {
                texturaActual = texturaFichaAguardiente; // fallback
            }

            batch.draw(
                texturaActual,
                ficha.getX() - size / 2,
                ficha.getY() - size / 2,
                size + 40,
                size
            );
        }

        batch.setColor(1, 1, 1, 1);

        //Dibujar textos
        font.draw(batch, "jugador 1:" + obtenerPuntaje(1), 20, 580);
        font.draw(batch, "Tiempo: " + sistemaTiempo.getTiempoFormateado(), 320, 580);
        font.draw(batch, "jugador 2:" + obtenerPuntaje(2), 600, 580);

=======
        // Dibujar textos (puntajes)
        batch.begin();
        font.draw(batch, "PUNTAJES:", 20, 740);
        int y = 710;
        for (Jugador j : estadoLocal.getJugadores().values()) {
            font.draw(batch, j.getNombre() + ": " + j.getPuntaje(), 30, y);
            y -= 30;
        }
>>>>>>> Stashed changes
        batch.end();


        // Dibujar cajita del cronometro y zonas
        renderizador.setProjectionMatrix(camara.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        renderizador.begin(ShapeRenderer.ShapeType.Filled);

        //Cajita cronometro
        renderizador.setColor(0, 0, 0, 0.6f);
        renderizador.rect(305, 550, 190, 38);

        renderizador.end();
    }

    private int obtenerPuntaje (int judadorId){
        for (Jugador j : estadoJuego.getJugadores()){
            if (j.getId()== judadorId){
                return j.getPuntaje();
            }
        }
        return 0;
    }
    private void limpiarPantalla() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //borra la pantalla
    }

    @Override
    public void dispose() {
        /**
         * Liberar memoria
         * */
        renderizador.dispose();
        batch.dispose();
        fondo.dispose();
        texturaFichaAguardiente.dispose();
        texturaFichaCerbeza.dispose();
        zona1.dispose();
        zona2.dispose();
        zona3.dispose();
        zona4.dispose();
        sonidoPuntaje.dispose();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        shape.dispose();
        batch.dispose();
        font.dispose();
        if (cliente != null) cliente.cerrar();
        if (servidor != null) servidor.detener();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}