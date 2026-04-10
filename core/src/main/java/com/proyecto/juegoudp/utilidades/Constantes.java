package com.proyecto.juegoudp.utilidades;

/**
 * Reúne las constantes generales utilizadas en la configuración del juego y de la red.
 *
 * Esta clase centraliza valores fijos relacionados con la cantidad máxima de jugadores,
 * el puerto de comunicación UDP, las dimensiones del mundo lógico y la frecuencia
 * de envío de datos por red.
 *
 * Al declarar la clase como final y definir un constructor privado, se evita
 * su instanciación y herencia, ya que su único propósito es servir como contenedor
 * de constantes.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public final class Constantes {

    /**
     * Evita la creación de instancias de esta clase utilitaria.
     */
    private Constantes() {}

    /**
     * Cantidad máxima de jugadores permitidos en la partida.
     */
    public static final int MAX_JUGADORES = 4;

    /**
     * Puerto utilizado para la comunicación UDP del juego.
     */
    public static final int PUERTO_UDP = 5000;

    /**
     * Ancho del mundo lógico bidimensional del juego.
     *
     * Todas las pantallas y elementos visuales se escalan tomando este valor
     * como referencia horizontal.
     */
    public static final int ANCHO_MUNDO = 1024;

    /**
     * Alto del mundo lógico bidimensional del juego.
     *
     * Todas las pantallas y elementos visuales se escalan tomando este valor
     * como referencia vertical.
     */
    public static final int ALTO_MUNDO = 768;

    /**
     * Cantidad de envíos de posición por segundo realizados por la red.
     *
     * Este valor ayuda a limitar la frecuencia de actualización para evitar
     * saturación en la comunicación UDP.
     */
    public static final float ENVIOS_RED_POR_SEGUNDO = 18f;
}
