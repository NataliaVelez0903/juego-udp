package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.proyecto.juegoudp.logica.GestorPuntaje;
import com.proyecto.juegoudp.logica.SistemaArrastre;
import com.proyecto.juegoudp.logica.SistemaCaptura;
import com.proyecto.juegoudp.logica.SistemaColisiones;

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

    private SpriteBatch batch;
    // para el fondo
    private Texture fondo;
    // para la ficha
    private Texture texturaFicha;
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
    private SistemaCaptura sistemaCaptura; // 🔥 NUEVO
    private GestorPuntaje gestorPuntaje;
    private BitmapFont font;

    //Instancia de objeto SistemaTiempo para el cotronometro del juego
    private SistemaTiempo sistemaTiempo;


    public PantallaJuego() {
        inicializar();
    }

    private void inicializar() {

        // Estado del juego
        estadoJuego = new EstadoJuego();


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
        sistemaCaptura = new SistemaCaptura(estadoJuego, gestorPuntaje);

        //Cronometro
        sistemaTiempo = new SistemaTiempo(60);

        // Fondo
        batch = new SpriteBatch();
        fondo = new Texture("images/fondo.jpg");

        // Textura ficha
        texturaFicha = new Texture("images/aguardienteAmarillo.png");

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
        sistemaCaptura = new SistemaCaptura(estadoJuego, gestorPuntaje); // 🔥 IMPORTANTE

        renderizador = new ShapeRenderer();

        font = new BitmapFont();
        font.setColor(1,1,1,1);
        font.getData().setScale(1.7f);

    }

    private void crearFichas() {
        estadoJuego.agregarFicha(new Ficha(1, 100f, 100f));
        estadoJuego.agregarFicha(new Ficha(2, 200f, 200f));
        estadoJuego.agregarFicha(new Ficha(3, 300f, 150f));
    }

    @Override
    public void render(float delta) {

        limpiarPantalla();

        // Se encarga de las colisiones
        sistemaColisiones.actualizar();
        sistemaCaptura.actualizar();

        //Actualizar el cronometro
        sistemaTiempo.actualizar(delta);

        camara.update();

        // =========================
        // Dibujar fondo
        // =========================
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        batch.draw(fondo, 0, 0, 800, 600);

        for (Ficha ficha : estadoJuego.getFichas()) {

            // No dibujar capturadas
            if (ficha.isCapturada()) continue;

            float size = 100; // tamaño de la ficha

            if (ficha.isArrastrando()) {
                batch.setColor(1, 0, 0, 1);
            } else {
                batch.setColor(1, 1, 1, 1);
            }

            batch.draw(
                texturaFicha,
                ficha.getX() - size / 2,
                ficha.getY() - size / 2,
                size + 40,
                size
            );


        }

        batch.setColor(1, 1, 1, 1);
        batch.end();


        // Dibujar cajita del cronometro y zonas
        renderizador.setProjectionMatrix(camara.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        renderizador.begin(ShapeRenderer.ShapeType.Filled);

        //Cajita cronometro
        renderizador.setColor(0, 0, 0, 0.6f);
        renderizador.rect(305, 550, 190, 38);

        //Zonas
        batch.begin();

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

        batch.end();
        renderizador.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);

        //Dibujar textos
        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        font.draw(batch, "jugador 1:" + obtenerPuntaje(1), 20, 580);
        font.draw(batch, "Tiempo: " + sistemaTiempo.getTiempoFormateado(), 320, 580);
        font.draw(batch, "jugador 2:" + obtenerPuntaje(2), 600, 580);


        batch.end();
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
        renderizador.dispose();
        batch.dispose();
        fondo.dispose();
        texturaFicha.dispose();
        zona1.dispose();
        zona2.dispose();
        zona3.dispose();
        zona4.dispose();
    }
}
