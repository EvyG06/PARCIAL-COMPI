package com.eia.felinas.algoritmos;

import com.eia.felinas.modelo.Arista;
import com.eia.felinas.modelo.ResultadoMst;

import java.util.ArrayList;
import java.util.List;

/**
 * Kruskal para la Mision 4: arbol de expansion minima (MST) sobre un grafo
 * no dirigido, apoyado en Union-Find con compresion de camino y union por
 * tamano.
 *
 * Complejidad temporal: O(C log C), dominada por ordenar los C cables; el
 * procesamiento posterior con Union-Find es O(C * alfa(N)), practicamente
 * O(C). Con C <= 100,000 es una operacion trivial.
 * Complejidad espacial: O(N + C): el Union-Find y la lista de cables
 * ordenados.
 *
 * Por que es el algoritmo correcto: Kruskal construye el MST global
 * procesando los cables de menor a mayor costo y aceptando cada uno que no
 * cierre un ciclo (propiedad de corte de los arboles de expansion minima).
 * Es la forma directa de resolver "conectar todas las intersecciones con el
 * minimo cable total" sin necesitar elegir un nodo de arranque como exige
 * Prim, y su costo esta dominado por un ordenamiento simple.
 */
public final class Kruskal {

    private Kruskal() {
    }

    /**
     * Calcula el MST de un grafo no dirigido de n nodos (0 a n-1).
     * Cables duplicados entre el mismo par se toleran: Kruskal simplemente
     * descarta el mas caro al encontrar que ya conecta el mismo conjunto.
     * Auto-lazos se descartan de entrada porque nunca pueden formar parte
     * de un arbol.
     */
    public static ResultadoMst calcular(int n, List<Arista> aristas) {
        List<Arista> ordenadas = new ArrayList<>(aristas);
        ordenadas.sort((a, b) -> Long.compare(a.peso(), b.peso()));

        UnionFind unionFind = new UnionFind(n);
        List<Arista> seleccionadas = new ArrayList<>();
        long costoTotal = 0;

        for (Arista cable : ordenadas) {
            if (cable.desde() == cable.hacia()) {
                continue; // auto-lazo: jamas reduce el numero de componentes
            }
            if (unionFind.unir(cable.desde(), cable.hacia())) {
                seleccionadas.add(cable);
                costoTotal += cable.peso();
                if (seleccionadas.size() == n - 1) {
                    break; // el arbol ya conecta las n intersecciones
                }
            }
        }

        boolean conectado = n <= 1 || seleccionadas.size() == n - 1;
        return new ResultadoMst(conectado, costoTotal, seleccionadas);
    }
}
