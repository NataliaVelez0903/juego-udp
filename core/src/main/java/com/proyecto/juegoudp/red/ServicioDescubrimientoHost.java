package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.utilidades.Constantes;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * Escucha solicitudes de descubrimiento LAN y responde con datos del host.
 */
public class ServicioDescubrimientoHost {
    private static final String MENSAJE_BUSQUEDA = "DISCOVER_PELOTEROS";

    private final String nombreHost;
    private final int jugadoresRequeridos;
    private final int tiempoSegundos;

    private DatagramSocket socketDescubrimiento;
    private volatile boolean activo;
    private Thread hilo;

    public ServicioDescubrimientoHost(String nombreHost, int jugadoresRequeridos, int tiempoSegundos) {
        this.nombreHost = (nombreHost == null || nombreHost.isBlank()) ? "Host" : nombreHost.trim();
        this.jugadoresRequeridos = jugadoresRequeridos;
        this.tiempoSegundos = tiempoSegundos;
    }

    public void iniciar() throws Exception {
        if (activo) {
            return;
        }
        socketDescubrimiento = new DatagramSocket(Constantes.PUERTO_DESCUBRIMIENTO_UDP);
        socketDescubrimiento.setBroadcast(true);
        socketDescubrimiento.setSoTimeout(600);
        activo = true;
        hilo = new Thread(this::bucleEscucha, "lan-host-discovery");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void bucleEscucha() {
        byte[] buffer = new byte[1024];
        while (activo) {
            try {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socketDescubrimiento.receive(paquete);
                String texto = new String(paquete.getData(), 0, paquete.getLength(), StandardCharsets.UTF_8).trim();
                if (!MENSAJE_BUSQUEDA.equals(texto)) {
                    continue;
                }
                String respuesta = "HOST_INFO|" + nombreHost + "|" + jugadoresRequeridos + "|" + tiempoSegundos;
                byte[] datos = respuesta.getBytes(StandardCharsets.UTF_8);
                DatagramPacket resp = new DatagramPacket(datos, datos.length, paquete.getAddress(), paquete.getPort());
                socketDescubrimiento.send(resp);
            } catch (SocketTimeoutException ignored) {
                // Loop
            } catch (Exception e) {
                if (activo) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void detener() {
        activo = false;
        if (socketDescubrimiento != null) {
            socketDescubrimiento.close();
        }
        if (hilo != null) {
            hilo.interrupt();
        }
    }
}

