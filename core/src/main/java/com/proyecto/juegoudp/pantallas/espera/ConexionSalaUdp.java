package com.proyecto.juegoudp.pantallas.espera;

import com.badlogic.gdx.Gdx;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.red.AnalizadorEstadoUdp;
import com.proyecto.juegoudp.red.ClienteUdp;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.TipoMensaje;
import com.proyecto.juegoudp.utilidades.Constantes;
import java.net.InetAddress;

/**
 * Conexión UDP de la sala: host con servidor local o cliente remoto.
 */
public class ConexionSalaUdp implements IConexionSala {
    private final JuegoPrincipal juego;
    private final boolean esAnfitrion;
    private final String direccionIpServidor;
    private final EscuchaSala escucha;

    private ServidorUdp servidor;
    private ClienteUdp cliente;
    private int miIdentificador = -1;
    private int jugadoresConectados;
    private int jugadoresRequeridos = 2;
    private int duracionPartidaSegundos = 60;
    private boolean transicionandoAPartida;

    public ConexionSalaUdp(JuegoPrincipal juego, boolean esAnfitrion, String direccionIpServidor, EscuchaSala escucha) {
        this.juego = juego;
        this.esAnfitrion = esAnfitrion;
        this.direccionIpServidor = direccionIpServidor == null ? "" : direccionIpServidor.trim();
        this.escucha = escucha;
    }

    public int obtenerMiIdentificador() {
        return miIdentificador;
    }

    public int obtenerJugadoresConectados() {
        return jugadoresConectados;
    }

    public int obtenerJugadoresRequeridos() {
        return jugadoresRequeridos;
    }

    public int obtenerDuracionPartidaSegundos() {
        return duracionPartidaSegundos;
    }

    public boolean estaPasandoAPartida() {
        return transicionandoAPartida;
    }

    public ServidorUdp obtenerServidor() {
        return servidor;
    }

    public ClienteUdp obtenerCliente() {
        return cliente;
    }

    @Override
    public void iniciar() {
        try {
            jugadoresRequeridos = Math.max(2, Math.min(juego.getConfiguracion().getNumeroJugadores(), Constantes.MAX_JUGADORES));
            duracionPartidaSegundos = Math.max(30, (int) juego.getConfiguracion().getTiempoLimite());

            if (esAnfitrion) {
                servidor = new ServidorUdp(jugadoresRequeridos, duracionPartidaSegundos);
                servidor.start();
                cliente = new ClienteUdp("localhost");
            } else {
                if (direccionIpServidor.isEmpty()) {
                    escucha.alFallo("Error: falta IP del host.");
                    return;
                }
                cliente = new ClienteUdp(direccionIpServidor);
            }

            cliente.setCallbackEstado(estado -> Gdx.app.postRunnable(() -> alRecibirEstado(estado)));
            cliente.setCallbackMensaje(msg -> Gdx.app.postRunnable(() -> alRecibirMensaje(msg)));

            String datosUnirse = juego.getNombreJugador() + "\t" + juego.getAvatarSeleccionado();
            cliente.enviarMensaje(new Mensaje(TipoMensaje.UNIRSE, 0, 0, 0, 0, 0, 0, datosUnirse));
        } catch (Exception e) {
            escucha.alFallo("Error: " + e.getMessage());
        }
    }

    @Override
    public void detener() {
        if (transicionandoAPartida) {
            return;
        }
        if (servidor != null) {
            servidor.detener();
            servidor = null;
        }
        if (cliente != null) {
            cliente.cerrar();
            cliente = null;
        }
    }

    private void alRecibirMensaje(Mensaje mensaje) {
        escucha.alRecibirMensaje(mensaje);
        if (mensaje.getTipo() != TipoMensaje.TU_ID) {
            return;
        }
        if (mensaje.getIdJugador() < 0) {
            escucha.alFallo("No se pudo unir: " + mensaje.getDatos());
            detener();
            return;
        }
        miIdentificador = mensaje.getIdJugador();
        escucha.alAsignarIdJugador(miIdentificador);
        intentarIniciarPartida();
    }

    private void alRecibirEstado(String estadoSerializado) {
        if (!estadoSerializado.startsWith("STATE|")) {
            return;
        }
        jugadoresConectados = AnalizadorEstadoUdp.contarJugadores(
                AnalizadorEstadoUdp.segmentoJugadores(estadoSerializado));
        int requeridosLeidos = AnalizadorEstadoUdp.leerJugadoresRequeridos(estadoSerializado);
        if (requeridosLeidos > 0) {
            jugadoresRequeridos = requeridosLeidos;
        }
        escucha.alActualizarConectados(jugadoresConectados, jugadoresRequeridos);
        intentarIniciarPartida();
    }

    private void intentarIniciarPartida() {
        if (transicionandoAPartida) {
            return;
        }
        if (miIdentificador < 0) {
            return;
        }
        if (jugadoresConectados < jugadoresRequeridos) {
            return;
        }
        transicionandoAPartida = true;
        escucha.alIniciarPartida();
    }

    public String obtenerDireccionIpLocal() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "?";
        }
    }
}
