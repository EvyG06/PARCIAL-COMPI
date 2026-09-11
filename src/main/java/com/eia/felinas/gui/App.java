package com.eia.felinas.gui;

import com.eia.felinas.gui.viz.VisualizadorGrafo;
import com.eia.felinas.gui.viz.VisualizadorGrilla;
import com.eia.felinas.gui.viz.VisualizadorMatrizFloydWarshall;
import com.eia.felinas.misiones.Mision1;
import com.eia.felinas.misiones.Mision2;
import com.eia.felinas.misiones.Mision3;
import com.eia.felinas.misiones.Mision4;
import com.eia.felinas.misiones.Samples;
import com.eia.felinas.modelo.ResultadoBellmanFord;
import com.eia.felinas.modelo.ResultadoFloydWarshall;
import com.eia.felinas.parser.ErrorDeEntrada;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal: selector de las 4 misiones, areas de entrada/salida,
 * boton "cargar sample" y el panel de visualizacion correspondiente
 * (requisito 7.1 del enunciado). Esta clase es la UNICA que conoce JavaFX Y
 * las clases de "misiones" a la vez: solo llama a Mision*.resolverDetallado
 * y a los Visualizador*; nunca reimplementa logica de grafos aqui.
 */
public class App extends Application {

    /** Metadatos de cada mision para poblar el selector sin repetir texto en varios lugares. */
    private enum Mision {
        UNO(1, "Mision 1 - Rescate en el campo minado", "BFS y DFS - 12%",
                "Pega el mapa: filas, columnas, bombas, inicio y destino de Nina."),
        DOS(2, "Mision 2 - Cuentas de Claude", "Dijkstra - 12%",
                "Pega la red de conexiones para encontrar la ruta mas barata hasta el mainframe."),
        TRES(3, "Mision 3 - La reserva de churun", "Floyd-Warshall y Bellman-Ford - 20%",
                "Pega el mapa de pasadizos (los pesos pueden ser negativos) para maximizar el churun."),
        CUATRO(4, "Mision 4 - Reconectar la red", "Kruskal - 12%",
                "Pega los cables disponibles para reconectar la red al menor costo.");

        final int numero;
        final String titulo;
        final String etiquetaPeso;
        final String descripcion;

        Mision(int numero, String titulo, String etiquetaPeso, String descripcion) {
            this.numero = numero;
            this.titulo = titulo;
            this.etiquetaPeso = etiquetaPeso;
            this.descripcion = descripcion;
        }
    }

    private Mision misionActual = Mision.UNO;
    private final List<Button> botonesMision = new ArrayList<>();

    private Label tituloContenido;
    private Label subtituloContenido;
    private Label etiquetaError;
    private TextArea areaEntrada;
    private TextArea areaSalida;
    private VBox contenidoVisualizacion;

    @Override
    public void start(Stage escenario) {
        BorderPane raiz = new BorderPane();
        raiz.getStyleClass().add("root-app");
        raiz.setLeft(construirBarraLateral());
        raiz.setCenter(construirContenido());

        Scene escena = new Scene(raiz, 1180, 760);
        escena.getStylesheets().add(getClass().getResource("/css/tema.css").toExternalForm());

        escenario.setTitle("The Feline Graph Chronicles - Parcial Lenguajes y Compiladores");
        escenario.setScene(escena);
        escenario.show();

        seleccionarMision(Mision.UNO);
    }

    // ---------- Barra lateral ----------

    private VBox construirBarraLateral() {
        Label titulo = new Label("The Feline\nGraph Chronicles");
        titulo.getStyleClass().add("app-title");
        Label subtitulo = new Label("Pola y Minerva contra Limon");
        subtitulo.getStyleClass().add("app-subtitle");

        VBox sidebar = new VBox(6, titulo, subtitulo);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);

        VBox tarjetas = new VBox(10);
        tarjetas.setPadding(new Insets(24, 0, 0, 0));
        for (Mision mision : Mision.values()) {
            Button boton = construirTarjetaMision(mision);
            botonesMision.add(boton);
            tarjetas.getChildren().add(boton);
        }
        sidebar.getChildren().add(tarjetas);
        return sidebar;
    }

    private Button construirTarjetaMision(Mision mision) {
        Label linea1 = new Label(mision.titulo);
        linea1.getStyleClass().add("mission-card-titulo");
        linea1.setWrapText(true);
        Label linea2 = new Label(mision.etiquetaPeso);
        linea2.getStyleClass().add("mission-card-peso");
        VBox contenido = new VBox(2, linea1, linea2);

        Button boton = new Button();
        boton.setGraphic(contenido);
        boton.getStyleClass().add("mission-card");
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setOnAction(e -> seleccionarMision(mision));
        return boton;
    }

    private void seleccionarMision(Mision mision) {
        this.misionActual = mision;
        for (int i = 0; i < Mision.values().length; i++) {
            botonesMision.get(i).getStyleClass().remove("mission-card-selected");
        }
        botonesMision.get(mision.ordinal()).getStyleClass().add("mission-card-selected");

        tituloContenido.setText(mision.titulo);
        subtituloContenido.setText(mision.descripcion);
        areaEntrada.clear();
        areaSalida.clear();
        ocultarError();
        contenidoVisualizacion.getChildren().clear();
    }

    // ---------- Contenido central ----------

    private VBox construirContenido() {
        tituloContenido = new Label();
        tituloContenido.getStyleClass().add("content-title");
        subtituloContenido = new Label();
        subtituloContenido.getStyleClass().add("content-subtitle");
        subtituloContenido.setWrapText(true);

        etiquetaError = new Label();
        etiquetaError.getStyleClass().add("panel-error");
        etiquetaError.setWrapText(true);
        etiquetaError.setMaxWidth(Double.MAX_VALUE);
        etiquetaError.setVisible(false);
        etiquetaError.setManaged(false);

        VBox encabezado = new VBox(4, tituloContenido, subtituloContenido, etiquetaError);
        encabezado.setPadding(new Insets(24, 28, 12, 28));

        HBox filaEntradaSalida = construirFilaEntradaSalida();

        Label etiquetaViz = new Label("Visualizacion");
        etiquetaViz.getStyleClass().add("label-seccion");

        contenidoVisualizacion = new VBox(18);
        contenidoVisualizacion.setPadding(new Insets(14));
        contenidoVisualizacion.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollViz = new ScrollPane(contenidoVisualizacion);
        scrollViz.setFitToWidth(true);
        scrollViz.getStyleClass().add("panel-visualizacion");
        VBox.setVgrow(scrollViz, Priority.ALWAYS);

        VBox seccionViz = new VBox(8, etiquetaViz, scrollViz);
        seccionViz.setPadding(new Insets(4, 28, 24, 28));
        VBox.setVgrow(seccionViz, Priority.ALWAYS);

        VBox contenedor = new VBox(encabezado, filaEntradaSalida, seccionViz);
        return contenedor;
    }

    private HBox construirFilaEntradaSalida() {
        areaEntrada = new TextArea();
        areaEntrada.getStyleClass().add("text-area-io");
        areaEntrada.setPromptText("Pega aqui la entrada de la mision, o carga el ejemplo del enunciado...");
        areaEntrada.setPrefRowCount(9);

        areaSalida = new TextArea();
        areaSalida.getStyleClass().add("text-area-io");
        areaSalida.setEditable(false);
        areaSalida.setPrefRowCount(9);

        Button botonSample = new Button("Cargar ejemplo");
        botonSample.getStyleClass().add("btn-secundario");
        botonSample.setOnAction(e -> areaEntrada.setText(Samples.deMision(misionActual.numero)));

        Button botonResolver = new Button("Resolver");
        botonResolver.getStyleClass().add("btn-primario");
        botonResolver.setOnAction(e -> resolver());

        Label etiquetaEntrada = new Label("Entrada");
        etiquetaEntrada.getStyleClass().add("label-seccion");
        HBox filaBotonesEntrada = new HBox(10, etiquetaEntrada);
        HBox espaciador = new HBox();
        HBox.setHgrow(espaciador, Priority.ALWAYS);
        filaBotonesEntrada.getChildren().addAll(espaciador, botonSample, botonResolver);
        filaBotonesEntrada.setAlignment(Pos.CENTER_LEFT);

        VBox columnaEntrada = new VBox(6, filaBotonesEntrada, areaEntrada);
        HBox.setHgrow(columnaEntrada, Priority.ALWAYS);

        Label etiquetaSalida = new Label("Salida");
        etiquetaSalida.getStyleClass().add("label-seccion");
        VBox columnaSalida = new VBox(6, etiquetaSalida, areaSalida);
        HBox.setHgrow(columnaSalida, Priority.ALWAYS);

        HBox fila = new HBox(16, columnaEntrada, columnaSalida);
        fila.setPadding(new Insets(0, 28, 12, 28));
        return fila;
    }

    // ---------- Resolucion y manejo de errores ----------

    private void resolver() {
        ocultarError();
        contenidoVisualizacion.getChildren().clear();
        try {
            switch (misionActual) {
                case UNO -> resolverMision1();
                case DOS -> resolverMision2();
                case TRES -> resolverMision3();
                case CUATRO -> resolverMision4();
            }
        } catch (ErrorDeEntrada e) {
            mostrarError(e.getMessage());
        } catch (RuntimeException e) {
            // Ultima red de seguridad: nunca una excepcion cruda ni un
            // stack trace en la interfaz (requisito 2.2 del enunciado).
            mostrarError("Ocurrio un error inesperado al procesar la entrada: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        etiquetaError.setText(mensaje);
        etiquetaError.setVisible(true);
        etiquetaError.setManaged(true);
        areaSalida.clear();
    }

    private void ocultarError() {
        etiquetaError.setVisible(false);
        etiquetaError.setManaged(false);
    }

    private void resolverMision1() {
        Mision1.Resultado resultado = Mision1.resolverDetallado(areaEntrada.getText());
        areaSalida.setText(resultado.salida());
        for (Mision1.Caso caso : resultado.casos()) {
            contenidoVisualizacion.getChildren().add(
                    envolverConTitulo("Caso #" + caso.numero(), VisualizadorGrilla.dibujar(caso)));
        }
    }

    private void resolverMision2() {
        Mision2.Resultado resultado = Mision2.resolverDetallado(areaEntrada.getText());
        areaSalida.setText(resultado.salida());
        for (Mision2.Caso caso : resultado.casos()) {
            List<Integer> ruta = caso.dijkstra().hayRuta(caso.destino())
                    ? caso.dijkstra().reconstruirRuta(caso.destino())
                    : List.of();
            Region grafo = VisualizadorGrafo.dibujarConRuta(
                    caso.n(), caso.aristas(), false, ruta, false,
                    caso.origen(), caso.destino(), VisualizadorGrafo.colorRuta());
            contenidoVisualizacion.getChildren().add(envolverConTitulo("Caso #" + caso.numero(), grafo));
        }
    }

    private void resolverMision3() {
        Mision3.Resultado resultado = Mision3.resolverDetallado(areaEntrada.getText());
        areaSalida.setText(resultado.salida());
        for (Mision3.Caso caso : resultado.casos()) {
            contenidoVisualizacion.getChildren().add(
                    envolverConTitulo("Matriz - Caso #" + caso.numero(),
                            VisualizadorMatrizFloydWarshall.dibujar(caso)));
            contenidoVisualizacion.getChildren().add(
                    envolverConTitulo("Grafo - Caso #" + caso.numero(), grafoDeMision3(caso)));
        }
    }

    private Region grafoDeMision3(Mision3.Caso caso) {
        ResultadoFloydWarshall fw = caso.floydWarshall();
        ResultadoBellmanFord bf = caso.bellmanFord();
        List<Integer> camino;
        boolean cerrarCiclo;
        var color = VisualizadorGrafo.colorRuta();

        if (fw.hayRuta(caso.origen(), caso.destino()) && fw.esNoAcotado(caso.origen(), caso.destino())) {
            camino = new ArrayList<>();
            for (int nodo : bf.cicloResponsable()) {
                camino.add(nodo);
            }
            cerrarCiclo = true;
            color = VisualizadorGrafo.colorCiclo();
        } else if (fw.hayRuta(caso.origen(), caso.destino())) {
            camino = bf.reconstruirRuta(caso.destino());
            cerrarCiclo = false;
        } else {
            camino = List.of();
            cerrarCiclo = false;
        }
        return VisualizadorGrafo.dibujarConRuta(
                caso.n(), caso.aristas(), true, camino, cerrarCiclo,
                caso.origen(), caso.destino(), color);
    }

    private void resolverMision4() {
        Mision4.Resultado resultado = Mision4.resolverDetallado(areaEntrada.getText());
        areaSalida.setText(resultado.salida());
        for (Mision4.Caso caso : resultado.casos()) {
            Region grafo = VisualizadorGrafo.dibujarConjuntoDestacado(
                    caso.n(), caso.cables(), caso.mst().seleccionadas());
            String etiqueta = "Caso #" + caso.numero()
                    + (caso.mst().conectado() ? "" : " (red incompleta)");
            contenidoVisualizacion.getChildren().add(envolverConTitulo(etiqueta, grafo));
        }
    }

    private Region envolverConTitulo(String titulo, Region contenido) {
        Label etiqueta = new Label(titulo);
        etiqueta.getStyleClass().add("caso-titulo");
        VBox contenedor = new VBox(6, etiqueta, contenido);
        contenedor.setAlignment(Pos.TOP_CENTER);
        return contenedor;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
