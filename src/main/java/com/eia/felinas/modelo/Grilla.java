package com.eia.felinas.modelo;

/**
 * Grilla de la Mision 1: un campo minado de R x C celdas.
 *
 * Las bombas se guardan en un arreglo booleano PLANO de tamano R*C en lugar
 * de una matriz boolean[R][C]: con grillas de hasta 1000 x 1000 = 10^6
 * celdas, el arreglo plano evita un nivel de indireccion por acceso y
 * mantiene los datos contiguos en memoria. La celda (fila, columna) vive en
 * el indice fila * columnas + columna.
 */
public final class Grilla {

    private final int filas;
    private final int columnas;
    private final boolean[] bombas;

    public Grilla(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.bombas = new boolean[filas * columnas];
    }

    public int filas() {
        return filas;
    }

    public int columnas() {
        return columnas;
    }

    /** Numero total de celdas (R * C). */
    public int celdas() {
        return filas * columnas;
    }

    /** Convierte coordenadas (fila, columna) al indice plano. */
    public int indice(int fila, int columna) {
        return fila * columnas + columna;
    }

    public boolean dentro(int fila, int columna) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
    }

    public void ponerBomba(int fila, int columna) {
        bombas[indice(fila, columna)] = true;
    }

    public boolean hayBomba(int fila, int columna) {
        return bombas[indice(fila, columna)];
    }

    public boolean hayBomba(int indice) {
        return bombas[indice];
    }
}
