package com.eia.felinas.gui.viz;

import com.eia.felinas.modelo.Arista;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dibuja grafos de nodos y aristas (Misiones 2, 3 y 4) sobre un layout
 * circular: los nodos se ubican espaciados en un circulo, lo que evita
 * cruces artificiales de un layout aleatorio y escala razonablemente hasta
 * los limites de la seccion 2.3 (60 nodos en M2/M3, 100 en M4).
 *
 * Dos modos de resaltado, porque las misiones lo necesitan distinto:
 *   - dibujarConRuta: resalta una secuencia ordenada de nodos (un camino, o
 *     un ciclo si cerrarCiclo=true). Sirve para la ruta de Dijkstra (M2) y
 *     la ruta o el ciclo de ganancia positiva de Floyd-Warshall/Bellman-Ford
 *     (M3).
 *   - dibujarConjuntoDestacado: resalta un CONJUNTO de aristas sin orden
 *     entre si. Sirve para las aristas del arbol de expansion minima (M4),
 *     que no forman un camino.
 */
public final class VisualizadorGrafo {

    private static final double RADIO_MINIMO = 140;
    private static final double MARGEN = 30;

    private static final Color COLOR_NODO = Paleta.FONDO;
    private static final Color COLOR_BORDE_NODO = Paleta.OSCURO;
    private static final Color COLOR_ARISTA = Paleta.BORDE;
    private static final Color COLOR_INICIO = Paleta.TURQUESA;
    private static final Color COLOR_DESTINO = Paleta.LIMON_OSCURO;
    private static final Color COLOR_RUTA = Paleta.TURQUESA;
    private static final Color COLOR_CICLO = Paleta.PELIGRO;

    private VisualizadorGrafo() {
    }

    /**
     * Dibuja el grafo con una ruta (o ciclo) resaltada.
     *
     * @param camino     secuencia ordenada de nodos a resaltar; vacia si no hay nada que resaltar.
     * @param cerrarCiclo si es true, se dibuja tambien la arista que cierra camino[ultimo] -> camino[0].
     * @param inicio     nodo de inicio a marcar en verde, o null si no aplica.
     * @param destino    nodo destino a marcar en dorado, o null si no aplica.
     * @param colorRuta  color del resaltado (COLOR_RUTA para caminos optimos, COLOR_CICLO para ciclos peligrosos).
     */
    public static Region dibujarConRuta(int n, List<Arista> aristas, boolean dirigido,
                                        List<Integer> camino, boolean cerrarCiclo,
                                        Integer inicio, Integer destino, Color colorRuta) {
        if (!LimitesVisualizacion.debeDibujarGrafo(n)) {
            return mensajeOmitido("el grafo de " + n + " nodos");
        }
        double[][] posiciones = layoutCircular(n);
        Canvas lienzo = lienzoBase(posiciones);
        GraphicsContext gc = lienzo.getGraphicsContext2D();

        Set<String> aristasDeLaRuta = clavesConsecutivas(camino, cerrarCiclo);
        dibujarAristas(gc, aristas, dirigido, posiciones, aristasDeLaRuta, colorRuta);
        dibujarNodos(gc, n, posiciones, inicio, destino);
        return new Pane(lienzo);
    }

    /** Dibuja el grafo resaltando un CONJUNTO de aristas sin orden (Mision 4: MST). */
    public static Region dibujarConjuntoDestacado(int n, List<Arista> todas, List<Arista> destacadas) {
        if (!LimitesVisualizacion.debeDibujarRedMision4(n, todas.size())) {
            return mensajeOmitido("la red de " + n + " intersecciones y " + todas.size() + " cables");
        }
        double[][] posiciones = layoutCircular(n);
        Canvas lienzo = lienzoBase(posiciones);
        GraphicsContext gc = lienzo.getGraphicsContext2D();

        Set<String> clavesDestacadas = new HashSet<>();
        for (Arista a : destacadas) {
            clavesDestacadas.add(claveNoDirigida(a.desde(), a.hacia()));
        }
        // Con hasta 300 cables dibujar TODOS los candidatos satura la vista;
        // por encima de 80 aristas solo se dibujan las seleccionadas del MST.
        List<Arista> aMostrar = todas.size() <= 80 ? todas : destacadas;
        dibujarAristas(gc, aMostrar, false, posiciones, clavesDestacadas, COLOR_RUTA);
        dibujarNodos(gc, n, posiciones, null, null);
        return new Pane(lienzo);
    }

    private static Canvas lienzoBase(double[][] posiciones) {
        double maxX = 0;
        double maxY = 0;
        for (double[] p : posiciones) {
            maxX = Math.max(maxX, p[0]);
            maxY = Math.max(maxY, p[1]);
        }
        Canvas lienzo = new Canvas(maxX + MARGEN, maxY + MARGEN);
        lienzo.getGraphicsContext2D().setFill(Color.WHITE);
        lienzo.getGraphicsContext2D().fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        return lienzo;
    }

    /** Ubica los n nodos espaciados uniformemente sobre un circulo. */
    private static double[][] layoutCircular(int n) {
        double radioNodo = radioDelNodo(n);
        double radioCirculo = Math.max(RADIO_MINIMO, n * (radioNodo * 2.4) / (2 * Math.PI));
        double centro = radioCirculo + radioNodo + MARGEN;
        double[][] posiciones = new double[n][2];
        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n - Math.PI / 2;
            posiciones[i][0] = centro + radioCirculo * Math.cos(angulo);
            posiciones[i][1] = centro + radioCirculo * Math.sin(angulo);
        }
        return posiciones;
    }

    private static double radioDelNodo(int n) {
        if (n <= 15) {
            return 14;
        }
        if (n <= 40) {
            return 9;
        }
        return 5;
    }

    private static void dibujarAristas(GraphicsContext gc, List<Arista> aristas, boolean dirigido,
                                       double[][] posiciones, Set<String> resaltadas, Color colorResaltado) {
        // Primero las normales (para que las resaltadas queden encima).
        for (Arista a : aristas) {
            String clave = dirigido ? claveDirigida(a.desde(), a.hacia()) : claveNoDirigida(a.desde(), a.hacia());
            if (!resaltadas.contains(clave)) {
                dibujarUnaArista(gc, posiciones, a.desde(), a.hacia(), dirigido, COLOR_ARISTA, 1.2);
            }
        }
        for (Arista a : aristas) {
            String clave = dirigido ? claveDirigida(a.desde(), a.hacia()) : claveNoDirigida(a.desde(), a.hacia());
            if (resaltadas.contains(clave)) {
                dibujarUnaArista(gc, posiciones, a.desde(), a.hacia(), dirigido, colorResaltado, 3.0);
            }
        }
    }

    private static void dibujarUnaArista(GraphicsContext gc, double[][] posiciones, int desde, int hacia,
                                         boolean dirigido, Color color, double grosor) {
        double x1 = posiciones[desde][0];
        double y1 = posiciones[desde][1];
        double x2 = posiciones[hacia][0];
        double y2 = posiciones[hacia][1];
        gc.setStroke(color);
        gc.setLineWidth(grosor);
        gc.strokeLine(x1, y1, x2, y2);
        if (dirigido) {
            dibujarFlecha(gc, x1, y1, x2, y2, color);
        }
    }

    private static void dibujarFlecha(GraphicsContext gc, double x1, double y1, double x2, double y2, Color color) {
        double angulo = Math.atan2(y2 - y1, x2 - x1);
        // La punta se dibuja retrocedida desde el centro del nodo destino
        // para que no quede tapada por el circulo del nodo.
        double retroceso = 16;
        double px = x2 - retroceso * Math.cos(angulo);
        double py = y2 - retroceso * Math.sin(angulo);
        double tamano = 7;
        gc.setFill(color);
        double[] xs = {
                px,
                px - tamano * Math.cos(angulo - Math.PI / 6),
                px - tamano * Math.cos(angulo + Math.PI / 6)
        };
        double[] ys = {
                py,
                py - tamano * Math.sin(angulo - Math.PI / 6),
                py - tamano * Math.sin(angulo + Math.PI / 6)
        };
        gc.fillPolygon(xs, ys, 3);
    }

    private static void dibujarNodos(GraphicsContext gc, int n, double[][] posiciones, Integer inicio, Integer destino) {
        double radioNodo = radioDelNodo(n);
        boolean etiquetar = n <= 40 && radioNodo >= 8;
        for (int i = 0; i < n; i++) {
            double x = posiciones[i][0];
            double y = posiciones[i][1];
            Color relleno = COLOR_NODO;
            if (inicio != null && i == inicio) {
                relleno = COLOR_INICIO;
            } else if (destino != null && i == destino) {
                relleno = COLOR_DESTINO;
            }
            gc.setFill(relleno);
            gc.fillOval(x - radioNodo, y - radioNodo, radioNodo * 2, radioNodo * 2);
            gc.setStroke(COLOR_BORDE_NODO);
            gc.setLineWidth(1);
            gc.strokeOval(x - radioNodo, y - radioNodo, radioNodo * 2, radioNodo * 2);
            if (etiquetar) {
                gc.setFill((inicio != null && i == inicio) || (destino != null && i == destino)
                        ? Color.WHITE : COLOR_BORDE_NODO);
                gc.setFont(Font.font("System", FontWeight.BOLD, radioNodo));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);
                gc.fillText(String.valueOf(i), x, y + 1);
            }
        }
    }

    private static Set<String> clavesConsecutivas(List<Integer> camino, boolean cerrarCiclo) {
        Set<String> claves = new HashSet<>();
        for (int i = 0; i + 1 < camino.size(); i++) {
            claves.add(claveDirigida(camino.get(i), camino.get(i + 1)));
            claves.add(claveNoDirigida(camino.get(i), camino.get(i + 1)));
        }
        if (cerrarCiclo && camino.size() > 1) {
            int ultimo = camino.get(camino.size() - 1);
            int primero = camino.get(0);
            claves.add(claveDirigida(ultimo, primero));
            claves.add(claveNoDirigida(ultimo, primero));
        }
        return claves;
    }

    private static String claveDirigida(int desde, int hacia) {
        return desde + "->" + hacia;
    }

    private static String claveNoDirigida(int a, int b) {
        return Math.min(a, b) + "-" + Math.max(a, b);
    }

    private static Region mensajeOmitido(String nombreInstancia) {
        Label mensaje = new Label(LimitesVisualizacion.mensajeOmitido(nombreInstancia));
        mensaje.setWrapText(true);
        mensaje.setMaxWidth(420);
        VBox contenedor = new VBox(mensaje);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(16));
        return contenedor;
    }

    /** Colores publicos para que la GUI elija el resaltado segun el caso (ruta optima vs. ciclo). */
    public static Color colorRuta() {
        return COLOR_RUTA;
    }

    public static Color colorCiclo() {
        return COLOR_CICLO;
    }
}
