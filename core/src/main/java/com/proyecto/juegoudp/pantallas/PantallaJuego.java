package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.ScreenAdapter; //Clase de libGDX que permite crear pantallas facilmente
import com.badlogic.gdx.graphics.OrthographicCamera;// camara 2D del juego
import com.badlogic.gdx.Gdx; // acceso a funciones globales de libGDX
import com.badlogic.gdx.graphics.GL20; // permite limpiar pantalla y usar colores
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;// Herramientas para dibujar formas

// importar clases de modelo
import com.proyecto.juegoudp.logica.SistemaArrastre;
import com.proyecto.juegoudp.logica.SistemaColisiones;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Ficha;


public class PantallaJuego extends ScreenAdapter {
    private OrthographicCamera camara;
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
        // le dice a renderizador como usar la camara
        renderizador.setProjectionMatrix(camara.combined);
        //empieza a dibujar formas rellenas
        renderizador.begin(ShapeRenderer.ShapeType.Filled);
        // recorre todas las fichas del juego
        for (Ficha ficha : estadoJuego.getFichas()) {
            // dibuja un circulo en la posicion de la ficha
            renderizador.circle(ficha.getX(), ficha.getY(), 20);
        }
        renderizador.end();
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
