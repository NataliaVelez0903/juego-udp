package com.proyecto.juegoudp.pantallas.juego;

import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.red.ClienteUDP;
import com.proyecto.juegoudp.red.Mensaje;
import com.proyecto.juegoudp.red.TipoMensaje;
import com.proyecto.juegoudp.utilidades.Constantes;

/**
 * Movimiento WASD local y envío limitado de posición al servidor.
 */
public final class MovimientoJugadorLocal {
    private final EstadoJuego estadoLocal;
    private final ClienteUDP cliente;
    private final boolean[] estadoTeclasMovimiento;
    private final float velocidadPixelesSegundo;
    private float acumuladorSegundosParaEnvio;

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
