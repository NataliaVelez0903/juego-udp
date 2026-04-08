package com.proyecto.juegoudp.pantallas.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * {@link Skin} mínimo con fuente por defecto (menús y salas).
 */
public class FabricaSkinBasico implements IFabricaSkin {
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
