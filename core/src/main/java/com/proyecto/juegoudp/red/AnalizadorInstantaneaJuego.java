package com.proyecto.juegoudp.red;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Analiza la cadena de estado recibida desde el servidor
 * y la convierte en una estructura utilizable por la pantalla de juego.
 *
 * Esta clase interpreta mensajes serializados con el formato
 * STATE|..., extrayendo la información de la secuencia,
 * la cantidad de jugadores requeridos, el tiempo restante
 * y los datos correspondientes a jugadores y pelotas.
 *
 * Su propósito es transformar una representación textual
 * del estado de la partida en objetos estructurados que
 * puedan ser utilizados por el modelo local del juego.
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
public final class AnalizadorInstantaneaJuego {

    /**
     * Constructor privado para evitar la creación de instancias
     * de esta clase utilitaria.
     */
    private AnalizadorInstantaneaJuego() {}

    /**
     * Analiza una cadena de estado serializada y construye
     * una instantánea estructurada de la partida.
     *
     * Este método verifica que la cadena tenga el formato esperado,
     * extrae los datos generales de la instantánea y procesa
     * los registros de jugadores y pelotas contenidos en ella.
     *
     * Si la cadena no tiene un formato válido o no puede
     * interpretarse correctamente, retorna null.
     *
     * @param estadoSerializado cadena serializada con formato STATE|...
     * @return una instantánea estructurada de la partida,
     *         o null si la cadena no es válida
     */
    public static InstantaneaPartida analizar(String estadoSerializado) {
        if (estadoSerializado == null || !estadoSerializado.startsWith("STATE|")) {
            return null;
        }
        // Formato:
        // STATE|seq|req|tiempo|jugadores|pelotas|arbitros
        // (arbitros puede no existir en versiones antiguas)
        String[] partes = estadoSerializado.split("\\|", 8);
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

        if (partes.length >= 7) {
            for (String registro : partes[6].split(";")) {
                if (registro.isEmpty()) {
                    continue;
                }
                String[] campos = registro.split(",");
                if (campos.length < 5) {
                    continue;
                }
                DatoArbitroInstantanea arbitro = new DatoArbitroInstantanea();
                arbitro.id = enteroDesde(campos[0], -1);
                arbitro.x = flotanteDesde(campos[1], 0f);
                arbitro.y = flotanteDesde(campos[2], 0f);
                arbitro.vx = flotanteDesde(campos[3], 0f);
                arbitro.vy = flotanteDesde(campos[4], 0f);
                instantanea.arbitros.add(arbitro);
            }
        }

        return instantanea;
    }

    /**
     * Decodifica una cadena codificada en formato URL utilizando UTF-8.
     *
     * Si ocurre algún error durante la decodificación,
     * retorna el valor original sin modificar.
     *
     * @param valor cadena codificada
     * @return la cadena decodificada o el valor original si falla la operación
     */
    private static String decodificar(String valor) {
        try {
            return URLDecoder.decode(valor, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return valor;
        }
    }

    /**
     * Convierte una cadena en un número entero.
     *
     * Si la conversión falla, retorna el valor por defecto indicado.
     *
     * @param valor cadena a convertir
     * @param defecto valor por defecto en caso de error
     * @return el entero convertido o el valor por defecto
     */
    private static int enteroDesde(String valor, int defecto) {
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            return defecto;
        }
    }

    /**
     * Convierte una cadena en un número de punto flotante.
     *
     * Si la conversión falla, retorna el valor por defecto indicado.
     *
     * @param valor cadena a convertir
     * @param defecto valor por defecto en caso de error
     * @return el número flotante convertido o el valor por defecto
     */
    private static float flotanteDesde(String valor, float defecto) {
        try {
            return Float.parseFloat(valor);
        } catch (Exception e) {
            return defecto;
        }
    }

    /**
     * Representa una instantánea completa de la partida
     * obtenida a partir del mensaje de estado serializado.
     *
     * Contiene los datos generales de la instantánea,
     * así como las colecciones de jugadores y pelotas
     * reconstruidas desde la cadena de estado.
     */
    public static final class InstantaneaPartida {

        /**
         * Número de secuencia de la instantánea.
         */
        public long secuencia;

        /**
         * Cantidad de jugadores requeridos para la partida.
         */
        public int jugadoresRequeridos;

        /**
         * Tiempo restante de la partida, expresado en segundos.
         */
        public int tiempoRestanteSegundos;

        /**
         * Lista de jugadores presentes en la instantánea.
         */
        public final List<DatoJugadorInstantanea> jugadores = new ArrayList<>();

        /**
         * Lista de pelotas presentes en la instantánea.
         */
        public final List<DatoPelotaInstantanea> pelotas = new ArrayList<>();

        /**
         * Lista de árbitros presentes en la instantánea.
         */
        public final List<DatoArbitroInstantanea> arbitros = new ArrayList<>();
    }

    /**
     * Representa un registro de jugador contenido
     * en la instantánea serializada de la partida.
     */
    public static final class DatoJugadorInstantanea {

        /**
         * Identificador del jugador.
         */
        public int id;

        /**
         * Nombre del jugador.
         */
        public String nombre;

        /**
         * Posición horizontal del jugador.
         */
        public float x;

        /**
         * Posición vertical del jugador.
         */
        public float y;

        /**
         * Puntaje actual del jugador.
         */
        public int puntaje;

        /**
         * Identificador del avatar del jugador.
         */
        public int avatarId;

        /**
         * Indica si el jugador posee la pelota.
         */
        public boolean tienePelota;

        /**
         * Campo adicional de banderas o indicadores.
         */
        public int flags;
    }

    /**
     * Representa un registro de pelota contenido
     * en la instantánea serializada de la partida.
     */
    public static final class DatoPelotaInstantanea {

        /**
         * Identificador de la pelota.
         */
        public int id;

        /**
         * Posición horizontal de la pelota.
         */
        public float x;

        /**
         * Posición vertical de la pelota.
         */
        public float y;

        /**
         * Velocidad horizontal de la pelota.
         */
        public float vx;

        /**
         * Velocidad vertical de la pelota.
         */
        public float vy;

        /**
         * Identificador del jugador que controla la pelota.
         */
        public int idJugador;
    }

    /**
     * Representa un registro de árbitro contenido
     * en la instantánea serializada de la partida.
     */
    public static final class DatoArbitroInstantanea {
        public int id;
        public float x;
        public float y;
        public float vx;
        public float vy;
    }
}
