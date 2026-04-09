package com.proyecto.juegoudp.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.sonido.GestorSonidos;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Dibuja el campo, jugadores, pelotas, zonas de gol y HUD (puntajes, tiempo, ayuda).
 */
public class RenderizadorPartida implements IRenderizadorPartida {
    private final OrthographicCamera camara;
    private final ShapeRenderer dibujadorFormas;
    private final SpriteBatch loteSprites;
    private final BitmapFont fuente;
    private final EstadoJuego estadoLocal;
    private final GestorSonidos gestorSonidos;

    private final Texture texturaFondo;
    private final Texture texturaPelota;
    private final Texture texturaZona;

    private final float[][] coloresJugador;
    private final ProveedorInterfazPartida proveedorInterfaz;

    /**
     * Datos de interfaz necesarios para el HUD (provenientes del gestor de estado).
     */
    public interface ProveedorInterfazPartida {
        int obtenerIdJugadorLocal();

        int obtenerTiempoRestanteSegundos();

        int obtenerJugadoresRequeridos();
    }

    public RenderizadorPartida(
            OrthographicCamera camara,
            ShapeRenderer dibujadorFormas,
            SpriteBatch loteSprites,
            BitmapFont fuente,
            EstadoJuego estadoLocal,
            GestorSonidos gestorSonidos,
            ProveedorInterfazPartida proveedorInterfaz
    ) {
        this.camara = camara;
        this.dibujadorFormas = dibujadorFormas;
        this.loteSprites = loteSprites;
        this.fuente = fuente;
        this.estadoLocal = estadoLocal;
        this.gestorSonidos = gestorSonidos;
        this.proveedorInterfaz = proveedorInterfaz;
        this.coloresJugador = new float[][]{{1, 0, 0}, {0, 0, 1}, {0, 1, 0}, {1, 1, 0}, {1, 0, 1}, {0, 1, 1}};

        texturaFondo = new Texture(Gdx.files.internal("images/fondo.jpg"));
        texturaPelota = new Texture(Gdx.files.internal("images/ficha.png"));
        texturaZona = new Texture(Gdx.files.internal("images/zonapuntos.jpg"));
    }

    @Override
    public void render(float deltaSegundos) {
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camara.update();

        loteSprites.setProjectionMatrix(camara.combined);
        loteSprites.begin();
        loteSprites.draw(texturaFondo, 0, 0, 1024, 768);
        loteSprites.end();

        dibujadorFormas.setProjectionMatrix(camara.combined);
        dibujadorFormas.begin(ShapeRenderer.ShapeType.Filled);
        for (Jugador jugador : estadoLocal.getJugadores().values()) {
            float[] color = coloresJugador[jugador.getAvatarId() % coloresJugador.length];
            dibujadorFormas.setColor(color[0], color[1], color[2], 1);
            dibujadorFormas.circle(jugador.getX(), jugador.getY(), 20);
        }
        dibujadorFormas.end();

        loteSprites.begin();
        float yZona = 768 / 2f;
        loteSprites.draw(texturaZona, 100 - 40, yZona - 40, 80, 80);
        loteSprites.draw(texturaZona, 924 - 40, yZona - 40, 80, 80);

        for (Pelota pelota : estadoLocal.getPelotas().values()) {
            loteSprites.draw(texturaPelota, pelota.getX() - 24, pelota.getY() - 24, 48, 48);
        }

        if (proveedorInterfaz.obtenerIdJugadorLocal() < 0) {
            fuente.draw(loteSprites, "Conectando al servidor (UDP)...", 20, 400);
        }
        fuente.draw(loteSprites, "ACCIONES: WASD mover | ratón: pelota suelta", 20, 80);

        if (gestorSonidos != null && gestorSonidos.isSilenciado()) {
            fuente.draw(loteSprites, "SONIDO: silenciado (M activar)", 20, 50);
        } else {
            fuente.draw(loteSprites, "SONIDO: activo (M silenciar)", 20, 50);
        }

        fuente.draw(loteSprites, "PUNTAJES:", 20, 740);
        int tiempo = proveedorInterfaz.obtenerTiempoRestanteSegundos();
        if (tiempo >= 0) {
            int minutos = tiempo / 60;
            int segundos = tiempo % 60;
            fuente.draw(loteSprites, String.format("TIEMPO: %02d:%02d", minutos, segundos), 820, 740);
        }

        int yTexto = 710;
        if (proveedorInterfaz.obtenerJugadoresRequeridos() >= 4) {
            int puntajeEquipoA = 0;
            int puntajeEquipoB = 0;
            List<Jugador> jugadores = new ArrayList<>(estadoLocal.getJugadores().values());
            jugadores.sort(Comparator.comparingInt(Jugador::getId));
            for (Jugador jugador : jugadores) {
                if (esEquipoA(jugador.getId())) {
                    puntajeEquipoA += jugador.getPuntaje();
                } else {
                    puntajeEquipoB += jugador.getPuntaje();
                }
            }
            String nombresEquipoA = nombresEquipo(jugadores, true);
            String nombresEquipoB = nombresEquipo(jugadores, false);
            fuente.draw(loteSprites, "Equipo A (" + nombresEquipoA + "): " + puntajeEquipoA, 30, yTexto);
            yTexto -= 30;
            fuente.draw(loteSprites, "Equipo B (" + nombresEquipoB + "): " + puntajeEquipoB, 30, yTexto);
            yTexto -= 35;
        }
        for (Jugador jugador : estadoLocal.getJugadores().values()) {
            fuente.draw(loteSprites, jugador.getNombre() + ": " + jugador.getPuntaje(), 30, yTexto);
            yTexto -= 30;
        }

        loteSprites.end();
    }

    @Override
    public void resize(int ancho, int alto) {
        camara.viewportWidth = ancho;
        camara.viewportHeight = alto;
        camara.update();
    }

    @Override
    public void dispose() {
        texturaFondo.dispose();
        texturaPelota.dispose();
        texturaZona.dispose();
    }

    private boolean esEquipoA(int idJugador) {
        return idJugador % 2 != 0;
    }

    private String nombresEquipo(List<Jugador> jugadores, boolean equipoA) {
        StringBuilder nombres = new StringBuilder();
        for (Jugador jugador : jugadores) {
            if (esEquipoA(jugador.getId()) != equipoA) {
                continue;
            }
            if (nombres.length() > 0) {
                nombres.append(" / ");
            }
            nombres.append(jugador.getNombre());
        }
        return nombres.length() == 0 ? "-" : nombres.toString();
    }
}
