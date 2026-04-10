package com.proyecto.juegoudp.pantallas.espera;

/**
 * Define el contrato para la gestión de la conexión en la sala de espera.
 *
 * Esta interfaz establece las operaciones básicas necesarias para controlar
 * el ciclo de vida de una sesión de espera, permitiendo iniciar y detener
 * la comunicación asociada a la sala.
 *
 * Su propósito es abstraer la lógica de conexión, facilitando la implementación
 * de distintos mecanismos de comunicación (por ejemplo, UDP u otros protocolos).
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public interface IConexionSala {

    /**
     * Inicia la sesión de espera y establece la conexión necesaria
     * para la comunicación entre los participantes.
     */
    void iniciar();

    /**
     * Detiene la sesión de espera y libera los recursos asociados
     * a la conexión.
     */
    void detener();
}
