package com.proyecto.juegoudp;

import com.badlogic.gdx.Game;
import com.proyecto.juegoudp.modelo.ConfiguracionPartida;
import com.proyecto.juegoudp.pantallas.PantallaMenu;
import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.ServidorUDP;

/**
 * Representa la clase principal del juego.
 *
 * Esta clase actúa como punto de entrada de la aplicación en libGDX y se encarga
 * de la configuración general del juego, el almacenamiento de datos básicos de la
 * partida y la transición entre pantallas.
 *
 * Además, mantiene información asociada al jugador local, como su nombre,
 * el avatar seleccionado y la configuración actual de la partida.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class JuegoPrincipal extends Game {
    /**
     * Configuración actual de la partida.
     */
    private ConfiguracionPartida configuracion;

    /**
     * Nombre del jugador local.
     */
    private String nombreJugador;

    /**
     * Identificador del avatar seleccionado por el jugador.
     */
    private int avatarSeleccionado;

    /**
     * Inicializa la aplicación y establece la pantalla principal del menú.
     *
     * En este método se crea la configuración inicial de la partida y se define
     * la primera pantalla visible al usuario.
     */
    @Override
    public void create() {
        configuracion = new ConfiguracionPartida();
        setScreen(new PantallaMenu(this));
    }

    /**
     * Establece el nombre del jugador local.
     *
     * @param nombre nombre que se asignará al jugador
     */
    public void setNombreJugador(String nombre) {
        this.nombreJugador = nombre;
    }

    /**
     * Obtiene el nombre del jugador local.
     *
     * @return nombre actual del jugador
     */
    public String getNombreJugador() {
        return nombreJugador;
    }

    /**
     * Establece el avatar seleccionado por el jugador.
     *
     * @param avatar identificador del avatar seleccionado
     */
    public void setAvatarSeleccionado(int avatar) {
        this.avatarSeleccionado = avatar;
    }

    /**
     * Obtiene el avatar seleccionado por el jugador.
     *
     * @return identificador del avatar actual
     */
    public int getAvatarSeleccionado() {
        return avatarSeleccionado;
    }

    /**
     * Obtiene la configuración actual de la partida.
     *
     * @return configuración almacenada en el juego
     */
    public ConfiguracionPartida getConfiguracion() {
        return configuracion;
    }

    /**
     * Reemplaza la configuración actual de la partida.
     *
     * @param config nueva configuración que se desea establecer
     */
    public void setConfiguracion(ConfiguracionPartida config) {
        this.configuracion = config;
    }

    /**
     * Inicia la pantalla de juego usando la información básica de conexión.
     *
     * Este método redirige internamente al inicio desde sala de espera,
     * indicando que no se reutilizan instancias previas de servidor o cliente.
     *
     * @param esAnfitrion indica si el jugador local actuará como anfitrión
     * @param direccionIpServidor dirección IP del servidor al que se conectará el cliente
     */
    public void iniciarJuego(boolean esAnfitrion, String direccionIpServidor) {
        iniciarJuegoDesdeEspera(esAnfitrion, direccionIpServidor, null, null);
    }

    /**
     * Inicia la pantalla principal de juego reutilizando, si existen,
     * las instancias de servidor y cliente UDP creadas previamente.
     *
     * Este método se utiliza especialmente después de pasar por la sala de espera,
     * permitiendo conservar la infraestructura de red ya inicializada.
     *
     * @param esAnfitrion indica si el jugador local actuará como anfitrión
     * @param direccionIpServidor dirección IP del servidor de la partida
     * @param servidor instancia del servidor UDP ya creada, o null si no se reutiliza
     * @param cliente instancia del cliente UDP ya creada, o null si no se reutiliza
     */
    public void iniciarJuegoDesdeEspera(
        boolean esAnfitrion,
        String direccionIpServidor,
        ServidorUDP servidor,
        ClienteUDP cliente
    ) {
        setScreen(new com.proyecto.juegoudp.pantallas.PantallaJuego(
            this,
            esAnfitrion,
            direccionIpServidor,
            nombreJugador,
            avatarSeleccionado,
            servidor,
            cliente
        ));
    }

    /**
     * Regresa al usuario a la pantalla principal del menú.
     */
    public void volverAlMenu() {
        setScreen(new PantallaMenu(this));
    }
}
