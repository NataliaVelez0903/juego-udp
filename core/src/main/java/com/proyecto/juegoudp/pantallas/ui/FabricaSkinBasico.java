package com.proyecto.juegoudp.pantallas.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Implementa una fábrica de skins básicos para la interfaz de usuario.
 *
 * Esta clase se encarga de construir un objeto {Skin} mínimo
 * con los estilos necesarios para etiquetas, botones y campos de texto
 * utilizados en los menús y salas del juego.
 *
 * Utiliza una fuente por defecto y configura estilos simples
 * para los componentes visuales básicos de la interfaz.
 *
 * Su propósito es centralizar la creación de un skin funcional
 * y reutilizable para elementos de la interfaz gráfica.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class FabricaSkinBasico implements IFabricaSkin {

    /**
     * Crea y configura un skin básico con estilos por defecto
     * para etiquetas, botones y campos de texto.
     *
     * Este método construye un nuevo objeto {@link Skin},
     * registra una fuente por defecto y asocia estilos mínimos
     * para los componentes visuales principales de la interfaz.
     *
     * @return un skin básico listo para utilizarse en la interfaz
     */
    @Override
    public Skin crearSkin() {
        Skin skin = new Skin();
        BitmapFont fuente = new BitmapFont();
        skin.add("default", fuente);

        Label.LabelStyle estiloEtiqueta = new Label.LabelStyle();
        estiloEtiqueta.font = fuente;
        skin.add("default", estiloEtiqueta);

        TextButton.TextButtonStyle estiloBoton = new TextButton.TextButtonStyle();
        estiloBoton.font = fuente;
        skin.add("default", estiloBoton);

        TextField.TextFieldStyle estiloCampo = new TextField.TextFieldStyle();
        estiloCampo.font = fuente;
        estiloCampo.fontColor = Color.WHITE;
        skin.add("default", estiloCampo);

        return skin;
    }
}
