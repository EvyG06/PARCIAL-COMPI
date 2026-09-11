package com.eia.felinas.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Dibuja huellas de gato como decoracion del tema (separadores, marcadores
 * de mision). Se dibujan a mano en un Canvas en vez de cargar un icono,
 * asi el motivo grafico no depende de ningun archivo externo.
 */
final class Huellas {

    private Huellas() {
    }

    /** Una huella de tamano fijo: una almohadilla central y cuatro dedos. */
    static Canvas dibujar(double tamano, Color color) {
        Canvas lienzo = new Canvas(tamano, tamano);
        GraphicsContext gc = lienzo.getGraphicsContext2D();
        gc.setFill(color);

        double padAncho = tamano * 0.5;
        double padAlto = tamano * 0.4;
        gc.fillOval((tamano - padAncho) / 2, tamano * 0.5, padAncho, padAlto);

        double dedoAncho = tamano * 0.16;
        double dedoAlto = tamano * 0.22;
        gc.fillOval(tamano * 0.06, tamano * 0.22, dedoAncho, dedoAlto);
        gc.fillOval(tamano * 0.30, tamano * 0.03, dedoAncho, dedoAlto);
        gc.fillOval(tamano * 0.54, tamano * 0.03, dedoAncho, dedoAlto);
        gc.fillOval(tamano * 0.78, tamano * 0.22, dedoAncho, dedoAlto);
        return lienzo;
    }
}
