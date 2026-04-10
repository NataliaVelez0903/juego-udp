package com.proyecto.juegoudp.pantallas.juego;

/**
 * Define el contrato para el renderizado de la partida.
 *
 * Esta interfaz abstrae la lógica de renderizado de la pantalla de juego,
 * permitiendo desacoplar la vista de la implementación concreta basada
 * en libGDX u otros mecanismos de representación.
 *
 * Su propósito es facilitar la sustitución, extensión o prueba del
 * componente de renderizado sin afectar el resto del sistema.
 *
 * Establece los métodos básicos del ciclo de vida de renderizado,
 * incluyendo la actualización por frame, la adaptación a cambios
 * de tamaño de la ventana y la liberación de recursos.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public interface IRenderizadorPartida {

    /**
     * Realiza el renderizado de la escena de la partida.
     *
     * Este método es invocado en cada frame y permite actualizar
     * la representación visual del juego en función del tiempo transcurrido.
     *
     * @param delta tiempo transcurrido desde el último frame, en segundos
     */
    void render(float delta);

    /**
     * Ajusta el renderizado ante cambios en el tamaño de la ventana.
     *
     * @param width nuevo ancho de la ventana
     * @param height nuevo alto de la ventana
     */
    void resize(int width, int height);

    /**
     * Libera los recursos utilizados por el renderizador.
     *
     * Este método debe encargarse de limpiar cualquier recurso gráfico
     * o de memoria utilizado durante la ejecución de la partida.
     */
    void dispose();
}
