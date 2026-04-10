package com.proyecto.juegoudp.pantallas.juego;

import com.badlogic.gdx.InputProcessor;

/**
 * Define el contrato para el manejo de entrada en la pantalla de partida.
 *
 * Esta interfaz representa la abstracción del componente encargado
 * de procesar los eventos de entrada del usuario durante la partida,
 * tales como pulsaciones de teclado, clics, arrastres y demás
 * interacciones soportadas por libGDX.
 *
 * Al extender {InputProcessor}, hereda el conjunto de métodos
 * necesarios para capturar y responder a los distintos eventos
 * de entrada proporcionados por el framework.
 *
 * Su propósito es permitir que la lógica de control de entrada
 * pueda desacoplarse de implementaciones concretas.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public interface IControladorEntradaJuego extends InputProcessor {
}
