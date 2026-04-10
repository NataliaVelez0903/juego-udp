package com.proyecto.juegoudp.pantallas.espera;

import com.badlogic.gdx.Gdx;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.red.AnalizadorEstadoUdp;
import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.ServidorUDP;
import com.proyecto.juegoudp.red.TipoMensaje;
import com.proyecto.juegoudp.utilidades.Constantes;
import java.net.InetAddress;

/**
 * Gestiona la conexión UDP de la sala de espera de una partida.
 *
 * Esta clase coordina el proceso de conexión de un jugador a una sala,
 * ya sea actuando como anfitrión o como cliente remoto. En caso de ser
 * anfitrión, inicia un servidor local y establece una conexión cliente
 * hacia {localhost}; en caso contrario, se conecta a la dirección IP
 * del servidor especificada.
 *
 * Además de iniciar y detener la conexión, esta clase se encarga de
 * procesar los mensajes y estados recibidos, mantener actualizada la
 * información de la sala y notificar a la interfaz correspondiente mediante
 * la instancia de {EscuchaSala}.
 *
 * También controla la transición desde la sala de espera hacia la partida
 * una vez que se cumplen las condiciones necesarias, como la asignación de
 * identificador al jugador y la conexión de todos los participantes requeridos.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class ConexionSalaUdp implements IConexionSala {

    /**
     * Referencia al juego principal, desde donde se obtiene la configuración
     * general de la partida y los datos del jugador actual.
     */
    private final JuegoPrincipal juego;

    /**
     * Indica si esta conexión corresponde al anfitrión de la sala.
     */
    private final boolean esAnfitrion;

    /**
     * Dirección IP del servidor al que se conectará el cliente cuando
     * no actúe como anfitrión.
     */
    private final String direccionIpServidor;

    /**
     * Componente encargado de recibir las notificaciones sobre eventos
     * de la conexión y de la sala de espera.
     */
    private final EscuchaSala escucha;

    /**
     * Servidor UDP asociado a la sala cuando el usuario actúa como anfitrión.
     */
    private ServidorUDP servidor;

    /**
     * Cliente UDP utilizado para enviar y recibir información de la sala.
     */
    private ClienteUDP cliente;

    /**
     * Identificador asignado al jugador actual dentro de la partida.
     *
     * Su valor inicial es {-1}, indicando que aún no ha sido asignado.
     */
    private int miIdentificador = -1;

    /**
     * Cantidad de jugadores actualmente conectados a la sala.
     */
    private int jugadoresConectados;

    /**
     * Cantidad mínima de jugadores requerida para iniciar la partida.
     *
     * Su valor inicial predeterminado es 2.
     */
    private int jugadoresRequeridos = 2;

    /**
     * Duración configurada para la partida, expresada en segundos.
     *
     * Su valor inicial predeterminado es 60.
     */
    private int duracionPartidaSegundos = 60;

    /**
     * Indica si ya se está realizando la transición desde la sala
     * de espera hacia la partida.
     */
    private boolean transicionandoAPartida;

    /**
     * Construye una nueva conexión UDP para la sala de espera.
     *
     * @param juego referencia al juego principal
     * @param esAnfitrion {true} si el usuario actuará como anfitrión;
     *                    {false} si actuará como cliente
     * @param direccionIpServidor dirección IP del servidor al que se conectará
     *                            el cliente; si es {null}, se reemplaza
     *                            por una cadena vacía
     * @param escucha componente que recibirá las notificaciones de los eventos
     *                asociados a la sala
     */
    public ConexionSalaUdp(JuegoPrincipal juego, boolean esAnfitrion, String direccionIpServidor, EscuchaSala escucha) {
        this.juego = juego;
        this.esAnfitrion = esAnfitrion;
        this.direccionIpServidor = direccionIpServidor == null ? "" : direccionIpServidor.trim();
        this.escucha = escucha;
    }

    /**
     * Obtiene el identificador asignado al jugador actual.
     *
     * @return el identificador del jugador, o {-1} si aún no ha sido asignado
     */
    public int obtenerMiIdentificador() {
        return miIdentificador;
    }

    /**
     * Obtiene la cantidad de jugadores conectados actualmente a la sala.
     *
     * @return el número de jugadores conectados
     */
    public int obtenerJugadoresConectados() {
        return jugadoresConectados;
    }

    /**
     * Obtiene la cantidad de jugadores requerida para iniciar la partida.
     *
     * @return el número de jugadores requeridos
     */
    public int obtenerJugadoresRequeridos() {
        return jugadoresRequeridos;
    }

    /**
     * Obtiene la duración configurada para la partida.
     *
     * @return la duración de la partida en segundos
     */
    public int obtenerDuracionPartidaSegundos() {
        return duracionPartidaSegundos;
    }

    /**
     * Indica si la sala ya está realizando la transición hacia la partida.
     *
     * @return {true} si ya se inició la transición;
     *         {false} en caso contrario
     */
    public boolean estaPasandoAPartida() {
        return transicionandoAPartida;
    }

    /**
     * Obtiene la referencia al servidor UDP asociado a la sala.
     *
     * @return el servidor UDP, o {null} si no existe
     */
    public ServidorUDP obtenerServidor() {
        return servidor;
    }

    /**
     * Obtiene la referencia al cliente UDP utilizado por la sala.
     *
     * @return el cliente UDP, o {null} si no existe
     */
    public ClienteUDP obtenerCliente() {
        return cliente;
    }

    /**
     * Inicia la conexión UDP de la sala de espera.
     *
     * Este método valida y ajusta la configuración de jugadores y duración
     * de la partida. Si el usuario actúa como anfitrión, crea e inicia un
     * servidor UDP local y luego conecta un cliente a {localhost}.
     * Si el usuario actúa como cliente, intenta conectarse a la dirección IP
     * proporcionada.
     *
     * Además, registra los callbacks necesarios para procesar mensajes
     * y estados recibidos, y finalmente envía al servidor un mensaje de unión
     * con el nombre y avatar del jugador actual.
     */
    @Override
    public void iniciar() {
        try {
            jugadoresRequeridos = Math.max(2, Math.min(juego.getConfiguracion().getNumeroJugadores(), Constantes.MAX_JUGADORES));
            duracionPartidaSegundos = Math.max(30, (int) juego.getConfiguracion().getTiempoLimite());

            if (esAnfitrion) {
                servidor = new ServidorUDP(jugadoresRequeridos, duracionPartidaSegundos);
                servidor.start();
                cliente = new ClienteUDP("localhost");
            } else {
                if (direccionIpServidor.isEmpty()) {
                    escucha.alFallo("Error: falta IP del host.");
                    return;
                }
                cliente = new ClienteUDP(direccionIpServidor);
            }

            cliente.setCallbackEstado(estado -> Gdx.app.postRunnable(() -> alRecibirEstado(estado)));
            cliente.setCallbackMensaje(msg -> Gdx.app.postRunnable(() -> alRecibirMensaje(msg)));

            String datosUnirse = juego.getNombreJugador() + "\t" + juego.getAvatarSeleccionado();
            cliente.enviarMensaje(new Mensaje(TipoMensaje.UNIRSE, 0, 0, 0, 0, 0, 0, datosUnirse));
        } catch (Exception e) {
            escucha.alFallo("Error: " + e.getMessage());
        }
    }

    /**
     * Detiene la conexión UDP de la sala de espera.
     *
     * Si la transición hacia la partida ya está en curso, este método
     * no realiza ninguna acción. En caso contrario, detiene el servidor UDP
     * si existe y cierra el cliente UDP asociado.
     */
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

    /**
     * Procesa un mensaje recibido desde la conexión UDP.
     *
     * Primero notifica el mensaje a la interfaz de escucha. Luego,
     * si el mensaje corresponde al tipo { TipoMensaje#TU_ID},
     * valida la asignación del identificador del jugador. Si el servidor
     * rechaza la unión, informa del fallo y detiene la conexión.
     * Si la asignación es correcta, actualiza el identificador local
     * e intenta iniciar la partida.
     *
     * @param mensaje el mensaje recibido desde el servidor
     */
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

    /**
     * Procesa el estado serializado recibido desde la conexión UDP.
     *
     * Este método verifica que la cadena recibida corresponda a un estado
     * válido, extrae la cantidad de jugadores conectados y, si está disponible,
     * actualiza la cantidad de jugadores requeridos para iniciar la partida.
     * Posteriormente notifica estos datos al componente de escucha e intenta
     * iniciar la partida si ya se cumplen las condiciones necesarias.
     *
     * @param estadoSerializado el estado recibido en formato de texto
     */
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

    /**
     * Verifica si ya se cumplen las condiciones necesarias para iniciar la partida.
     *
     * La transición solo se realiza si todavía no ha comenzado, el jugador
     * actual ya tiene identificador asignado y la cantidad de jugadores
     * conectados es suficiente. Cuando estas condiciones se cumplen,
     * se marca la transición como iniciada y se notifica a la interfaz
     * de escucha para comenzar la partida.
     */
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

    /**
     * Obtiene la dirección IP local de la máquina actual.
     *
     * @return la dirección IP local si puede resolverse;
     *         en caso de error, retorna {"?"}
     */
    public String obtenerDireccionIpLocal() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "?";
        }
    }
}
