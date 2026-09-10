package com.eia.felinas.algoritmos;

import com.eia.felinas.modelo.Arista;
import com.eia.felinas.modelo.Pesos;
import com.eia.felinas.modelo.ResultadoDijkstra;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Dijkstra para la Mision 2: costo minimo desde un origen hasta cada nodo,
 * sobre un grafo NO DIRIGIDO con pesos no negativos.
 *
 * Complejidad temporal: O((N + C) log N) usando una cola de prioridad
 * binaria (java.util.PriorityQueue, obligatoria segun el enunciado): cada
 * arista genera a lo sumo una insercion adicional y cada insercion o
 * extraccion cuesta O(log N). Con N <= 10,000 y C <= 100,000 son un par de
 * millones de operaciones, muy por debajo de un escaneo O(N^2) que el
 * enunciado prohibe explicitamente a estos limites.
 * Complejidad espacial: O(N + C) para la lista de adyacencia y los
 * arreglos de distancias/padres.
 *
 * Por que es el algoritmo correcto: los pesos son no negativos (garantia
 * del enunciado, seccion 4), que es exactamente la condicion que hace a
 * Dijkstra optimo. Cuando un nodo sale definitivamente de la cola con su
 * distancia minima ya fijada, ningun camino descubierto despues puede
 * mejorarla, porque toda arista adicional solo puede sumar (nunca restar) al
 * costo acumulado. Con pesos negativos esa garantia se rompe: un camino mas
 * largo en numero de aristas podria terminar siendo mas barato despues de
 * que Dijkstra ya dio por cerrado un nodo, y el algoritmo devolveria una
 * distancia incorrecta sin ningun aviso.
 */
public final class Dijkstra {

    private Dijkstra() {
    }

    /**
     * Calcula el costo minimo desde el origen hacia todos los nodos de un
     * grafo no dirigido de n nodos (0 a n-1). Conexiones repetidas entre el
     * mismo par y auto-lazos se toleran sin deduplicar: la relajacion se
     * queda automaticamente con el costo mas barato disponible.
     */
    public static ResultadoDijkstra calcular(int n, List<Arista> aristas, int origen) {
        List<List<Arista>> adyacencia = construirAdyacenciaBidireccional(n, aristas);

        long[] distancia = new long[n];
        int[] padre = new int[n];
        for (int i = 0; i < n; i++) {
            distancia[i] = Pesos.SIN_RUTA;
            padre[i] = -1;
        }
        distancia[origen] = 0;

        // Cada entrada de la cola es {distancia acumulada, nodo}. Un nodo
        // puede quedar encolado varias veces con distintas distancias; las
        // entradas obsoletas se descartan al desencolar comparando contra
        // la distancia vigente (mas simple que actualizar la prioridad in
        // situ, que PriorityQueue no soporta directamente).
        PriorityQueue<long[]> pendientes = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pendientes.add(new long[] {0L, origen});
        boolean[] finalizado = new boolean[n];

        while (!pendientes.isEmpty()) {
            long[] tope = pendientes.poll();
            int nodo = (int) tope[1];
            if (finalizado[nodo]) {
                continue; // entrada obsoleta: este nodo ya se cerro con una distancia mejor
            }
            finalizado[nodo] = true;

            for (Arista arista : adyacencia.get(nodo)) {
                int vecino = arista.desde() == nodo ? arista.hacia() : arista.desde();
                if (finalizado[vecino]) {
                    continue;
                }
                long candidato = distancia[nodo] + arista.peso();
                if (distancia[vecino] == Pesos.SIN_RUTA || candidato < distancia[vecino]) {
                    distancia[vecino] = candidato;
                    padre[vecino] = nodo;
                    pendientes.add(new long[] {candidato, vecino});
                }
            }
        }
        return new ResultadoDijkstra(distancia, padre);
    }

    /**
     * Cada arista se guarda en la lista de sus dos extremos: el grafo de
     * esta mision es no dirigido (enunciado, seccion 4). Un auto-lazo solo
     * se agrega una vez, para no relajarlo dos veces por recorrido.
     */
    private static List<List<Arista>> construirAdyacenciaBidireccional(int n, List<Arista> aristas) {
        List<List<Arista>> adyacencia = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adyacencia.add(new ArrayList<>());
        }
        for (Arista arista : aristas) {
            adyacencia.get(arista.desde()).add(arista);
            if (arista.desde() != arista.hacia()) {
                adyacencia.get(arista.hacia()).add(arista);
            }
        }
        return adyacencia;
    }
}
