package com.eia.felinas.gui.viz;

import javafx.application.Application;

/**
 * Lanzador plano para DemoVisualizacion (mismo motivo que Main.java): si la
 * clase que extiende Application es tambien la clase invocada directamente
 * por "java -cp ...", la JVM aborta con "JavaFX runtime components are
 * missing" aunque los jars de JavaFX si esten en el classpath. Separar el
 * punto de entrada evita el problema.
 */
public final class DemoVisualizacionLauncher {

    private DemoVisualizacionLauncher() {
    }

    public static void main(String[] args) {
        Application.launch(DemoVisualizacion.class, args);
    }
}
