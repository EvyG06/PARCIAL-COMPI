package com.eia.felinas.algoritmos;

import com.eia.felinas.modelo.Grilla;
import com.eia.felinas.modelo.ResultadoBusqueda;

/**
 * Busqueda en anchura (BFS) sobre la grilla de la Mision 1.
 *
 * Complejidad temporal: O(R*C) — cada celda entra a la cola a lo sumo una
 * vez y sus 4 vecinos se examinan en tiempo constante.
 * Complejidad espacial: O(R*C) — arreglos de visitados y padres mas la cola.
 *
 * Por que es el algoritmo correcto: la grilla es un grafo NO ponderado
 * (cada movimiento cuesta exactamente 1). BFS explora por niveles de
 * distancia creciente al origen, asi que la primera vez que alcanza el
 * destino lo hace necesariamente por un camino de longitud minima. Esa
 * garantia es la que DFS no tiene: ambos exploran el mismo grafo y solo
 * difieren en el orden en que expanden la frontera (cola FIFO contra pila
 * LIFO), y por eso en el sample BFS da 18 y DFS da 32.
 */
public final class Bfs {

    private Bfs() {
    }

    /**
     * Busca el camino mas corto desde (filaInicio, colInicio) hasta
     * (filaDestino, colDestino) evitando las bombas.
     */
    public static ResultadoBusqueda buscar(Grilla grilla,
                                           int filaInicio, int colInicio,
                                           int filaDestino, int colDestino) {
        // Regla del enunciado: bomba en el inicio o el destino => inalcanzable.
        if (grilla.hayBomba(filaInicio, colInicio) || grilla.hayBomba(filaDestino, colDestino)) {
            return ResultadoBusqueda.inalcanzable();
        }
        int inicio = grilla.indice(filaInicio, colInicio);
        int destino = grilla.indice(filaDestino, colDestino);
        // Regla del enunciado: inicio y destino coinciden => 0 movimientos.
        if (inicio == destino) {
            return ResultadoBusqueda.conCamino(new int[] {inicio});
        }

        int totalCeldas = grilla.celdas();
        int columnas = grilla.columnas();
        boolean[] visitado = new boolean[totalCeldas];
        int[] padre = new int[totalCeldas];

        // Cola FIFO sobre un arreglo plano: cada celda entra a lo sumo una
        // vez, asi que un arreglo de tamano R*C nunca se desborda y evita el
        // costo de encajonar Integers en una ArrayDeque con 10^6 celdas.
        int[] cola = new int[totalCeldas];
        int cabeza = 0;
        int fin = 0;

        visitado[inicio] = true;
        padre[inicio] = -1;
        cola[fin++] = inicio;

        while (cabeza < fin) {
            int celda = cola[cabeza++];
            if (celda == destino) {
                return ResultadoBusqueda.conCamino(reconstruirCamino(padre, destino));
            }
            int fila = celda / columnas;
            int col = celda % columnas;
            for (int dir = 0; dir < 4; dir++) {
                int nuevaFila = fila + Direcciones.DELTA_FILA[dir];
                int nuevaCol = col + Direcciones.DELTA_COLUMNA[dir];
                if (!grilla.dentro(nuevaFila, nuevaCol)) {
                    continue;
                }
                int vecino = grilla.indice(nuevaFila, nuevaCol);
                if (visitado[vecino] || grilla.hayBomba(vecino)) {
                    continue;
                }
                visitado[vecino] = true;
                padre[vecino] = celda;
                cola[fin++] = vecino;
            }
        }
        return ResultadoBusqueda.inalcanzable();
    }

    /** Recorre los padres desde el destino hacia atras y devuelve el camino inicio -> destino. */
    private static int[] reconstruirCamino(int[] padre, int destino) {
        int longitud = 0;
        for (int celda = destino; celda != -1; celda = padre[celda]) {
            longitud++;
        }
        int[] camino = new int[longitud];
        int posicion = longitud - 1;
        for (int celda = destino; celda != -1; celda = padre[celda]) {
            camino[posicion--] = celda;
        }
        return camino;
    }
}
