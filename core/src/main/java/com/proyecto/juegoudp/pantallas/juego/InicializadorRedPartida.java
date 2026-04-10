package com.proyecto.juegoudp.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.ServidorUDP;
import com.proyecto.juegoudp.red.TipoMensaje;

/**
 * Inicializa la conexión de red de la partida y configura la comunicación UDP.
 *
 * Esta clase utilitaria se encarga de crear o reutilizar las instancias
 * de cliente y servidor UDP necesarias para la partida, registrar las
 * escuchas de estado e identificación del jugador y enviar el mensaje
 * inicial de unión al servidor.
 *
 * Su propósito es centralizar la lógica de inicialización de la red
 * para la pantalla de partida, evitando que dicha responsabilidad
 * quede dispersa en otros componentes.
 *
 * Al ser una clase de utilidad, no está pensada para ser instanciada.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public final class InicializadorRedPartida {

    /**
     * Constructor privado para evitar la creación de instancias de esta clase.
     */
    private InicializadorRedPartida() {}

    /**
     * Representa el resultado del proceso de conexión de red de la partida.
     *
     * Esta clase agrupa las referencias al cliente UDP utilizado por la partida
     * y, en caso de que el jugador actúe como anfitrión, al servidor UDP asociado.
     */
    public static final class ResultadoConexion {

        /**
         * Cliente UDP utilizado para la comunicación de la partida.
         */
        public final ClienteUDP cliente;

        /**
         * Servidor UDP asociado a la partida cuando el jugador actúa
         * como anfitrión. Puede ser null si no aplica.
         */
        public final ServidorUDP servidor;

        /**
         * Construye un resultado de conexión con las referencias
         * al cliente y al servidor correspondientes.
         *
         * @param cliente el cliente UDP asociado a la partida
         * @param servidor el servidor UDP asociado a la partida,
         *                 o null si no existe
         */
        public ResultadoConexion(ClienteUDP cliente, ServidorUDP servidor) {
            this.cliente = cliente;
            this.servidor = servidor;
        }
    }

    /**
     * Define el contrato para recibir estados serializados de la partida
     * provenientes del servidor.
     */
    public interface EscuchaEstadoSerializado {

        /**
         * Notifica la recepción de un estado serializado de la partida.
         *
         * @param estadoSerializado el estado recibido en formato de texto
         */
        void alRecibirEstado(String estadoSerializado);
    }

    /**
     * Define el contrato para recibir el identificador asignado al jugador
     * o información de error asociada al intento de conexión.
     */
    public interface EscuchaIdentificadorJugador {

        /**
         * Notifica la recepción del identificador asignado al jugador
         * o de un mensaje de error devuelto por el servidor.
         *
         * @param idJugador el identificador asignado al jugador
         * @param datosError información adicional de error, si existe
         */
        void alRecibirIdentificador(int idJugador, String datosError);
    }

    /**
     * Establece la conexión de red de la partida, creando o reutilizando
     * el cliente y servidor UDP según corresponda.
     *
     * Este método reutiliza las instancias ya existentes si se proporcionan.
     * En caso contrario, crea una nueva conexión según el rol del jugador:
     * si actúa como anfitrión, puede crear tanto el servidor como el cliente;
     * si actúa como cliente, crea únicamente el cliente conectado a la
     * dirección IP indicada.
     *
     * Además, registra las funciones de escucha para procesar estados
     * serializados y mensajes de asignación de identificador, y finalmente
     * envía al servidor un mensaje de unión con el nombre y avatar del jugador.
     *
     * @param esAnfitrion indica si el jugador actual actúa como anfitrión
     * @param direccionIpServidor dirección IP del servidor al que se conectará
     *                            el cliente cuando no sea anfitrión
     * @param nombreJugador nombre del jugador que solicita la unión
     * @param idAvatar identificador del avatar seleccionado por el jugador
     * @param servidorExistente servidor UDP ya existente, reutilizable por el anfitrión
     * @param clienteExistente cliente UDP ya existente, reutilizable si no es null
     * @param escuchaEstado escucha encargada de recibir estados serializados
     * @param escuchaIdentificador escucha encargada de recibir el identificador
     *                             del jugador o un mensaje de error
     * @return un objeto con las referencias al cliente y al servidor utilizados
     * @throws Exception si ocurre un error durante la creación o configuración
     *                   de la conexión de red
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
