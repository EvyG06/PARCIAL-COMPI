package com.eia.felinas.algoritmos;

/**
 * Orden fijo de vecinos exigido por el enunciado para la Mision 1:
 * arriba, abajo, izquierda, derecha. BFS y DFS usan EXACTAMENTE este orden;
 * en DFS el orden es lo que hace el resultado determinista y calificable.
 */
final class Direcciones {

    /** Desplazamiento de fila: arriba, abajo, izquierda, derecha. */
    static final int[] DELTA_FILA = {-1, 1, 0, 0};

    /** Desplazamiento de columna: arriba, abajo, izquierda, derecha. */
    static final int[] DELTA_COLUMNA = {0, 0, -1, 1};

    private Direcciones() {
    }
}
