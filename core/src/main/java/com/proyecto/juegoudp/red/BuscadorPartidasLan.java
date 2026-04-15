package com.proyecto.juegoudp.red;

import com.proyecto.juegoudp.utilidades.Constantes;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Envía broadcast LAN y recoge hosts que respondan.
 */
public final class BuscadorPartidasLan {
    private static final String MENSAJE_BUSQUEDA = "DISCOVER_PELOTEROS";

    private BuscadorPartidasLan() {}

    public static List<InfoPartidaLan> buscar(int timeoutMs) {
        Map<String, InfoPartidaLan> encontradasPorIp = new LinkedHashMap<>();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setSoTimeout(200);
            byte[] datos = MENSAJE_BUSQUEDA.getBytes(StandardCharsets.UTF_8);

            // Broadcast global
            DatagramPacket broadcastGlobal = new DatagramPacket(
                datos, datos.length, InetAddress.getByName("255.255.255.255"), Constantes.PUERTO_DESCUBRIMIENTO_UDP);
            socket.send(broadcastGlobal);

            // Broadcast por interfaces (más robusto en Windows/LAN)
            for (InetAddress bcast : broadcastsLocales()) {
                DatagramPacket pkt = new DatagramPacket(datos, datos.length, bcast, Constantes.PUERTO_DESCUBRIMIENTO_UDP);
                socket.send(pkt);
            }

            long limite = System.currentTimeMillis() + Math.max(timeoutMs, 400);
            byte[] buffer = new byte[1024];
            while (System.currentTimeMillis() < limite) {
                try {
                    DatagramPacket resp = new DatagramPacket(buffer, buffer.length);
                    socket.receive(resp);
                    String texto = new String(resp.getData(), 0, resp.getLength(), StandardCharsets.UTF_8).trim();
                    if (!texto.startsWith("HOST_INFO|")) {
                        continue;
                    }
                    String[] p = texto.split("\\|", 4);
                    if (p.length < 4) {
                        continue;
                    }
                    String ip = resp.getAddress().getHostAddress();
                    String nombreHost = p[1];
                    int jugadores = parseEnteroSeguro(p[2], 2);
                    int tiempo = parseEnteroSeguro(p[3], 60);
                    encontradasPorIp.put(ip, new InfoPartidaLan(ip, nombreHost, jugadores, tiempo));
                } catch (SocketTimeoutException ignored) {
                    // seguir hasta timeout final
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
        return new ArrayList<>(encontradasPorIp.values());
    }

    private static int parseEnteroSeguro(String txt, int defecto) {
        try {
            return Integer.parseInt(txt);
        } catch (Exception e) {
            return defecto;
        }
    }

    private static Collection<InetAddress> broadcastsLocales() {
        List<InetAddress> lista = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isUp() || ni.isLoopback()) {
                    continue;
                }
                for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                    InetAddress bcast = ia.getBroadcast();
                    if (bcast != null) {
                        lista.add(bcast);
                    }
                }
            }
        } catch (Exception ignored) {
            // Fallback solo con 255.255.255.255
        }
        return lista;
    }
}

