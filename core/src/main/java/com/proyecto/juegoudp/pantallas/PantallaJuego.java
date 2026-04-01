package com.proyecto.juegoudp.pantallas;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.ScreenAdapter; //Clase de libGDX que permite crear pantallas facilmente
import com.badlogic.gdx.graphics.OrthographicCamera;// camara 2D del juego
import com.badlogic.gdx.Gdx; // acceso a funciones globales de libGDX
import com.badlogic.gdx.graphics.GL20; // permite limpiar pantalla y usar colores
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;// Herramientas para dibujar formas
/***
 * clase para colores
 */

import com.badlogic.gdx.graphics.Color;
// importar clases de modelo
import com.proyecto.juegoudp.logica.SistemaArrastre;
import com.proyecto.juegoudp.logica.SistemaColisiones;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Ficha;


public class PantallaJuego extends ScreenAdapter {
    private OrthographicCamera camara;

    private SpriteBatch batch;
    /**
     * para el fondo
     * */
    private Texture fondo;
    /**
     * Para la ficha
     * */
    private Texture texturaFicha;
    private ShapeRenderer renderizador; // se encarga de dibujar los circulos
    private EstadoJuego estadoJuego;
    /***
     *
     * Se instancia un objeto de nuestro sistema de arrastre
     */

    private SistemaArrastre sistemaArrastre;
    /**
     * Se instancia un objeto para las colisiones
     * */
    private SistemaColisiones sistemaColisiones;
    public PantallaJuego (){
        inicializar ();
    }

    private void inicializar(){

        // 1️crear estado del juego P
        estadoJuego = new EstadoJuego();
        /*
        *Fondo
        * */
        batch = new SpriteBatch();
        fondo = new Texture("images/dayro.jpg");
        /*
         * textura ficha
         * */
        texturaFicha = new Texture("images/aguardienteAmarillo.png");
        // 2⃣crear fichas
        crearFichas();



        //cámara
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 800, 600);

        int miJugadorId = 1;
        sistemaArrastre = new SistemaArrastre(estadoJuego, miJugadorId, camara);

        // 5️⃣renderizador
        renderizador = new ShapeRenderer();

        sistemaColisiones = new SistemaColisiones(estadoJuego);
    }
    private void crearFichas(){
        estadoJuego.agregarFicha (new Ficha(1,100f,100f));
        estadoJuego.agregarFicha (new Ficha(2,200f,200f));
        estadoJuego.agregarFicha (new Ficha(3, 300f,150f));



    }

    @Override
    public void render (float delta){
        limpiarPantalla();
        /**
         * Se encarga de las colisiones
         * */
        sistemaColisiones.actualizar();
        camara.update();

        /**
         * dibujar fondo
         * */
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        batch.draw(fondo, 0, 0, 800, 600);

        // 🔥 dibujar fichas con imagen
        for (Ficha ficha : estadoJuego.getFichas()) {

            float size = 100; // tamaño de la ficha

            /***
             * Se cambia el color de la ficha que se este moviendo
             *
             */
            if (ficha.isArrastrando()) {
                batch.setColor(1, 0, 0, 1); // rojo
            } else {
                batch.setColor(1, 1, 1, 1); // normal
            }


            batch.draw(
                texturaFicha,
                ficha.getX() - size / 2,
                ficha.getY() - size / 2,
                size + 40,
                size
            );
        }

        // ⚠️ resetear color
        batch.setColor(1, 1, 1, 1);

        batch.end();


    }

    private void limpiarPantalla (){
        Gdx.gl.glClearColor(0,0,0,1); // color de fondo negro
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // borra la pantalla
    }

    @Override
    public void dispose(){
        renderizador.dispose(); // libera recursos
    }

}
