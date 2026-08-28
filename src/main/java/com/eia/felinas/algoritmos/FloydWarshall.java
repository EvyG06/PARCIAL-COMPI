package com.eia.felinas.algoritmos;

import com.eia.felinas.modelo.Arista;
import com.eia.felinas.modelo.Pesos;
import com.eia.felinas.modelo.ResultadoFloydWarshall;

import java.util.List;

/**
 * Floyd-Warshall MAXIMIZANTE para la Mision 3: maximo churun acumulable
 * entre todos los pares de nodos, permitiendo repetir nodos y aristas
 * (caminatas, no caminos simples).
 *
 * Complejidad temporal: O(N^3) por el triple bucle, mas O(N^3) del pase de
 * no acotados = O(N^3) total. Con N <= 100 son 10^6 operaciones: trivial.
 * Complejidad espacial: O(N^2) por la matriz.
 *
 * Por que es el algoritmo correcto: la mision pide el maximo sobre
 * CAMINATAS entre TODOS los pares (la GUI debe mostrar la matriz N x N
 * completa) en un grafo con pesos negativos. Dijkstra queda descartado por
 * los pesos negativos; buscar el camino simple mas largo seria NP-duro.
 * Floyd-Warshall calcula todos los pares en una sola pasada de programacion
 * dinamica: d[i][j] tras la iteracion k = mejor caminata de i a j usando
 * como intermedios solo nodos del conjunto {0..k}.
 *
 * Pase de no acotados (enunciado, seccion 5.1): tras el triple bucle,
 * (i, j) es no acotado si y solo si existe k con d[i][k] finito,
 * d[k][k] > 0 y d[k][j] finito — es decir, un ciclo de ganancia positiva
 * que pasa por k, alcanzable desde i y desde el cual se alcanza j: cada
 * vuelta extra al ciclo suma churun sin limite. Sin este pase la matriz
 * contiene numeros grandes sin significado en lugar de infinitos.
 */
public final class FloydWarshall {

    private FloydWarshall() {
    }

    public static ResultadoFloydWarshall calcular(int n, List<Arista> aristas) {
        long[][] d = new long[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = Pesos.SIN_RUTA;
            }
            d[i][i] = 0; // La caminata vacia de un nodo a si mismo vale 0.
        }
        // Pasadizos repetidos entre el mismo par ordenado: se queda el maximo.
        for (Arista arista : aristas) {
            if (arista.peso() > d[arista.desde()][arista.hacia()]) {
                d[arista.desde()][arista.hacia()] = arista.peso();
            }
        }

        // Triple bucle clasico, maximizando en lugar de minimizando.
        // Guardas contra SIN_RUTA: jamas se suma sobre el centinela (2.1).
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (d[i][k] == Pesos.SIN_RUTA) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (d[k][j] == Pesos.SIN_RUTA) {
                        continue;
                    }
                    long porK = d[i][k] + d[k][j];
                    if (porK > d[i][j] || d[i][j] == Pesos.SIN_RUTA) {
                        d[i][j] = porK;
                    }
                }
            }
        }

        // Pase extra: marcar los pares cuyo maximo es infinito.
        boolean[][] noAcotado = new boolean[n][n];
        for (int k = 0; k < n; k++) {
            if (d[k][k] <= 0) {
                continue; // Ningun ciclo de ganancia positiva pasa por k.
            }
            for (int i = 0; i < n; i++) {
                if (d[i][k] == Pesos.SIN_RUTA) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (d[k][j] != Pesos.SIN_RUTA) {
                        noAcotado[i][j] = true;
                    }
                }
            }
        }
        return new ResultadoFloydWarshall(d, noAcotado);
    }
}
