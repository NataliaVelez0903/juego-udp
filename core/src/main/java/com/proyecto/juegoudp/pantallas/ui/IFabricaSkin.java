package com.proyecto.juegoudp.pantallas.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Define el contrato para la creación de skins de la interfaz gráfica.
 *
 * Esta interfaz establece el método necesario para construir instancias
 * de {Skin} utilizadas por los componentes de la interfaz
 * basados en Scene2D.
 *
 * Su propósito es abstraer el proceso de creación de skins,
 * permitiendo distintas implementaciones según las necesidades
 * visuales de la aplicación.
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public interface IFabricaSkin {

    /**
     * Crea una instancia de skin para la interfaz gráfica.
     *
     * @return una skin configurada para utilizarse en componentes Scene2D
     */
    Skin crearSkin();
}
