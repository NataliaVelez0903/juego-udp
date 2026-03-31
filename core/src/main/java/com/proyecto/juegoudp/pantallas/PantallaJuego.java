package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Ficha;

public class PantallaJuego extends ScreenAdapter {
    private OrthographicCamera camara;
    private ShapeRenderer renderizador; // crea una imagen o modelo
    private EstadoJuego estadoJuego;

    public PantallaJuego (){
        inicializar ();
    }

    private void inicializar(){
        // camara
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 800, 600);

        // renderizador de formas
        renderizador = new ShapeRenderer();

        // estado del juego
        estadoJuego = new EstadoJuego();

        // crear fichas
        crearFichas();
    }
    private void crearFichas(){
        estadoJuego.agregarFicha (new Ficha(1,100f,100f));
        estadoJuego.agregarFicha (new Ficha(2,200f,200f));
        estadoJuego.agregarFicha (new Ficha(3, 300f,150f));
    }

    @Override
    public void render (float delta){
        limpiarPantalla();

        camara.update();
        renderizador.setProjectionMatrix(camara.combined);

        renderizador.begin(ShapeRenderer.ShapeType.Filled);

        for (Ficha ficha : estadoJuego.getFichas()) {
            renderizador.circle(ficha.getX(), ficha.getY(), 20);
        }
        renderizador.end();
    }

    private void limpiarPantalla (){
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose(){
        renderizador.dispose();
    }

}
