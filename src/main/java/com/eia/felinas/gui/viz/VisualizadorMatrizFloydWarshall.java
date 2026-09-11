package com.eia.felinas.gui.viz;

import com.eia.felinas.misiones.Mision3;
import com.eia.felinas.modelo.ResultadoFloydWarshall;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Muestra la matriz N x N de Floyd-Warshall de la Mision 3 en un panel
 * scrollable, tal como exige la seccion 5.1: "-" para pares sin ruta, "inf"
 * para pares no acotados, y el numero en el resto. La celda (S, D) —la
 * respuesta del caso— se resalta.
 *
 * A diferencia de la grilla de la Mision 1, esta tabla NO tiene umbral que
 * la omita: el enunciado (seccion 2.3) exige mostrarla "para todo N hasta
 * 100", y N ya esta acotado a 100 por la especificacion de entrada de la
 * Mision 3 (LimitesVisualizacion.debeDibujarMatriz documenta esta regla).
 */
public final class VisualizadorMatrizFloydWarshall {

    private static final double ANCHO_CELDA = 44;
    private static final double ALTO_VISIBLE = 420;

    private static final Color COLOR_ENCABEZADO = Color.web("#e0f7fa");
    private static final Color COLOR_DIAGONAL = Color.web("#f5fdf9");
    private static final Color COLOR_SIN_RUTA_TEXTO = Color.web("#8aa8a5");
    private static final Color COLOR_INFINITO_TEXTO = Paleta.PELIGRO;
    private static final Color COLOR_RESPUESTA_FONDO = Paleta.LIMON;

    private VisualizadorMatrizFloydWarshall() {
    }

    /** Construye el panel completo: titulo, aviso de cross-check y la matriz. */
    public static Region dibujar(Mision3.Caso caso) {
        VBox contenedor = new VBox(8);
        contenedor.setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("Matriz de Floyd-Warshall (maximo churun entre pares)");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 13));
        contenedor.getChildren().add(titulo);

        if (!caso.crossCheckCoincide()) {
            Label advertencia = new Label(
                    "ADVERTENCIA: Floyd-Warshall y Bellman-Ford no coinciden para (S="
                    + caso.origen() + ", D=" + caso.destino() + "). Revisar la implementacion.");
            advertencia.setTextFill(COLOR_INFINITO_TEXTO);
            advertencia.setWrapText(true);
            advertencia.setMaxWidth(480);
            contenedor.getChildren().add(advertencia);
        }

        ScrollPane scroll = new ScrollPane(construirTabla(caso.floydWarshall(), caso.origen(), caso.destino()));
        scroll.setFitToHeight(false);
        scroll.setPrefViewportHeight(Math.min(ALTO_VISIBLE, (caso.n() + 1) * ANCHO_CELDA));
        scroll.setPrefViewportWidth(Math.min(ALTO_VISIBLE + 100, (caso.n() + 1) * ANCHO_CELDA));
        VBox.setVgrow(scroll, Priority.ALWAYS);
        contenedor.getChildren().add(scroll);

        return contenedor;
    }

    private static GridPane construirTabla(ResultadoFloydWarshall fw, int origen, int destino) {
        GridPane tabla = new GridPane();
        int n = fw.n();

        // Esquina superior izquierda vacia + encabezados de columna (destino j).
        tabla.add(celda("", COLOR_ENCABEZADO, null, false), 0, 0);
        for (int j = 0; j < n; j++) {
            tabla.add(celda(String.valueOf(j), COLOR_ENCABEZADO, null, j == destino), j + 1, 0);
        }

        for (int i = 0; i < n; i++) {
            // Encabezado de fila (origen i).
            tabla.add(celda(String.valueOf(i), COLOR_ENCABEZADO, null, i == origen), 0, i + 1);
            for (int j = 0; j < n; j++) {
                String texto = fw.textoCelda(i, j);
                Color colorTexto = colorDelTexto(fw, i, j);
                Color fondo = fondoDeLaCelda(i, j, origen, destino);
                tabla.add(celda(texto, fondo, colorTexto, false), j + 1, i + 1);
            }
        }
        return tabla;
    }

    private static Color colorDelTexto(ResultadoFloydWarshall fw, int i, int j) {
        if (fw.esNoAcotado(i, j)) {
            return COLOR_INFINITO_TEXTO;
        }
        if (!fw.hayRuta(i, j)) {
            return COLOR_SIN_RUTA_TEXTO;
        }
        return Paleta.OSCURO;
    }

    private static Color fondoDeLaCelda(int i, int j, int origen, int destino) {
        if (i == origen && j == destino) {
            return COLOR_RESPUESTA_FONDO; // La respuesta del caso: fila S, columna D.
        }
        if (i == j) {
            return COLOR_DIAGONAL;
        }
        return Color.WHITE;
    }

    private static Label celda(String texto, Color fondo, Color colorTexto, boolean destacada) {
        Label etiqueta = new Label(texto);
        etiqueta.setMinWidth(ANCHO_CELDA);
        etiqueta.setPrefWidth(ANCHO_CELDA);
        etiqueta.setAlignment(Pos.CENTER);
        etiqueta.setPadding(new Insets(4));
        etiqueta.setFont(Font.font("System", destacada ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        if (colorTexto != null) {
            etiqueta.setTextFill(colorTexto);
        }
        String colorCss = String.format("#%02x%02x%02x",
                (int) (fondo.getRed() * 255), (int) (fondo.getGreen() * 255), (int) (fondo.getBlue() * 255));
        etiqueta.setStyle("-fx-background-color: " + colorCss
                + "; -fx-border-color: #d8d0e0; -fx-border-width: 0.5;");
        return etiqueta;
    }
}
