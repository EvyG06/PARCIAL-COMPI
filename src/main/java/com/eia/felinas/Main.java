package com.eia.felinas;

import com.eia.felinas.gui.App;

/**
 * Punto de entrada del programa.
 *
 * Esta clase NO extiende Application a proposito: cuando la clase main
 * extiende Application y JavaFX no esta en el module-path, la JVM aborta con
 * "JavaFX runtime components are missing". Un lanzador plano evita ese
 * problema y permite ejecutar desde IntelliJ con un simple boton Run.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        App.main(args);
    }
}
