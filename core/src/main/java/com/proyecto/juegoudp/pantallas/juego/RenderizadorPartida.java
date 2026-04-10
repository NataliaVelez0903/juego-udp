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
 * Implementa el renderizado visual de la partida.
 *
 * Esta clase se encarga de dibujar los elementos gráficos principales
 * del juego, incluyendo el fondo, los jugadores, las pelotas,
 * las zonas de gol y la interfaz de usuario.
 *
 * También muestra información relevante durante la partida,
 * como el tiempo restante, los puntajes individuales o por equipos,
 * y mensajes de ayuda para el jugador.
 *
 * Utiliza herramientas de libGDX para combinar renderizado
 * de texturas, formas y texto dentro del escenario de juego.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class RenderizadorPartida implements IRenderizadorPartida {

    /**
     * Cámara ortográfica utilizada para visualizar el mundo del juego.
     */
    private final OrthographicCamera camara;

    /**
     * Renderizador de formas utilizado para dibujar elementos simples,
     * como la representación circular de los jugadores.
     */
    private final ShapeRenderer dibujadorFormas;

    /**
     * Lote de sprites utilizado para dibujar texturas e imágenes.
     */
    private final SpriteBatch loteSprites;

    /**
     * Fuente utilizada para mostrar texto dentro de la partida.
     */
    private final BitmapFont fuente;

    /**
     * Estado local del juego, desde donde se obtienen jugadores,
     * pelotas y demás datos visibles en pantalla.
     */
    private final EstadoJuego estadoLocal;

    /**
     * Gestor de sonidos utilizado para consultar el estado del audio.
     */
    private final GestorSonidos gestorSonidos;

    /**
     * Textura utilizada como fondo del campo de juego.
     */
    private final Texture texturaFondo;

    /**
     * Textura utilizada para representar la pelota.
     */
    private final Texture texturaPelota;

    /**
     * Textura utilizada para representar las zonas de gol.
     */
    private final Texture texturaZona;

    /**
     * Arreglo de colores utilizado para diferenciar visualmente
     * a los jugadores según su avatar.
     */
    private final float[][] coloresJugador;

    /**
     * Proveedor de información de interfaz utilizada para el HUD.
     */
    private final ProveedorInterfazPartida proveedorInterfaz;

    /**
     * Utilidad para medir dimensiones del texto antes de dibujarlo.
     */
    private final GlyphLayout medirTexto = new GlyphLayout();

    /**
     * Escala aplicada al texto mostrado sobre los jugadores.
     */
    private static final float ESCALA_ETIQUETA_JUGADOR = 0.72f;

    /**
     * Margen general utilizado en los elementos del HUD.
     */
    private static final float HUD_MARGEN = 24f;

    /**
     * Separación vertical entre líneas del HUD.
     */
    private static final float HUD_SEP = 26f;

    /**
     * Sangría utilizada para mostrar nombres de jugadores debajo
     * de los encabezados de equipo.
     */
    private static final float HUD_SUB_NOMBRES = 14f;

    /**
     * Ancho máximo permitido para una línea del HUD.
     */
    private static final float HUD_ANCHO_MAX_LINEA = 500f;

    /**
     * Posición vertical del texto de ayuda mostrado en la parte inferior.
     */
    private static final float HUD_PIE_AYUDA = 78f;

    /**
     * Posición vertical del texto que indica el estado del sonido.
     */
    private static final float HUD_PIE_SONIDO = 50f;

    /**
     * Define los datos de interfaz necesarios para dibujar
     * la información de la partida en pantalla.
     */
    public interface ProveedorInterfazPartida {

        /**
         * Obtiene el identificador del jugador local.
         *
         * @return el identificador del jugador actual
         */
        int obtenerIdJugadorLocal();

        /**
         * Obtiene el tiempo restante de la partida.
         *
         * @return el tiempo restante en segundos
         */
        int obtenerTiempoRestanteSegundos();

        /**
         * Obtiene la cantidad de jugadores requerida para la partida.
         *
         * @return el número de jugadores requeridos
         */
        int obtenerJugadoresRequeridos();
    }

    /**
     * Construye un nuevo renderizador de la partida.
     *
     * @param camara cámara utilizada para visualizar el mundo
     * @param dibujadorFormas renderizador de formas
     * @param loteSprites lote de sprites para texturas
     * @param fuente fuente para dibujar texto
     * @param estadoLocal estado local del juego
     * @param gestorSonidos gestor de sonidos del juego
     * @param proveedorInterfaz proveedor de datos para la interfaz
     */
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

    /**
     * Renderiza todos los elementos visuales de la partida.
     *
     * Este método limpia la pantalla, actualiza la cámara y dibuja
     * el fondo, los jugadores, las pelotas, las zonas de gol
     * y el HUD con la información actual del juego.
     *
     * @param deltaSegundos tiempo transcurrido desde el último fotograma
     */
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

    /**
     * Ajusta el renderizador ante cambios en el tamaño de la ventana.
     *
     * @param ancho nuevo ancho de la ventana
     * @param alto nuevo alto de la ventana
     */
    @Override
    public void resize(int ancho, int alto) {
        // El área de juego lo fija {@link com.badlogic.gdx.utils.viewport.FitViewport} en {@link PantallaJuego}.
    }

    /**
     * Libera los recursos gráficos utilizados por el renderizador.
     */
    @Override
    public void dispose() {
        texturaFondo.dispose();
        texturaPelota.dispose();
        texturaZona.dispose();
    }

    /**
     * Determina si un jugador pertenece al equipo A.
     *
     * @param idJugador identificador del jugador
     * @return true si el jugador pertenece al equipo A; false en caso contrario
     */
    private boolean esEquipoA(int idJugador) {
        return idJugador % 2 != 0;
    }

    /**
     * Ajusta un texto para que no supere el ancho máximo permitido
     * dentro del HUD.
     *
     * Si el texto excede el límite, se recorta y se le agrega
     * un sufijo de puntos suspensivos.
     *
     * @param texto texto original
     * @return el texto ajustado al ancho máximo permitido
     */
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

    /**
     * Construye una cadena con los nombres de los jugadores
     * pertenecientes a un equipo.
     *
     * @param jugadores lista de jugadores disponibles
     * @param equipoA true para obtener nombres del equipo A;
     *                false para obtener nombres del equipo B
     * @return una cadena con los nombres de los jugadores del equipo
     */
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

    /**
     * Dibuja un texto centrado horizontalmente respecto a una coordenada dada,
     * aplicando contorno para mejorar la legibilidad.
     *
     * @param batch lote de sprites utilizado para dibujar
     * @param texto texto a renderizar
     * @param cx coordenada horizontal central
     * @param yBaseLinea coordenada vertical base del texto
     */
    private void dibujarTextoCentradoConContorno(SpriteBatch batch, String texto, float cx, float yBaseLinea) {
        medirTexto.setText(fuente, texto);
        float x = cx - medirTexto.width * 0.5f;
        dibujarTextoConContorno(batch, texto, x, yBaseLinea);
    }

    /**
     * Dibuja un texto con contorno oscuro y relleno claro
     * para mejorar su visibilidad sobre el fondo del juego.
     *
     * @param batch lote de sprites utilizado para dibujar
     * @param texto texto a renderizar
     * @param x coordenada horizontal de inicio
     * @param yBaseLinea coordenada vertical base del texto
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
