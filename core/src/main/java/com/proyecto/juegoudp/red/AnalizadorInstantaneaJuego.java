package com.proyecto.juegoudp.red;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Convierte la cadena {@code STATE|...} del servidor en datos estructurados para la pantalla de juego.
 */
public final class AnalizadorInstantaneaJuego {
    private AnalizadorInstantaneaJuego() {}

    public static InstantaneaPartida analizar(String estadoSerializado) {
        if (estadoSerializado == null || !estadoSerializado.startsWith("STATE|")) {
            return null;
        }
        // Usamos límite 7 para capturar el nuevo segmento del árbitro al final
        String[] partes = estadoSerializado.split("\\|", 7);
        if (partes.length < 6) {
            return null;
        }

        InstantaneaPartida instantanea = new InstantaneaPartida();
        try {
            instantanea.secuencia = Long.parseLong(partes[1]);
            instantanea.jugadoresRequeridos = Integer.parseInt(partes[2]);
            instantanea.tiempoRestanteSegundos = Integer.parseInt(partes[3]);
        } catch (NumberFormatException e) {
            return null;
        }

        for (String registro : partes[4].split(";")) {
            if (registro.isEmpty()) {
                continue;
            }
            String[] campos = registro.split(",", 8);
            if (campos.length < 7) {
                continue;
            }
            DatoJugadorInstantanea jugador = new DatoJugadorInstantanea();
            jugador.id = enteroDesde(campos[0], -1);
            jugador.nombre = decodificar(campos[1]);
            jugador.x = flotanteDesde(campos[2], 0f);
            jugador.y = flotanteDesde(campos[3], 0f);
            jugador.puntaje = enteroDesde(campos[4], 0);
            jugador.avatarId = enteroDesde(campos[5], 0);
            jugador.tienePelota = enteroDesde(campos[6], 0) == 1;
            jugador.flags = campos.length >= 8 ? enteroDesde(campos[7], 0) : 0;
            instantanea.jugadores.add(jugador);
        }

        for (String registro : partes[5].split(";")) {
            if (registro.isEmpty()) {
                continue;
            }
            String[] campos = registro.split(",");
            if (campos.length < 6) {
                continue;
            }
            DatoPelotaInstantanea pelota = new DatoPelotaInstantanea();
            pelota.id = enteroDesde(campos[0], -1);
            pelota.x = flotanteDesde(campos[1], 0f);
            pelota.y = flotanteDesde(campos[2], 0f);
            pelota.vx = flotanteDesde(campos[3], 0f);
            pelota.vy = flotanteDesde(campos[4], 0f);
            pelota.idJugador = enteroDesde(campos[5], -1);
            instantanea.pelotas.add(pelota);
        }

        // --- PROCESAMIENTO DEL ÁRBITRO ---
        if (partes.length >= 7 && !partes[6].isEmpty()) {
            String[] camposArb = partes[6].split(",");
            if (camposArb.length >= 2) {
                instantanea.arbitroX = flotanteDesde(camposArb[0], 512f);
                instantanea.arbitroY = flotanteDesde(camposArb[1], 384f);
            }
        }

        return instantanea;
    }

    private static String decodificar(String valor) {
        try {
            return URLDecoder.decode(valor, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return valor;
        }
    }

    private static int enteroDesde(String valor, int defecto) {
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            return defecto;
        }
    }

    private static float flotanteDesde(String valor, float defecto) {
        try {
            return Float.parseFloat(valor);
        } catch (Exception e) {
            return defecto;
        }
    }

    /** Instantánea completa parseada del segmento {@code STATE|} (jugadores, pelotas y árbitro). */
    public static final class InstantaneaPartida {
        public long secuencia;
        public int jugadoresRequeridos;
        public int tiempoRestanteSegundos;
        public final List<DatoJugadorInstantanea> jugadores = new ArrayList<>();
        public final List<DatoPelotaInstantanea> pelotas = new ArrayList<>();
        // Nuevos campos para sincronizar la posición del árbitro
        public float arbitroX;
        public float arbitroY;
    }

    /** Un registro de jugador tal como viene serializado en el {@code STATE}. */
    public static final class DatoJugadorInstantanea {
        public int id;
        public String nombre;
        public float x;
        public float y;
        public int puntaje;
        public int avatarId;
        public boolean tienePelota;
        public int flags;
    }

    /** Un registro de pelota tal como viene serializado en el {@code STATE}. */
    public static final class DatoPelotaInstantanea {
        public int id;
        public float x;
        public float y;
        public float vx;
        public float vy;
        public int idJugador;
    }
}
