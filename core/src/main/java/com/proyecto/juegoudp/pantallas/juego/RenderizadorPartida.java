package com.proyecto.juegoudp.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.proyecto.juegoudp.modelo.EstadoJuego;
import com.proyecto.juegoudp.modelo.Jugador;
import com.proyecto.juegoudp.modelo.Pelota;
import com.proyecto.juegoudp.sonido.GestorSonidos;
import com.proyecto.juegoudp.utilidades.Constantes;
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
    private final GlyphLayout medirTexto = new GlyphLayout();
    /** Escala de fuente sobre el campo (nombres y puntos en la bolita). */
    private static final float ESCALA_ETIQUETA_JUGADOR = 0.72f;
    private static final float HUD_MARGEN = 24f;
    private static final float HUD_SEP = 26f;
    private static final float HUD_SUB_NOMBRES = 14f;
    /** Evita que líneas de equipo tapen el tiempo a la derecha. */
    private static final float HUD_ANCHO_MAX_LINEA = 500f;
    private static final float HUD_PIE_AYUDA = 78f;
    private static final float HUD_PIE_SONIDO = 50f;

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
        loteSprites.draw(texturaFondo, 0, 0, Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO);
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
        float yZona = Constantes.ALTO_MUNDO / 2f;
        loteSprites.draw(texturaZona, 100 - 40, yZona - 40, 80, 80);
        loteSprites.draw(texturaZona, 924 - 40, yZona - 40, 80, 80);

        for (Pelota pelota : estadoLocal.getPelotas().values()) {
            loteSprites.draw(texturaPelota, pelota.getX() - 24, pelota.getY() - 24, 48, 48);
        }

        float scaleXPrev = fuente.getData().scaleX;
        float scaleYPrev = fuente.getData().scaleY;
        fuente.getData().setScale(ESCALA_ETIQUETA_JUGADOR);
        for (Jugador jugador : estadoLocal.getJugadores().values()) {
            float cx = jugador.getX();
            float cy = jugador.getY();
            String nombre = jugador.getNombre();
            String pts = String.valueOf(jugador.getPuntaje());
            dibujarTextoCentradoConContorno(loteSprites, nombre, cx, cy + 36f);
            dibujarTextoCentradoConContorno(loteSprites, pts, cx, cy - 34f);
        }
        fuente.getData().setScale(scaleXPrev, scaleYPrev);

        if (proveedorInterfaz.obtenerIdJugadorLocal() < 0) {
            dibujarTextoConContorno(loteSprites, "Conectando al servidor (UDP)...", HUD_MARGEN, Constantes.ALTO_MUNDO * 0.5f);
        }

        int tiempo = proveedorInterfaz.obtenerTiempoRestanteSegundos();
        if (tiempo >= 0) {
            int minutos = tiempo / 60;
            int segundos = tiempo % 60;
            String lineaTiempo = String.format("TIEMPO: %02d:%02d", minutos, segundos);
            medirTexto.setText(fuente, lineaTiempo);
            float xTiempo = Constantes.ANCHO_MUNDO - HUD_MARGEN - medirTexto.width;
            dibujarTextoConContorno(loteSprites, lineaTiempo, xTiempo, Constantes.ALTO_MUNDO - HUD_MARGEN);
        }

        if (proveedorInterfaz.obtenerJugadoresRequeridos() >= 4) {
            float yHud = Constantes.ALTO_MUNDO - HUD_MARGEN;
            dibujarTextoConContorno(loteSprites, "PUNTAJES EQUIPOS", HUD_MARGEN, yHud);
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
            yHud -= HUD_SEP;
            dibujarTextoConContorno(loteSprites,
                    truncarParaHud("Equipo A · " + puntajeEquipoA + " pts"), HUD_MARGEN, yHud);
            yHud -= HUD_SEP;
            dibujarTextoConContorno(loteSprites, truncarParaHud(nombresEquipoA), HUD_MARGEN + HUD_SUB_NOMBRES, yHud);
            yHud -= HUD_SEP + 4;
            dibujarTextoConContorno(loteSprites,
                    truncarParaHud("Equipo B · " + puntajeEquipoB + " pts"), HUD_MARGEN, yHud);
            yHud -= HUD_SEP;
            dibujarTextoConContorno(loteSprites, truncarParaHud(nombresEquipoB), HUD_MARGEN + HUD_SUB_NOMBRES, yHud);
        }

        dibujarTextoConContorno(loteSprites, "ACCIONES: WASD mover | ratón: pelota suelta", HUD_MARGEN, HUD_PIE_AYUDA);
        if (gestorSonidos != null && gestorSonidos.isSilenciado()) {
            dibujarTextoConContorno(loteSprites, "SONIDO: silenciado (M activar)", HUD_MARGEN, HUD_PIE_SONIDO);
        } else {
            dibujarTextoConContorno(loteSprites, "SONIDO: activo (M silenciar)", HUD_MARGEN, HUD_PIE_SONIDO);
        }

        loteSprites.end();
    }

    @Override
    public void resize(int ancho, int alto) {
        // El área de juego lo fija {@link com.badlogic.gdx.utils.viewport.FitViewport} en {@link PantallaJuego}.
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

    private String truncarParaHud(String texto) {
        medirTexto.setText(fuente, texto);
        if (medirTexto.width <= HUD_ANCHO_MAX_LINEA) {
            return texto;
        }
        String sufijo = "...";
        for (int i = texto.length() - 1; i > 0; i--) {
            String intento = texto.substring(0, i) + sufijo;
            medirTexto.setText(fuente, intento);
            if (medirTexto.width <= HUD_ANCHO_MAX_LINEA) {
                return intento;
            }
        }
        return sufijo;
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

    private void dibujarTextoCentradoConContorno(SpriteBatch batch, String texto, float cx, float yBaseLinea) {
        medirTexto.setText(fuente, texto);
        float x = cx - medirTexto.width * 0.5f;
        dibujarTextoConContorno(batch, texto, x, yBaseLinea);
    }

    /**
     * Contorno oscuro + relleno claro para leer bien sobre el campo y el fondo.
     */
    private void dibujarTextoConContorno(SpriteBatch batch, String texto, float x, float yBaseLinea) {
        Color fc = fuente.getColor();
        float pr = fc.r;
        float pg = fc.g;
        float pb = fc.b;
        float pa = fc.a;
        fuente.setColor(0f, 0f, 0f, 0.92f);
        for (int ox = -2; ox <= 2; ox++) {
            for (int oy = -2; oy <= 2; oy++) {
                if (ox == 0 && oy == 0) {
                    continue;
                }
                fuente.draw(batch, texto, x + ox, yBaseLinea + oy);
            }
        }
        fuente.setColor(1f, 1f, 1f, 1f);
        fuente.draw(batch, texto, x, yBaseLinea);
        fuente.setColor(pr, pg, pb, pa);
    }
}
