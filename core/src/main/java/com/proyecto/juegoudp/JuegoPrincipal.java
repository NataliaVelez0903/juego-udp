package com.proyecto.juegoudp;

import com.badlogic.gdx.Game;
import com.proyecto.juegoudp.modelo.ConfiguracionPartida;
import com.proyecto.juegoudp.pantallas.PantallaMenu;

public class JuegoPrincipal extends Game {
    private ConfiguracionPartida configuracion;
    private String nombreJugador;
    private int avatarSeleccionado;

    @Override
    public void create() {
        configuracion = new ConfiguracionPartida();
        setScreen(new PantallaMenu(this));
    }

    public void setNombreJugador(String nombre) { this.nombreJugador = nombre; }
    public String getNombreJugador() { return nombreJugador; }
    public void setAvatarSeleccionado(int avatar) { this.avatarSeleccionado = avatar; }
    public int getAvatarSeleccionado() { return avatarSeleccionado; }
    public ConfiguracionPartida getConfiguracion() { return configuracion; }
    public void setConfiguracion(ConfiguracionPartida config) { this.configuracion = config; }

    public void iniciarJuego(boolean esHost, String ipServidor) {
        setScreen(new com.proyecto.juegoudp.pantallas.PantallaJuego(this, esHost, ipServidor,
                nombreJugador, avatarSeleccionado));
    }

    public void volverAlMenu() {
        setScreen(new PantallaMenu(this));
    }
}