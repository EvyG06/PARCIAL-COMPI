package com.eia.felinas.gui.viz;

import com.eia.felinas.misiones.Mision1;
import com.eia.felinas.modelo.Grilla;
import com.eia.felinas.modelo.ResultadoBusqueda;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashSet;
import java.util.Set;

/**
 * Dibuja la grilla de la Mision 1 con los caminos de BFS y DFS resaltados,
 * uno al lado del otro (asi se ve directamente por que ambos exploran el
 * mismo grafo y solo difieren en la ruta encontrada, seccion 4 del
 * enunciado: sample con BFS 18 y DFS 32 movimientos).
 *
 * Respeta el limite de la seccion 2.3: por encima de 50 x 50 no se dibuja,
 * pero SIEMPRE se muestra un mensaje explicando la omision (la respuesta
 * numerica ya la puso Mision1 en el area de texto, independiente de esto).
 */
public final class VisualizadorGrilla {

    private static final double LADO_MAXIMO_PX = 460;
    private static final double CELDA_MINIMA_PX = 6;
    private static final double CELDA_MAXIMA_PX = 34;

    private static final Color COLOR_BOMBA = Color.web("#2b2730");
    private static final Color COLOR_LIBRE = Color.web("#faf7f2");
    private static final Color COLOR_BORDE = Color.web("#d8d0e0");
    private static final Color COLOR_INICIO = Color.web("#4caf50");
    private static final Color COLOR_DESTINO = Color.web("#e8a33d");
    private static final Color COLOR_RUTA_BFS = Color.web("#7c4dff");
    private static final Color COLOR_RUTA_DFS = Color.web("#ff6f61");

    private VisualizadorGrilla() {
    }

    /**
     * Construye el panel visual para un caso de la Mision 1: dos grillas
     * (BFS y DFS) si el tamano lo permite, o un mensaje de omision.
     */
    public static Region dibujar(Mision1.Caso caso) {
        Grilla grilla = caso.grilla();
        if (!LimitesVisualizacion.debeDibujarGrilla(grilla.filas(), grilla.columnas())) {
            return mensajeOmitido(grilla);
        }

        HBox contenedor = new HBox(20);
        contenedor.setAlignment(Pos.TOP_CENTER);
        contenedor.getChildren().add(
                panelDeUnaBusqueda(caso, "BFS (camino minimo)", caso.bfs(), COLOR_RUTA_BFS));
        contenedor.getChildren().add(
                panelDeUnaBusqueda(caso, "DFS (arriba, abajo, izq, der)", caso.dfs(), COLOR_RUTA_DFS));
        return contenedor;
    }

    private static VBox panelDeUnaBusqueda(Mision1.Caso caso, String titulo,
                                           ResultadoBusqueda resultado, Color colorRuta) {
        Label etiqueta = new Label(titulo);
        etiqueta.setFont(Font.font("System", FontWeight.BOLD, 13));

        Canvas lienzo = dibujarCanvas(caso, resultado, colorRuta);

        Label pie = new Label(resultado.esAlcanzable()
                ? resultado.movimientos() + " movimientos"
                : "Nina is unreachable");

        VBox panel = new VBox(6, etiqueta, lienzo, pie);
        panel.setAlignment(Pos.TOP_CENTER);
        return panel;
    }

    private static Canvas dibujarCanvas(Mision1.Caso caso, ResultadoBusqueda resultado,
                                        Color colorRuta) {
        Grilla grilla = caso.grilla();
        int filas = grilla.filas();
        int columnas = grilla.columnas();
        double celda = Math.max(CELDA_MINIMA_PX,
                Math.min(CELDA_MAXIMA_PX, LADO_MAXIMO_PX / Math.max(filas, columnas)));
        boolean dibujarLineas = celda >= 8;

        Canvas lienzo = new Canvas(columnas * celda, filas * celda);
        GraphicsContext gc = lienzo.getGraphicsContext2D();

        Set<Integer> celdasDeLaRuta = indicesDeLaRuta(resultado);

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                double x = c * celda;
                double y = f * celda;
                int indice = grilla.indice(f, c);
                gc.setFill(grilla.hayBomba(indice) ? COLOR_BOMBA : COLOR_LIBRE);
                gc.fillRect(x, y, celda, celda);
                if (dibujarLineas) {
                    gc.setStroke(COLOR_BORDE);
                    gc.setLineWidth(0.5);
                    gc.strokeRect(x, y, celda, celda);
                }
            }
        }

        // Overlay semitransparente sobre las celdas de la ruta encontrada.
        if (!celdasDeLaRuta.isEmpty()) {
            gc.setFill(colorRuta.deriveColor(0, 1, 1, 0.55));
            for (int indice : celdasDeLaRuta) {
                int f = indice / columnas;
                int c = indice % columnas;
                gc.fillRect(c * celda, f * celda, celda, celda);
            }
            // Linea que conecta los centros de la ruta: hace visible el
            // orden del recorrido incluso cuando las celdas son pequenas.
            int[] camino = resultado.camino();
            gc.setStroke(colorRuta.darker());
            gc.setLineWidth(Math.max(1.5, celda * 0.12));
            gc.beginPath();
            for (int i = 0; i < camino.length; i++) {
                int f = camino[i] / columnas;
                int c = camino[i] % columnas;
                double cx = c * celda + celda / 2;
                double cy = f * celda + celda / 2;
                if (i == 0) {
                    gc.moveTo(cx, cy);
                } else {
                    gc.lineTo(cx, cy);
                }
            }
            gc.stroke();
        }

        // Inicio y destino se marcan al final para que queden por encima
        // del overlay de ruta y de las lineas de conexion.
        marcarCelda(gc, grilla, caso.filaInicio(), caso.colInicio(), celda, COLOR_INICIO, "S");
        marcarCelda(gc, grilla, caso.filaDestino(), caso.colDestino(), celda, COLOR_DESTINO, "N");

        return lienzo;
    }

    private static void marcarCelda(GraphicsContext gc, Grilla grilla, int fila, int columna,
                                    double celda, Color color, String etiqueta) {
        double x = columna * celda;
        double y = fila * celda;
        gc.setFill(color);
        gc.fillOval(x + celda * 0.12, y + celda * 0.12, celda * 0.76, celda * 0.76);
        if (celda >= 14) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("System", FontWeight.BOLD, celda * 0.5));
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.setTextBaseline(javafx.geometry.VPos.CENTER);
            gc.fillText(etiqueta, x + celda / 2, y + celda / 2 + 1);
        }
    }

    private static Set<Integer> indicesDeLaRuta(ResultadoBusqueda resultado) {
        Set<Integer> indices = new HashSet<>();
        if (resultado.esAlcanzable()) {
            for (int indice : resultado.camino()) {
                indices.add(indice);
            }
        }
        return indices;
    }

    private static Region mensajeOmitido(Grilla grilla) {
        Label mensaje = new Label(LimitesVisualizacion.mensajeOmitido(
                "la grilla de " + grilla.filas() + " x " + grilla.columnas()));
        mensaje.setWrapText(true);
        mensaje.setMaxWidth(420);
        VBox contenedor = new VBox(mensaje);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(16));
        HBox.setHgrow(contenedor, Priority.NEVER);
        return contenedor;
    }
}
