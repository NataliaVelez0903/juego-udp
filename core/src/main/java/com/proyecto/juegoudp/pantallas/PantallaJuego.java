package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 *
 * Libreria para las texturas del juego
 * */
import com.badlogic.gdx.graphics.Texture;
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
import com.proyecto.juegoudp.sonido.GestorSonidos;
import com.proyecto.juegoudp.utilidades.Constantes;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class PantallaJuego implements Screen {
    private JuegoPrincipal juego;
    /**
     * Variable que almacena el fondo del juego
     * */
    private Texture fondo;
    /**
     * Variable que almacena la textura de
     * nuestra ficha (balon)
     * */
    private Texture pelotaImagen;

    /**
     * Variable que almacena la textura de
     * nuestra zona de puntaje
     * */
    private Texture zonaImagen;

    /**
     * Se instancia atributo para gestion de sonidos
     * */
    private GestorSonidos gestorSonidos;

    private int ultimoPuntajeIzquierda = 0; // Para detectar cambios
    private int ultimoPuntajeDerecha = 0;   // Para detectar cambios
    private OrthographicCamera camera;
    private ShapeRenderer shape;
    private SpriteBatch batch;
    private BitmapFont font;
    private ClienteUDP cliente;
    private ServidorUDP servidor;
    private EstadoJuego estadoLocal;
    private int miId = -1;
    private String miNombre;
    private boolean esHost;
    private float velocidad = 300f;
    /** Acumuladores para no saturar UDP con posición. */
    private float acumuladorEnvioJugador;
    private float acumuladorEnvioPelota;
    private long ultimoSeqEstado = -1;
    private boolean up, down, left, right;
    private float[][] colores = {{1,0,0},{0,0,1},{0,1,0},{1,1,0},{1,0,1},{0,1,1}};
    private Pelota pelotaArrastrada = null;
    private float offsetX, offsetY;

    public PantallaJuego(JuegoPrincipal juego, boolean esHost, String ipServidor, String nombre, int avatarId) {
        this(juego, esHost, ipServidor, nombre, avatarId, null, null);
    }

    /**
     * @param servidorExistente si no es null (host), se reutiliza el servidor de la sala de espera
     * @param clienteExistente si no es null, se reutiliza el cliente UDP de la sala de espera
     */
    public PantallaJuego(JuegoPrincipal juego, boolean esHost, String ipServidor, String nombre, int avatarId,
                         ServidorUDP servidorExistente, ClienteUDP clienteExistente) {
        this.juego = juego;
        this.esHost = esHost;
        this.miNombre = nombre;
        this.estadoLocal = new EstadoJuego();
        this.shape = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.camera = new OrthographicCamera(1024, 768);
        camera.setToOrtho(false);


        // Inicializar gestor de sonidos
        this.gestorSonidos = GestorSonidos.getInstancia();

        // Iniciar música de fondo
        this.gestorSonidos.iniciarMusicaFondo();

        /**
         * Se carga fondo de juego
         * */
        fondo = new Texture(Gdx.files.internal("images/fondo.jpg"));

        /**
         * Se carga imagen de nuestra ficha (balon)
         * */
        pelotaImagen = new Texture(Gdx.files.internal("images/ficha.png"));

        /**
         * Se carga imagen de nuestra zona de puntos
         * */
        zonaImagen = new Texture(Gdx.files.internal("images/zonapuntos.jpg"));



        try {
            if (clienteExistente != null) {
                cliente = clienteExistente;
                if (esHost && servidorExistente != null) {
                    servidor = servidorExistente;
                    System.out.println("[PantallaJuego] Reutilizando servidor y cliente desde sala de espera");
                }
            } else if (esHost) {
                System.out.println("[PantallaJuego] Iniciando servidor (host)...");
                servidor = new ServidorUDP();
                servidor.start();
                cliente = new ClienteUDP("localhost");
            } else {
                System.out.println("[PantallaJuego] Conectando a servidor remoto: " + ipServidor);
                cliente = new ClienteUDP(ipServidor);
            }

            cliente.setCallbackEstado(estadoSerializado -> {
                Gdx.app.postRunnable(() -> actualizarEstado(estadoSerializado));
            });
            cliente.setCallbackMensaje(msg -> {
                if (msg.getTipo() != TipoMensaje.TU_ID) return;
                Gdx.app.postRunnable(() -> {
                    if (msg.getIdJugador() < 0) {
                        System.out.println("[PantallaJuego] No se pudo unir: " + msg.getDatos());
                        juego.volverAlMenu();
                        return;
                    }
                    miId = msg.getIdJugador();
                    System.out.println("[PantallaJuego] Asignado miId=" + miId + " (UDP)");
                });
            });

            String datosUnirse = miNombre + "\t" + avatarId;
            Mensaje join = new Mensaje(TipoMensaje.UNIRSE, 0, 0, 0, 0, 0, 0, datosUnirse);
            cliente.enviarMensaje(join);
            System.out.println("[PantallaJuego] Enviado UNIRSE: " + miNombre + " (avatar " + avatarId + ")");

            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean keyDown(int k) {
                    if (k==Keys.M) {
                        if (gestorSonidos.isMusicaSonando()) {
                            gestorSonidos.setMusicaActivada(false);
                        } else {
                            gestorSonidos.setMusicaActivada(true);
                        }
                    }
                    if (miId < 0) return true;
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
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    if (miId < 0) return true;
                    Vector3 touch = new Vector3(screenX, screenY, 0);
                    camera.unproject(touch);
                    for (Pelota p : estadoLocal.getPelotas().values()) {
                        if (p.getIdJugador() == -1 && Math.hypot(touch.x - p.getX(), touch.y - p.getY()) < 20) {
                            pelotaArrastrada = p;
                            offsetX = p.getX() - touch.x;
                            offsetY = p.getY() - touch.y;
                            Mensaje tomar = new Mensaje(TipoMensaje.TOMAR_PELOTA, miId, p.getId(), 0, 0, 0, 0, "");
                            cliente.enviarMensaje(tomar);
                            break;
                        }
                    }
                    return true;
                }
                @Override
                public boolean touchDragged(int screenX, int screenY, int pointer) {
                    if (miId < 0 || pelotaArrastrada == null) return true;
                    Vector3 touch = new Vector3(screenX, screenY, 0);
                    camera.unproject(touch);
                    float nuevaX = touch.x + offsetX;
                    float nuevaY = touch.y + offsetY;
                    nuevaX = Math.max(20, Math.min(1004, nuevaX));
                    nuevaY = Math.max(20, Math.min(748, nuevaY));
                    pelotaArrastrada.setX(nuevaX);
                    pelotaArrastrada.setY(nuevaY);
                    float delta = Gdx.graphics.getDeltaTime();
                    acumuladorEnvioPelota += delta;
                    float intervalo = 1f / Constantes.ENVIOS_RED_POR_SEGUNDO;
                    if (acumuladorEnvioPelota >= intervalo) {
                        acumuladorEnvioPelota = 0;
                        Mensaje mover = new Mensaje(TipoMensaje.MOVER_PELOTA, miId, pelotaArrastrada.getId(), nuevaX, nuevaY, 0, 0, "");
                        cliente.enviarMensaje(mover);
                    }
                    return true;
                }
                @Override
                public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                    if (pelotaArrastrada != null) {
                        if (miId >= 0) {
                            Mensaje soltar = new Mensaje(TipoMensaje.SOLTAR_PELOTA, miId, pelotaArrastrada.getId(), 0, 0, 0, 0, "");
                            cliente.enviarMensaje(soltar);
                        }
                        pelotaArrastrada = null;
                        acumuladorEnvioPelota = 0;
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            juego.volverAlMenu();
        }
    }

    /**
     * Verifica si se ha marcado un punto y reproduce el sonido correspondiente
     * Principio de Abierto/Cerrado (OCP) - Extendemos funcionalidad sin modificar código existente
     */
    private void verificarYReproducirSonidoPunto() {
        // Calcular puntajes actuales (esto dependerá de tu lógica de juego)
        int puntajeIzquierda = 0;
        int puntajeDerecha = 0;

        // Aquí debes implementar la lógica para obtener los puntajes actuales
        // Asumiendo que tienes alguna forma de saber qué jugador está en qué equipo
        for (Jugador j : estadoLocal.getJugadores().values()) {
            // Ejemplo: si el avatarId es par es equipo izquierdo, impar equipo derecho
            if (j.getAvatarId() % 2 == 0) {
                puntajeIzquierda += j.getPuntaje();
            } else {
                puntajeDerecha += j.getPuntaje();
            }
        }

        // Verificar si hubo cambio en los puntajes
        if (puntajeIzquierda > ultimoPuntajeIzquierda || puntajeDerecha > ultimoPuntajeDerecha) {
            gestorSonidos.reproducirGol();
            System.out.println("[PantallaJuego] ¡GOL! Reproduciendo sonido");
        }

        // Actualizar últimos puntajes
        ultimoPuntajeIzquierda = puntajeIzquierda;
        ultimoPuntajeDerecha = puntajeDerecha;
    }

    private void actualizarEstado(String estado) {
        if (!estado.startsWith("STATE|")) return;
        String[] partes = estado.split("\\|", 6);
        if (partes.length < 3) return;

        int idxJugadores;
        int idxPelotas;
        if (partes.length >= 5) {
            try {
                long seq = Long.parseLong(partes[1]);
                if (seq <= ultimoSeqEstado) return;
                ultimoSeqEstado = seq;
            } catch (NumberFormatException e) {
                return;
            }
            idxJugadores = 3;
            idxPelotas = 4;
        } else if (partes.length == 4) {
            try {
                long seq = Long.parseLong(partes[1]);
                if (seq <= ultimoSeqEstado) return;
                ultimoSeqEstado = seq;
            } catch (NumberFormatException e) {
                return;
            }
            idxJugadores = 2;
            idxPelotas = 3;
        } else {
            idxJugadores = 1;
            idxPelotas = 2;
        }

        // Guardar puntajes antes de actualizar
        int puntajeTotalAnterior = 0;
        for (Jugador j : estadoLocal.getJugadores().values()) {
            puntajeTotalAnterior += j.getPuntaje();
        }

        estadoLocal.getJugadores().clear();
        String[] jugs = partes[idxJugadores].split(";");
        for (String j : jugs) {
            if (j.isEmpty()) continue;
            String[] d = j.split(",", 7);
            if (d.length >= 7) {
                int id = Integer.parseInt(d[0]);
                String nom;
                try {
                    nom = URLDecoder.decode(d[1], StandardCharsets.UTF_8);
                } catch (Exception e) {
                    nom = d[1];
                }
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

        // Calcular puntaje total después de actualizar
        int puntajeTotalNuevo = 0;
        for (Jugador j : estadoLocal.getJugadores().values()) {
            puntajeTotalNuevo += j.getPuntaje();
        }

        // Si aumentó el puntaje total, reproduce sonido de gol con volumen alto
        if (puntajeTotalNuevo > puntajeTotalAnterior) {
            // Reproducir con volumen 1.0f (máximo)
            gestorSonidos.reproducirGol(1.0f);
            System.out.println("[PantallaJuego] ¡Se marcó un punto! Reproduciendo sonido de gol a máximo volumen");
        }

        estadoLocal.getPelotas().clear();
        String[] pels = partes[idxPelotas].split(";");
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
    }



    @Override
    public void render(float delta) {

        // -------- MOVIMIENTO (envío limitado por UDP) --------
        if (miId >= 0) {
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

                    yo.setX(nx);
                    yo.setY(ny);

                    acumuladorEnvioJugador += delta;
                    float intervalo = 1f / Constantes.ENVIOS_RED_POR_SEGUNDO;
                    if (acumuladorEnvioJugador >= intervalo) {
                        acumuladorEnvioJugador = 0;
                        Mensaje mover = new Mensaje(
                            TipoMensaje.MOVER_JUGADOR,
                            miId, 0, nx, ny, 0, 0, ""
                        );
                        cliente.enviarMensaje(mover);
                    }
                }
            }
        }

        // -------- LIMPIAR --------
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        // -------- 1. FONDO --------
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(fondo, 0, 0, 1024, 768);
        batch.end();

        // -------- 2. SHAPES (SOLO JUGADORES) --------
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // Jugadores (CÍRCULOS)
        for (Jugador j : estadoLocal.getJugadores().values()) {
            float[] c = colores[j.getAvatarId() % colores.length];
            shape.setColor(c[0], c[1], c[2], 1);
            shape.circle(j.getX(), j.getY(), 20);

            // indicador si tiene pelota
            // if (j.isTienePelota()) {
            //    shape.setColor(1, 1, 1, 1);
            //    shape.circle(j.getX() + 15, j.getY() + 15, 8);
            // }
        }

        shape.end();

        // -------- 3. TEXTURAS (ZONAS Y PELOTA) --------
        batch.begin();

        /**
         * Se dibujan las zonas en el centro de la pantalla
         * */
        float yZona = 768 / 2f;

        /**
         * Se dibuja zona izquierda con imagen
         * */
        batch.draw(zonaImagen, 100 - 40, yZona - 40, 80, 80);

        /**
         * Se dibuja zona derecha con imagen
         * */
        batch.draw(zonaImagen, 924 - 40, yZona - 40, 80, 80);

        /**
         * Se dibuja la pelota con la imagen asignada
         * */
        for (Pelota p : estadoLocal.getPelotas().values()) {
            batch.draw(pelotaImagen,
                /**
                 * Medidas de nuestra pelota
                 * */
                p.getX() - 24,
                p.getY() - 24,
                48,
                48
            );
        }

        // -------- 4. UI --------
        if (miId < 0) {
            font.draw(batch, "Conectando al servidor (UDP)...", 20, 400);
        }
        // Mostrar estado de la música
        if (gestorSonidos != null && !gestorSonidos.isMusicaSonando()) {
            font.draw(batch, "MUSICA: OFF (Presiona M para activar)", 20, 50);
        } else {
            font.draw(batch, "MUSICA: ON (Presiona M para desactivar)", 20, 50);
        }

        font.draw(batch, "PUNTAJES:", 20, 740);

        int y = 710;
        for (Jugador j : estadoLocal.getJugadores().values()) {
            font.draw(batch, j.getNombre() + ": " + j.getPuntaje(), 30, y);
            y -= 30;
        }

        batch.end();
    }

    @Override public void resize(int w, int h) { camera.viewportWidth = w; camera.viewportHeight = h; camera.update(); }

    @Override public void dispose() {
        shape.dispose();
        batch.dispose();
        font.dispose();
        if(cliente!=null) cliente.cerrar();
        if(servidor!=null) servidor.detener();

        /**
         * Liberar sonidos
         * */
        if(gestorSonidos != null) gestorSonidos.dispose();
    }

    @Override public void show() {
        // Reiniciar música cuando se muestra la pantalla
        if (gestorSonidos != null) {
            gestorSonidos.iniciarMusicaFondo();
        }
    }

    @Override public void pause() {
        // Pausar música cuando el juego se pausa
        if (gestorSonidos != null) {
            gestorSonidos.pausarMusicaFondo();
        }
    }

    @Override public void resume() {
        // Reanudar música cuando el juego se reanuda
        if (gestorSonidos != null) {
            gestorSonidos.reanudarMusicaFondo();
        }
    }

    @Override public void hide() {
        // Detener música cuando se oculta la pantalla
        if (gestorSonidos != null) {
            gestorSonidos.detenerMusicaFondo();
        }
    }
}
