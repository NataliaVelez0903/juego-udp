package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.utilidades.Constantes;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Cliente UDP: envía mensajes al servidor y recibe estado o mensajes en un hilo de escucha.
 */
public class ClienteUDP {
    private DatagramSocket conexionDatagrama;
    private InetAddress direccionServidor;
    private int puertoServidor = Constantes.PUERTO_UDP;
    private boolean activo;
    private Consumer<String> alRecibirEstado;
    private Consumer<Mensaje> alRecibirMensaje;

    public ClienteUDP(String direccionIpServidor) throws Exception {
        conexionDatagrama = new DatagramSocket();
        direccionServidor = InetAddress.getByName(direccionIpServidor);
        activo = true;
        iniciarEscucha();
        System.out.println("[Cliente] Conectado a " + direccionIpServidor + ":" + puertoServidor);
    }

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

    public void setCallbackEstado(Consumer<String> consumidor) {
        this.alRecibirEstado = consumidor;
    }

    public void setCallbackMensaje(Consumer<Mensaje> consumidor) {
        this.alRecibirMensaje = consumidor;
    }

    public void cerrar() {
        activo = false;
        conexionDatagrama.close();
    }
}
