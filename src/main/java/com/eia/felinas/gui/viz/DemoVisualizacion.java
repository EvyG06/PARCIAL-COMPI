package com.eia.felinas.gui.viz;

import com.eia.felinas.misiones.Mision1;
import com.eia.felinas.misiones.Mision3;
import com.eia.felinas.misiones.Samples;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Punto de entrada de SOLO VERIFICACION MANUAL: muestra los visualizadores
 * de la Mision 1 y la Mision 3 con los samples del enunciado, en pestanas
 * separadas. No forma parte de la aplicacion final (esa la arma la GUI base
 * en com.eia.felinas.gui.App); existe para poder comprobar visualmente
 * estos componentes sin depender de que la GUI completa este lista.
 *
 * Ejecutar con: mvnw javafx:run "-Djavafx.mainClass=com.eia.felinas.gui.viz.DemoVisualizacion"
 */
public class DemoVisualizacion extends Application {

    @Override
    public void start(Stage escenario) {
        TabPane pestanas = new TabPane();
        pestanas.getTabs().add(pestanaMision1());
        pestanas.getTabs().add(pestanaMision3());

        escenario.setTitle("Demo de visualizacion - Feline Graph Chronicles");
        escenario.setScene(new Scene(pestanas, 1000, 650));
        escenario.show();
    }

    private Tab pestanaMision1() {
        Mision1.Resultado resultado = Mision1.resolverDetallado(Samples.deMision(1));
        Mision1.Caso caso = resultado.casos().get(0);

        VBox contenido = new VBox(10, VisualizadorGrilla.dibujar(caso));
        contenido.setPadding(new Insets(16));
        Tab tab = new Tab("Mision 1: BFS vs DFS", new ScrollPane(contenido));
        tab.setClosable(false);
        return tab;
    }

    private Tab pestanaMision3() {
        Mision3.Resultado resultado = Mision3.resolverDetallado(Samples.deMision(3));
        VBox contenido = new VBox(16);
        contenido.setPadding(new Insets(16));
        for (Mision3.Caso caso : resultado.casos()) {
            contenido.getChildren().add(VisualizadorMatrizFloydWarshall.dibujar(caso));
            contenido.getChildren().add(new Separator());
        }
        Tab tab = new Tab("Mision 3: matriz Floyd-Warshall", new ScrollPane(contenido));
        tab.setClosable(false);
        return tab;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
