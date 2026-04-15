package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.utilidades.Constantes;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Representa el cliente UDP encargado de comunicarse con el servidor.
 * 
 *
 *
 * Esta clase permite enviar mensajes al servidor y recibir información
 * desde la red, ya sea en forma de estados serializados de la partida
 * o como mensajes específicos del protocolo de comunicación.
 *
 * La recepción de datos se realiza en un hilo independiente de escucha,
 * lo que permite mantener la comunicación activa sin bloquear
 * el flujo principal de la aplicación.
 *
 * También ofrece mecanismos de devolución de llamada para que otros
 * componentes puedan reaccionar ante la recepción de estados
 * o mensajes desde el servidor.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class ClienteUDP {

    /**
     * Socket UDP utilizado para enviar y recibir paquetes de red.
     */
    private DatagramSocket conexionDatagrama;

    /**
     * Dirección IP del servidor al que se enviarán los mensajes.
     */
    private InetAddress direccionServidor;

    /**
     * Puerto del servidor UDP.
     *
     * Su valor se inicializa a partir de la constante global del sistema.
     */
    private int puertoServidor = Constantes.PUERTO_UDP;

    /**
     * Indica si el cliente se encuentra activo y en proceso de escucha.
     */
    private boolean activo;

    /**
     * Acción a ejecutar cuando se recibe un estado serializado de la partida.
     */
    private Consumer<String> alRecibirEstado;

    /**
     * Acción a ejecutar cuando se recibe un mensaje del protocolo.
     */
    private Consumer<Mensaje> alRecibirMensaje;

    /**
     * Construye un nuevo cliente UDP y lo prepara para la comunicación
     * con el servidor indicado.
     *
     * Este constructor crea el socket UDP, resuelve la dirección IP
     * del servidor, activa el cliente e inicia el hilo de escucha
     * para recibir paquetes desde la red.
     *
     * @param direccionIpServidor dirección IP del servidor al que se conectará el cliente
     * @throws Exception si ocurre un error al crear el socket o resolver la dirección IP
     */
    public ClienteUDP(String direccionIpServidor) throws Exception {
        conexionDatagrama = new DatagramSocket();
        direccionServidor = InetAddress.getByName(direccionIpServidor);
        activo = true;
        iniciarEscucha();
        System.out.println("[Cliente] Conectado a " + direccionIpServidor + ":" + puertoServidor);
    }

    /**
     * Inicia el hilo de escucha encargado de recibir paquetes UDP
     * desde el servidor.
     *
     * Este método crea un hilo independiente que permanece activo
     * mientras el cliente esté en funcionamiento. Cada paquete recibido
     * se interpreta como un estado serializado o como un mensaje
     * del protocolo, y se delega al callback correspondiente.
     */
    private void iniciarEscucha() {
        new Thread(() -> {
            byte[] memoriaRecepcion = new byte[8192];
            while (activo) {
                try {
                    DatagramPacket paquete = new DatagramPacket(memoriaRecepcion, memoriaRecepcion.length);
                    conexionDatagrama.receive(paquete);
                    String texto = new String(paquete.getData(), 0, paquete.getLength(), StandardCharsets.UTF_8);
                    if (texto.startsWith("STATE|")) {
                        if (alRecibirEstado != null) {
                            alRecibirEstado.accept(texto);
                        }
                    } else {
                        Mensaje mensaje = Mensaje.deserializar(texto);
                        if (mensaje != null && alRecibirMensaje != null) {
                            alRecibirMensaje.accept(mensaje);
                        }
                    }
                } catch (Exception e) {
                    if (activo) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * Envía un mensaje al servidor UDP.
     *
     * Este método serializa el mensaje recibido, lo convierte
     * a un arreglo de bytes codificado en UTF-8 y lo envía
     * mediante un paquete UDP al servidor configurado.
     *
     * Si ocurre un error durante el envío, se muestra un mensaje
     * en la salida de error.
     *
     * @param mensaje mensaje que se desea enviar al servidor
     */
    public void enviarMensaje(Mensaje mensaje) {
        try {
            String texto = mensaje.serializar();
            byte[] datos = texto.getBytes(StandardCharsets.UTF_8);
            DatagramPacket paquete = new DatagramPacket(datos, datos.length, direccionServidor, puertoServidor);
            conexionDatagrama.send(paquete);
        } catch (Exception e) {
            System.err.println("[Cliente] Error al enviar: " + e.getMessage());
        }
    }

    /**
     * Define la acción que se ejecutará cuando se reciba un estado
     * serializado de la partida.
     *
     * @param consumidor función que procesará el estado recibido
     */
    public void setCallbackEstado(Consumer<String> consumidor) {
        this.alRecibirEstado = consumidor;
    }

    /**
     * Define la acción que se ejecutará cuando se reciba un mensaje
     * del protocolo de comunicación.
     *
     * @param consumidor función que procesará el mensaje recibido
     */
    public void setCallbackMensaje(Consumer<Mensaje> consumidor) {
        this.alRecibirMensaje = consumidor;
    }

    /**
     * Cierra la conexión del cliente UDP.
     *
     * Este método desactiva el estado de escucha del cliente
     * y cierra el socket asociado, finalizando la comunicación de red.
     */
    public void cerrar() {
        activo = false;
        conexionDatagrama.close();
    }
}
