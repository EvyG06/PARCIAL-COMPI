package com.eia.felinas.modelo;

/**
 * Resultado de una busqueda (BFS o DFS) sobre la grilla.
 *
 * El camino se guarda como arreglo de indices planos de celda (ver
 * Grilla.indice), del inicio al destino inclusive: es la forma mas compacta
 * de conservarlo cuando la grilla tiene 10^6 celdas, y la visualizacion lo
 * decodifica con fila = indice / columnas, columna = indice % columnas.
 */
public final class ResultadoBusqueda {

    private static final ResultadoBusqueda INALCANZABLE =
            new ResultadoBusqueda(-1, new int[0]);

    private final int movimientos;
    private final int[] camino;

    private ResultadoBusqueda(int movimientos, int[] camino) {
        this.movimientos = movimientos;
        this.camino = camino;
    }

    public static ResultadoBusqueda conCamino(int[] camino) {
        // Un camino de k celdas tiene k - 1 movimientos.
        return new ResultadoBusqueda(camino.length - 1, camino);
    }

    public static ResultadoBusqueda inalcanzable() {
        return INALCANZABLE;
    }

    public boolean esAlcanzable() {
        return movimientos >= 0;
    }

    /** Numero de movimientos del camino encontrado, o -1 si no hay camino. */
    public int movimientos() {
        return movimientos;
    }

    /** Indices planos de las celdas del camino, del inicio al destino. */
    public int[] camino() {
        return camino;
    }
}
