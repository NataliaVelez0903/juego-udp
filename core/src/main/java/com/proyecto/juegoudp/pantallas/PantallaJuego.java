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
    private ShapeRenderer renderizador;
    private EstadoJuego estadoJuego;

    // Se instancia un objeto de nuestro sistema de arrastre
    private SistemaArrastre sistemaArrastre;


    //Se instancia un objeto para las colisiones
    private SistemaColisiones sistemaColisiones;
    private SistemaCaptura sistemaCaptura; // 🔥 NUEVO
    private GestorPuntaje gestorPuntaje;

    public PantallaJuego() {
        inicializar();
    }

    private void inicializar() {

        // Estado del juego
        estadoJuego = new EstadoJuego();

        //crear zona de juego
        estadoJuego.agregarZona(new Zona(1, 0, 200, 100, 200));
        estadoJuego.agregarZona(new Zona(2, 700, 200, 100, 200));

        // iniciar gestor de puntaje

        gestorPuntaje = new GestorPuntaje(estadoJuego);
        sistemaCaptura = new SistemaCaptura(estadoJuego, gestorPuntaje);

        // Fondo
        batch = new SpriteBatch();
        fondo = new Texture("images/dayro.jpg");

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

        // =========================
        // dibujar zonas
        // =========================
        renderizador.setProjectionMatrix(camara.combined);
        renderizador.begin(ShapeRenderer.ShapeType.Filled);

        for (Zona z : estadoJuego.getZonas()) {
            renderizador.setColor(0, 0, 1, 0.3f);
            renderizador.rect(z.getX(), z.getY(), z.getAncho(), z.getAlto());
        }

        renderizador.end();
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
    }
}
