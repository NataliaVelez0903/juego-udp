package com.proyecto.juegoudp;

import com.badlogic.gdx.Game;
import com.proyecto.juegoudp.pantallas.PantallaJuego;


// se hereda el comportamiento de libGDX
public class Main extends Game{

    @Override
    public void create (){
        setScreen(new PantallaJuego());
    }
}
