package com.proyecto.juegoudp.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.ServidorUDP;
import com.proyecto.juegoudp.red.TipoMensaje;

/**
 * Crea cliente/servidor UDP, registra escuchas y envía {@code UNIRSE}.
 */
public final class InicializadorRedPartida {
    private InicializadorRedPartida() {}

    /** Cliente UDP y, si aplica, referencia al servidor del anfitrión. */
    public static final class ResultadoConexion {
        public final ClienteUDP cliente;
        public final ServidorUDP servidor;

        public ResultadoConexion(ClienteUDP cliente, ServidorUDP servidor) {
            this.cliente = cliente;
            this.servidor = servidor;
        }
    }

    /** Recibe cada instantánea {@code STATE|...} del servidor. */
    public interface EscuchaEstadoSerializado {
        void alRecibirEstado(String estadoSerializado);
    }

    /** Recibe el id de jugador asignado o un mensaje de error (p. ej. sala llena). */
    public interface EscuchaIdentificadorJugador {
        void alRecibirIdentificador(int idJugador, String datosError);
    }

    /**
     * @param clienteExistente reutilizado desde sala de espera si no es null
     * @param servidorExistente solo anfitrión, reutilizado si no es null
     */
    public static ResultadoConexion conectar(
            boolean esAnfitrion,
            String direccionIpServidor,
            String nombreJugador,
            int idAvatar,
            ServidorUDP servidorExistente,
            ClienteUDP clienteExistente,
            EscuchaEstadoSerializado escuchaEstado,
            EscuchaIdentificadorJugador escuchaIdentificador
    ) throws Exception {
        ClienteUDP cliente;
        ServidorUDP servidor = null;

        if (clienteExistente != null) {
            cliente = clienteExistente;
            if (esAnfitrion && servidorExistente != null) {
                servidor = servidorExistente;
            }
        } else if (esAnfitrion) {
            servidor = new ServidorUDP();
            servidor.start();
            cliente = new ClienteUDP("localhost");
        } else {
            cliente = new ClienteUDP(direccionIpServidor);
        }

        cliente.setCallbackEstado(estado -> Gdx.app.postRunnable(() -> escuchaEstado.alRecibirEstado(estado)));
        cliente.setCallbackMensaje(mensaje -> {
            if (mensaje.getTipo() != TipoMensaje.TU_ID) {
                return;
            }
            Gdx.app.postRunnable(() -> escuchaIdentificador.alRecibirIdentificador(mensaje.getIdJugador(), mensaje.getDatos()));
        });

        String datosUnirse = nombreJugador + "\t" + idAvatar;
        cliente.enviarMensaje(new Mensaje(TipoMensaje.UNIRSE, 0, 0, 0, 0, 0, 0, datosUnirse));

        return new ResultadoConexion(cliente, servidor);
    }
}
