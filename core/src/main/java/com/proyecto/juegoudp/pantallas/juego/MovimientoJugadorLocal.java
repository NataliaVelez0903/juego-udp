package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.TipoMensaje;
import com.proyecto.juegoudp.utilidades.Constantes;

/**
 * Gestiona el movimiento local del jugador y el envío periódico
 * de su posición al servidor.
 *
 * Esta clase se encarga de interpretar el estado actual de las teclas
 * de movimiento, actualizar la posición local del jugador dentro
 * del escenario y enviar dicha posición al servidor con una frecuencia
 * controlada.
 *
 * Su propósito es mantener una respuesta inmediata en el cliente
 * mientras limita la cantidad de mensajes enviados por red,
 * favoreciendo una sincronización más eficiente con el servidor.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public final class MovimientoJugadorLocal {

    /**
     * Estado local del juego que contiene la información del jugador.
     */
    private final EstadoJuego estadoLocal;

    /**
     * Cliente UDP utilizado para enviar la posición del jugador al servidor.
     */
    private final ClienteUDP cliente;

    /**
     * Arreglo que representa el estado actual de las teclas de movimiento.
     */
    private final boolean[] estadoTeclasMovimiento;

    /**
     * Velocidad de desplazamiento del jugador, expresada en píxeles por segundo.
     */
    private final float velocidadPixelesSegundo;

    /**
     * Acumulador de tiempo utilizado para controlar la frecuencia
     * de envío de actualizaciones por red.
     */
    private float acumuladorSegundosParaEnvio;

    /**
     * Construye un nuevo gestor de movimiento local del jugador.
     *
     * @param estadoLocal el estado local del juego
     * @param cliente el cliente UDP utilizado para enviar datos al servidor
     * @param estadoTeclasMovimiento arreglo con el estado de las teclas de movimiento
     * @param velocidadPixelesSegundo velocidad de movimiento del jugador en píxeles por segundo
     */
    public MovimientoJugadorLocal(
        EstadoJuego estadoLocal,
        ClienteUDP cliente,
        boolean[] estadoTeclasMovimiento,
        float velocidadPixelesSegundo
    ) {
        this.estadoLocal = estadoLocal;
        this.cliente = cliente;
        this.estadoTeclasMovimiento = estadoTeclasMovimiento;
        this.velocidadPixelesSegundo = velocidadPixelesSegundo;
    }

    /**
     * Actualiza la posición local del jugador y, si corresponde,
     * envía su nueva posición al servidor.
     *
     * Este método calcula el desplazamiento del jugador en función
     * de las teclas de movimiento activas, actualiza su posición
     * dentro de los límites permitidos del escenario y controla
     * el envío periódico de mensajes de red con la nueva ubicación.
     *
     * Si la partida ha finalizado, el identificador del jugador no es válido
     * o no existe movimiento, el método no realiza ninguna acción.
     *
     * @param deltaSegundos tiempo transcurrido desde la última actualización, en segundos
     * @param idJugador identificador del jugador local
     * @param partidaFinalizada indica si la partida ya ha finalizado
     */
    public void actualizar(float deltaSegundos, int idJugador, boolean partidaFinalizada) {
        if (partidaFinalizada || idJugador < 0) {
            return;
        }

        float deltaX = 0;
        float deltaY = 0;
        if (estadoTeclasMovimiento[0]) {
            deltaY += velocidadPixelesSegundo * deltaSegundos;
        }
        if (estadoTeclasMovimiento[1]) {
            deltaY -= velocidadPixelesSegundo * deltaSegundos;
        }
        if (estadoTeclasMovimiento[3]) {
            deltaX += velocidadPixelesSegundo * deltaSegundos;
        }
        if (estadoTeclasMovimiento[2]) {
            deltaX -= velocidadPixelesSegundo * deltaSegundos;
        }
        if (deltaX == 0 && deltaY == 0) {
            return;
        }

        Jugador yo = estadoLocal.getJugador(idJugador);
        if (yo == null) {
            return;
        }

        float nuevaX = yo.getX() + deltaX;
        float nuevaY = yo.getY() + deltaY;
        nuevaX = Math.max(20, Math.min(1004, nuevaX));
        nuevaY = Math.max(20, Math.min(748, nuevaY));
        yo.setX(nuevaX);
        yo.setY(nuevaY);

        acumuladorSegundosParaEnvio += deltaSegundos;
        float intervaloSegundos = 1f / Constantes.ENVIOS_RED_POR_SEGUNDO;
        if (acumuladorSegundosParaEnvio >= intervaloSegundos) {
            acumuladorSegundosParaEnvio = 0;
            cliente.enviarMensaje(new Mensaje(TipoMensaje.MOVER_JUGADOR, idJugador, 0, nuevaX, nuevaY, 0, 0, ""));
        }
    }
}
