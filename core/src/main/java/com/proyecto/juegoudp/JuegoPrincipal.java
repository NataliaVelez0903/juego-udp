package com.proyecto.juegoudp;

import com.badlogic.gdx.Game;
import com.proyecto.juegoudp.modelo.ConfiguracionPartida;
import com.proyecto.juegoudp.pantallas.PantallaMenu;
import com.proyecto.juegoudp.red.ClienteUdp;

/**
 * Punto de entrada del juego libGDX: configuración global y transición entre pantallas.
 * La descripción del proyecto y la arquitectura están en {@code README.md} en la raíz del repositorio.
 */
public class JuegoPrincipal extends Game {
    private ConfiguracionPartida configuracion;
    private String nombreJugador;
    private int avatarSeleccionado;

    @Override
    public void create() {
        configuracion = new ConfiguracionPartida();
        setScreen(new PantallaMenu(this));
    }

    public void setNombreJugador(String nombre) {
        this.nombreJugador = nombre;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setAvatarSeleccionado(int avatar) {
        this.avatarSeleccionado = avatar;
    }

    public int getAvatarSeleccionado() {
        return avatarSeleccionado;
    }

    public ConfiguracionPartida getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(ConfiguracionPartida config) {
        this.configuracion = config;
    }

    public void iniciarJuego(boolean esAnfitrion, String direccionIpServidor) {
        iniciarJuegoDesdeEspera(esAnfitrion, direccionIpServidor, null, null);
    }

    /** Tras la sala de espera: reutiliza servidor/cliente UDP si no son null. */
    public void iniciarJuegoDesdeEspera(
            boolean esAnfitrion,
            String direccionIpServidor,
            ServidorUdp servidor,
            ClienteUdp cliente
    ) {
        setScreen(new com.proyecto.juegoudp.pantallas.PantallaJuego(this, esAnfitrion, direccionIpServidor,
                nombreJugador, avatarSeleccionado, servidor, cliente));
    }

    public void volverAlMenu() {
        setScreen(new PantallaMenu(this));
    }
}
