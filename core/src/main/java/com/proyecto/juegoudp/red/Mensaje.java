package com.proyecto.juegoudp.red;

import java.util.Locale;

/**
 * Representa un mensaje intercambiado a través del protocolo UDP.
 *
 * Esta clase encapsula la información necesaria para la comunicación
 * entre cliente y servidor, incluyendo el tipo de mensaje,
 * identificadores asociados, coordenadas, velocidades y una cadena
 * adicional de datos opcional.
 *
 * También proporciona métodos para convertir el mensaje a una
 * representación textual y reconstruirlo nuevamente a partir
 * de dicha representación.
 *
 * Su propósito es servir como unidad básica de intercambio
 * de información dentro del sistema de red del juego.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class Mensaje {

    /**
     * Tipo del mensaje enviado o recibido.
     */
    private TipoMensaje tipo;

    /**
     * Identificador del jugador asociado al mensaje.
     */
    private int idJugador;

    /**
     * Identificador del objeto asociado al mensaje.
     */
    private int idObjeto;

    /**
     * Coordenada horizontal asociada al mensaje.
     */
    private float x;

    /**
     * Coordenada vertical asociada al mensaje.
     */
    private float y;

    /**
     * Velocidad horizontal asociada al mensaje.
     */
    private float vx;

    /**
     * Velocidad vertical asociada al mensaje.
     */
    private float vy;

    /**
     * Cadena de datos adicional asociada al mensaje.
     */
    private String datos;

    /**
     * Construye un nuevo mensaje UDP con todos sus campos.
     *
     * @param tipo tipo del mensaje
     * @param idJugador identificador del jugador asociado
     * @param idObjeto identificador del objeto asociado
     * @param x coordenada horizontal
     * @param y coordenada vertical
     * @param vx velocidad horizontal
     * @param vy velocidad vertical
     * @param datos cadena de datos adicional
     */
    public Mensaje(TipoMensaje tipo, int idJugador, int idObjeto, float x, float y, float vx, float vy, String datos) {
        this.tipo = tipo;
        this.idJugador = idJugador;
        this.idObjeto = idObjeto;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.datos = datos;
    }

    /**
     * Convierte el mensaje actual en una representación textual serializada.
     *
     * El formato generado contiene el tipo de mensaje, los identificadores,
     * las coordenadas, las velocidades y la cadena de datos adicional,
     * separados por el carácter |.
     *
     * @return una cadena serializada que representa el mensaje
     */
    public String serializar() {
        return String.format(Locale.US, "%s|%d|%d|%.2f|%.2f|%.2f|%.2f|%s",
            tipo.name(), idJugador, idObjeto, x, y, vx, vy, datos == null ? "" : datos);
    }

    /**
     * Reconstruye un mensaje a partir de una cadena serializada.
     *
     * Este método interpreta el formato textual esperado y crea
     * una nueva instancia de Mensaje con los valores extraídos.
     *
     * Si la cadena no tiene el formato adecuado o ocurre un error
     * durante la conversión, retorna null.
     *
     * @param linea cadena serializada que representa un mensaje
     * @return el mensaje reconstruido, o null si la cadena no es válida
     */
    public static Mensaje deserializar(String linea) {
        String[] p = linea.split("\\|", 8);
        if (p.length < 8) return null;
        try {
            TipoMensaje t = TipoMensaje.valueOf(p[0]);
            int idJ = Integer.parseInt(p[1]);
            int idO = Integer.parseInt(p[2]);
            float x = Float.parseFloat(p[3]);
            float y = Float.parseFloat(p[4]);
            float vx = Float.parseFloat(p[5]);
            float vy = Float.parseFloat(p[6]);
            String d = p[7];
            return new Mensaje(t, idJ, idO, x, y, vx, vy, d);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obtiene el tipo del mensaje.
     *
     * @return el tipo del mensaje
     */
    public TipoMensaje getTipo() {
        return tipo;
    }

    /**
     * Obtiene el identificador del jugador asociado al mensaje.
     *
     * @return el identificador del jugador
     */
    public int getIdJugador() {
        return idJugador;
    }

    /**
     * Obtiene el identificador del objeto asociado al mensaje.
     *
     * @return el identificador del objeto
     */
    public int getIdObjeto() {
        return idObjeto;
    }

    /**
     * Obtiene la coordenada horizontal asociada al mensaje.
     *
     * @return la coordenada horizontal
     */
    public float getX() {
        return x;
    }

    /**
     * Obtiene la coordenada vertical asociada al mensaje.
     *
     * @return la coordenada vertical
     */
    public float getY() {
        return y;
    }

    /**
     * Obtiene la velocidad horizontal asociada al mensaje.
     *
     * @return la velocidad horizontal
     */
    public float getVx() {
        return vx;
    }

    /**
     * Obtiene la velocidad vertical asociada al mensaje.
     *
     * @return la velocidad vertical
     */
    public float getVy() {
        return vy;
    }

    /**
     * Obtiene la cadena de datos adicional asociada al mensaje.
     *
     * @return la cadena de datos adicional
     */
    public String getDatos() {
        return datos;
    }
}
