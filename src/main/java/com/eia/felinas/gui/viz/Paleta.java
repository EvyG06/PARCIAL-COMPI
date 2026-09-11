package com.eia.felinas.gui.viz;

import javafx.scene.paint.Color;

/**
 * Paleta de colores centralizada del tema "limon y turquesa": el nombre
 * limon es un guino directo al villano del enunciado (Limon, el Lider del
 * Mal), y turquesa es el color complementario que usan Pola y Minerva.
 *
 * Todos los Visualizador* leen de aqui en vez de repetir codigos hexadecimales,
 * asi que cambiar el tema del proyecto es cuestion de editar un solo archivo.
 */
public final class Paleta {

    /** Fondo de celdas/nodos sin marcar. */
    public static final Color FONDO = Color.web("#f5fdf9");
    /** Bordes y aristas sin resaltar. */
    public static final Color BORDE = Color.web("#b9d8d4");
    /** Texto y trazos oscuros (bombas, contornos de nodo). */
    public static final Color OSCURO = Color.web("#123c40");

    /** Turquesa: color de Pola y Minerva. Usado para el inicio y la ruta optima. */
    public static final Color TURQUESA = Color.web("#00acc1");
    public static final Color TURQUESA_OSCURO = Color.web("#00838f");

    /** Limon: guino al nombre del villano. Usado para el destino/la respuesta. */
    public static final Color LIMON = Color.web("#c6d94d");
    public static final Color LIMON_OSCURO = Color.web("#9e9d24");

    /** Rojo coral: peligro (ciclos de ganancia positiva, errores). */
    public static final Color PELIGRO = Color.web("#e2564f");

    private Paleta() {
    }
}
