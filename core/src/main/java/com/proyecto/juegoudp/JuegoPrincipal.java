package com.proyecto.juegoudp;

import com.badlogic.gdx.Game;
import com.proyecto.juegoudp.modelo.ConfiguracionPartida;
import com.proyecto.juegoudp.pantallas.PantallaMenu;
<<<<<<< HEAD
import com.proyecto.juegoudp.red.ClienteUdp;
=======
import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.ServidorUDP;
>>>>>>> 32857898659ebd838fe83e4507b0e8a04a86503c

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
            ServidorUDP servidor,
            ClienteUDP cliente
    ) {
        setScreen(new com.proyecto.juegoudp.pantallas.PantallaJuego(this, esAnfitrion, direccionIpServidor,
                nombreJugador, avatarSeleccionado, servidor, cliente));
    }

    public void volverAlMenu() {
        setScreen(new PantallaMenu(this));
    }
}
