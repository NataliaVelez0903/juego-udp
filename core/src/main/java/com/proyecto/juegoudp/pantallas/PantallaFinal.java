package com.proyecto.juegoudp.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.proyecto.juegoudp.JuegoPrincipal;
import com.proyecto.juegoudp.utilidades.Constantes;
import com.proyecto.juegoudp.utilidades.UtilidadesPantalla;
import com.proyecto.juegoudp.modelo.Jugador;
import java.util.List;

/**
 * Representa la pantalla final de la partida.
 *
 * Esta clase se encarga de mostrar el resultado final del juego,
 * incluyendo el nombre del ganador, su puntaje, el tiempo total
 * de la partida y el ranking de jugadores ordenado por puntaje.
 *
 * Además, ofrece un botón que permite regresar al menú principal
 * una vez finalizada la partida.
 *
 * Implementa la interfaz {@link Screen} de libGDX para integrarse
 * en el ciclo de vida de pantallas del juego.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class PantallaFinal implements Screen {

    /**
     * Referencia al juego principal.
     */
    private final JuegoPrincipal juego;

    /**
     * Escenario utilizado para gestionar y dibujar los componentes
     * de la interfaz gráfica.
     */
    private final Stage stage;

    /**
     * Skin utilizada para dar estilo a los componentes visuales
     * de la pantalla.
     */
    private final Skin skin;

    /**
     * Construye una nueva pantalla final con la información de cierre
     * de la partida.
     *
     * Este constructor crea los componentes visuales necesarios para
     * mostrar el ganador, el tiempo total, el ranking de jugadores
     * y el botón para volver al menú principal.
     *
     * @param juego referencia al juego principal
     * @param ganador nombre del jugador o equipo ganador
     * @param puntajeGanador puntaje del ganador
     * @param ranking lista de jugadores ordenados por puntaje
     * @param tiempoTotalSegundos duración total de la partida, en segundos
     */
    public PantallaFinal(JuegoPrincipal juego, String ganador, int puntajeGanador, List<Jugador> ranking, int tiempoTotalSegundos) {
        this.juego = juego;
        this.stage = new Stage(new FitViewport(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO));
        this.skin = crearSkinBasico();
        Gdx.input.setInputProcessor(stage);

        Label titulo = new Label("FIN DE PARTIDA", skin);
        titulo.setPosition(Constantes.ANCHO_MUNDO * 0.5f - titulo.getPrefWidth() * 0.5f, 650);
        stage.addActor(titulo);

        Label lblGanador = new Label("Ganador: " + ganador + " (" + puntajeGanador + " pts)", skin);
        lblGanador.setWrap(true);
        lblGanador.setWidth(720);
        lblGanador.setAlignment(com.badlogic.gdx.utils.Align.center);
        lblGanador.setPosition(Constantes.ANCHO_MUNDO * 0.5f - lblGanador.getWidth() * 0.5f, 520);
        stage.addActor(lblGanador);

        int min = Math.max(0, tiempoTotalSegundos) / 60;
        int seg = Math.max(0, tiempoTotalSegundos) % 60;
        Label lblTiempo = new Label(String.format("Tiempo total: %02d:%02d", min, seg), skin);
        lblTiempo.setPosition(Constantes.ANCHO_MUNDO * 0.5f - lblTiempo.getPrefWidth() * 0.5f, 472);
        stage.addActor(lblTiempo);

        int y = 430;
        int pos = 1;
        for (Jugador j : ranking) {
            Label linea = new Label(pos + ". " + j.getNombre() + " - " + j.getPuntaje() + " pts", skin);
            linea.pack();
            linea.setPosition(Constantes.ANCHO_MUNDO * 0.5f - linea.getWidth() * 0.5f, y);
            stage.addActor(linea);
            y -= 35;
            pos++;
            if (pos > 6) break;
        }

        TextButton btnMenu = new TextButton("VOLVER AL MENU PRINCIPAL", skin);
        btnMenu.setSize(280, 50);
        btnMenu.setPosition(Constantes.ANCHO_MUNDO * 0.5f - btnMenu.getWidth() * 0.5f, 120);
        btnMenu.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                juego.volverAlMenu();
            }
        });
        stage.addActor(btnMenu);
    }

    /**
     * Crea una skin básica para los componentes visuales de esta pantalla.
     *
     * Esta skin incluye una fuente por defecto y estilos simples
     * para etiquetas y botones.
     *
     * @return una skin básica lista para utilizarse en la interfaz
     */
    private Skin crearSkinBasico() {
        Skin skinBasico = new Skin();
        BitmapFont font = new BitmapFont();
        skinBasico.add("default", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skinBasico.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        skinBasico.add("default", buttonStyle);

        return skinBasico;
    }

    /**
     * Renderiza la pantalla final en cada fotograma.
     *
     * Este método limpia el fondo de la pantalla, actualiza el escenario
     * y dibuja todos los componentes visuales.
     *
     * @param delta tiempo transcurrido desde el último fotograma
     */
    @Override
    public void render(float delta) {
        UtilidadesPantalla.limpiarFondoCompletoYViewport(stage.getViewport(), 0.08f, 0.08f, 0.15f);
        stage.act(delta);
        stage.draw();
    }

    /**
     * Ajusta el viewport del escenario cuando cambia el tamaño de la ventana.
     *
     * @param width nuevo ancho de la ventana
     * @param height nuevo alto de la ventana
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Se ejecuta cuando la pantalla pasa a estar visible.
     *
     * Actualiza el viewport del escenario con las dimensiones actuales
     * de la ventana.
     */
    @Override
    public void show() {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    /**
     * Se ejecuta cuando la aplicación entra en pausa.
     */
    @Override
    public void pause() {}

    /**
     * Se ejecuta cuando la aplicación se reanuda después de una pausa.
     */
    @Override
    public void resume() {}

    /**
     * Se ejecuta cuando esta pantalla deja de estar visible.
     */
    @Override
    public void hide() {}

    /**
     * Libera los recursos utilizados por la pantalla final.
     *
     * Este método elimina el escenario y la skin utilizados
     * durante la ejecución de esta pantalla.
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
