package com.eia.felinas.gui.viz;

/**
 * Umbrales de dibujo de la seccion 2.3 del enunciado.
 *
 * Regla exacta: "The visualization is mandatory below the following
 * thresholds and optional above them (...) Above a threshold the GUI must
 * still compute and display the numeric answer, plus a visible message
 * explaining that the drawing was omitted because of the instance size."
 *
 * Es decir: la respuesta textual (que ya calculan las clases de "misiones")
 * SIEMPRE se muestra; estos metodos solo deciden si, ADEMAS, se intenta
 * dibujar. Logica pura sin JavaFX para poder testearla sin abrir ventana.
 */
public final class LimitesVisualizacion {

    private LimitesVisualizacion() {
    }

    /** Mision 1: grillas hasta 50 x 50. */
    public static boolean debeDibujarGrilla(int filas, int columnas) {
        return filas <= 50 && columnas <= 50;
    }

    /** Misiones 2 y 3 (grafo): hasta 60 nodos. */
    public static boolean debeDibujarGrafo(int nodos) {
        return nodos <= 60;
    }

    /** Mision 4: hasta 100 intersecciones y 300 cables. */
    public static boolean debeDibujarRedMision4(int intersecciones, int cables) {
        return intersecciones <= 100 && cables <= 300;
    }

    /**
     * La matriz de Floyd-Warshall (Mision 3) se muestra SIEMPRE: el
     * enunciado la exige "para todo N hasta 100", y N ya esta acotado a 100
     * por la especificacion de entrada de esa mision. No hay umbral que la
     * omita; este metodo existe para dejar la regla explicita y probada.
     */
    public static boolean debeDibujarMatriz(int n) {
        return n <= 100;
    }

    /** Mensaje exigido por la seccion 2.3 cuando el dibujo se omite. */
    public static String mensajeOmitido(String nombreInstancia) {
        return "El dibujo de " + nombreInstancia
                + " se omitio porque supera el limite de visualizacion; "
                + "la respuesta numerica de arriba es exacta e independiente del dibujo.";
    }
}
