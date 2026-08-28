package com.eia.felinas.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Ventana principal de la aplicacion.
 *
 * Por ahora es un marcador de posicion: en la fase de GUI se reemplaza el
 * contenido por el selector de misiones, las areas de entrada/salida y los
 * botones "cargar sample" (requisito 7.1 del enunciado).
 */
public class App extends Application {

    @Override
    public void start(Stage escenario) {
        Label titulo = new Label("The Feline Graph Chronicles");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Pola y Minerva contra Limon y Nero");
        subtitulo.setStyle("-fx-font-size: 16px;");

        Label estado = new Label("Esqueleto del proyecto listo. Misiones en construccion...");

        VBox raiz = new VBox(12, titulo, subtitulo, estado);
        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(40));

        escenario.setTitle("The Feline Graph Chronicles - Parcial Lenguajes y Compiladores");
        escenario.setScene(new Scene(raiz, 900, 600));
        escenario.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
