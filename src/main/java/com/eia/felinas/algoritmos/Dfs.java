package com.eia.felinas.algoritmos;

import com.eia.felinas.modelo.Grilla;
import com.eia.felinas.modelo.ResultadoBusqueda;

/**
 * Busqueda en profundidad (DFS) sobre la grilla de la Mision 1,
 * implementada con PILA EXPLICITA (sin recursion).
 *
 * Complejidad temporal: O(R*C) — cada celda se visita a lo sumo una vez y
 * cada visita examina 4 vecinos.
 * Complejidad espacial: O(R*C) — visitados mas las dos pilas paralelas.
 *
 * Por que sin recursion (seccion 2.1 del enunciado): la grilla puede tener
 * 1000 x 1000 = 10^6 celdas y el peor camino de DFS puede atravesarlas casi
 * todas, es decir 10^6 llamadas anidadas. El stack de la JVM (~512 KB por
 * defecto) aguanta unas decenas de miles: la version recursiva muere con
 * StackOverflowError. La pila explicita vive en el heap y no tiene ese limite.
 *
 * Como se garantiza el MISMO camino que la version recursiva: cada posicion
 * de la pila es un "marco de llamada" con dos datos, la celda y la proxima
 * direccion a intentar (0=arriba, 1=abajo, 2=izquierda, 3=derecha). El tope
 * de la pila intenta sus direcciones en ese orden fijo; cuando encuentra un
 * vecino valido lo apila (equivale a la llamada recursiva) y cuando agota
 * las 4 direcciones se desapila (equivale al retorno / backtracking). Al
 * alcanzar el destino, la pila ES exactamente el camino encontrado.
 *
 * DFS no garantiza el camino mas corto: se compromete con la primera rama
 * que encuentra y solo retrocede si se estanca. Por eso en el sample del
 * enunciado BFS da 18 movimientos y este DFS da 32: mismo grafo, distinto
 * orden de expansion de la frontera.
 */
public final class Dfs {

    private Dfs() {
    }

    /**
     * Busca UN camino (no necesariamente minimo) desde el inicio hasta el
     * destino, expandiendo vecinos en el orden arriba, abajo, izquierda,
     * derecha exigido por el enunciado.
     */
    public static ResultadoBusqueda buscar(Grilla grilla,
                                           int filaInicio, int colInicio,
                                           int filaDestino, int colDestino) {
        // Mismas reglas de borde que BFS (enunciado, Mision 1).
        if (grilla.hayBomba(filaInicio, colInicio) || grilla.hayBomba(filaDestino, colDestino)) {
            return ResultadoBusqueda.inalcanzable();
        }
        int inicio = grilla.indice(filaInicio, colInicio);
        int destino = grilla.indice(filaDestino, colDestino);
        if (inicio == destino) {
            return ResultadoBusqueda.conCamino(new int[] {inicio});
        }

        int totalCeldas = grilla.celdas();
        int columnas = grilla.columnas();
        boolean[] visitado = new boolean[totalCeldas];

        // Dos pilas paralelas = los marcos de la recursion simulada.
        // La profundidad nunca supera el numero de celdas.
        int[] pilaCelda = new int[totalCeldas];
        int[] pilaDireccion = new int[totalCeldas];
        int cima = 0;

        visitado[inicio] = true;
        pilaCelda[cima] = inicio;
        pilaDireccion[cima] = 0;
        cima++;

        while (cima > 0) {
            int celda = pilaCelda[cima - 1];
            if (celda == destino) {
                // La pila contiene el camino completo inicio -> destino.
                int[] camino = new int[cima];
                System.arraycopy(pilaCelda, 0, camino, 0, cima);
                return ResultadoBusqueda.conCamino(camino);
            }
            int direccion = pilaDireccion[cima - 1];
            if (direccion == 4) {
                // Direcciones agotadas: backtracking (retorno de la "llamada").
                cima--;
                continue;
            }
            pilaDireccion[cima - 1]++;

            int fila = celda / columnas;
            int col = celda % columnas;
            int nuevaFila = fila + Direcciones.DELTA_FILA[direccion];
            int nuevaCol = col + Direcciones.DELTA_COLUMNA[direccion];
            if (!grilla.dentro(nuevaFila, nuevaCol)) {
                continue;
            }
            int vecino = grilla.indice(nuevaFila, nuevaCol);
            if (visitado[vecino] || grilla.hayBomba(vecino)) {
                continue;
            }
            // "Llamada recursiva": el vecino pasa a ser el nuevo tope.
            visitado[vecino] = true;
            pilaCelda[cima] = vecino;
            pilaDireccion[cima] = 0;
            cima++;
        }
        return ResultadoBusqueda.inalcanzable();
    }
}
