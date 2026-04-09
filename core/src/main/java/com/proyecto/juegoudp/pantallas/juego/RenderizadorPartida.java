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

/**
 * Dibuja el campo, jugadores, pelotas, zonas de gol, el árbitro y HUD.
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
    private final Texture texturaArbitro; // NUEVA TEXTURA

    private final float[][] coloresJugador;
    private final ProveedorInterfazPartida proveedorInterfaz;

    public interface ProveedorInterfazPartida {
        int obtenerIdJugadorLocal();
        int obtenerTiempoRestanteSegundos();
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
        texturaArbitro = new Texture(Gdx.files.internal("images/arbitro.png")); // CARGA
    }

    @Override
    public void render(float deltaSegundos) {
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camara.update();

        // 1. Fondo
        loteSprites.setProjectionMatrix(camara.combined);
        loteSprites.begin();
        loteSprites.draw(texturaFondo, 0, 0, 1024, 768);
        loteSprites.end();

        // 2. Jugadores (Formas geométricas)
        dibujadorFormas.setProjectionMatrix(camara.combined);
        dibujadorFormas.begin(ShapeRenderer.ShapeType.Filled);
        for (Jugador jugador : estadoLocal.getJugadores().values()) {
            float[] color = coloresJugador[jugador.getAvatarId() % coloresJugador.length];
            dibujadorFormas.setColor(color[0], color[1], color[2], 1);
            dibujadorFormas.circle(jugador.getX(), jugador.getY(), 20);
        }
        dibujadorFormas.end();

        // 3. Entidades con texturas (Zonas, Pelotas y Árbitro)
        loteSprites.begin();
        float yZona = 768 / 2f;
        loteSprites.draw(texturaZona, 100 - 40, yZona - 40, 80, 80);
        loteSprites.draw(texturaZona, 924 - 40, yZona - 40, 80, 80);

        for (Pelota pelota : estadoLocal.getPelotas().values()) {
            loteSprites.draw(texturaPelota, pelota.getX() - 24, pelota.getY() - 24, 48, 48);
        }

        // --- DIBUJO DEL ÁRBITRO ---
        if (estadoLocal.getArbitro() != null) {
            float tamañoBase = 40f;
            float escala = 3f; // Queremos el triple
            float tamañoFinal = tamañoBase * escala; // 120
            float offset = tamañoFinal / 2f; // 60 para que esté centrado

            loteSprites.draw(texturaArbitro,
                estadoLocal.getArbitro().getX() - offset,
                estadoLocal.getArbitro().getY() - offset,
                tamañoFinal, tamañoFinal);
        }

        // 4. HUD
        renderizarHUD();
        loteSprites.end();
    }

    private void renderizarHUD() {
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
        for (Jugador jugador : estadoLocal.getJugadores().values()) {
            fuente.draw(loteSprites, jugador.getNombre() + ": " + jugador.getPuntaje(), 30, yTexto);
            yTexto -= 30;
        }
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
        texturaArbitro.dispose(); // LIBERACIÓN
    }
}
