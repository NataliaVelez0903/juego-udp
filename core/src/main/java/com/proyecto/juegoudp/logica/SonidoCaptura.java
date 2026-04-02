package com.proyecto.juegoudp.logica;


import com.badlogic.gdx.audio.Sound;

public class SonidoCaptura implements IReproducirSonido {

    private Sound sonido;

    public SonidoCaptura(Sound sonido) {
        this.sonido = sonido;
    }

    @Override
    public void reproducir() {
        sonido.play();
    }
    public void dispose() {
        sonido.dispose();
    }
}
