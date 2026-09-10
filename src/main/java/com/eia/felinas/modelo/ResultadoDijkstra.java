package com.eia.felinas.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado de Dijkstra desde un origen fijo: el costo minimo hacia cada
 * nodo y los padres para reconstruir la ruta que la visualizacion resalta.
 */
public final class ResultadoDijkstra {

    private final long[] distancias;
    private final int[] padre;

    public ResultadoDijkstra(long[] distancias, int[] padre) {
        this.distancias = distancias;
        this.padre = padre;
    }

    public boolean hayRuta(int nodo) {
        return distancias[nodo] != Pesos.SIN_RUTA;
    }

    /** Costo minimo hasta el nodo; solo valido si hayRuta(nodo). */
    public long distancia(int nodo) {
        return distancias[nodo];
    }

    /**
     * Reconstruye la ruta origen -> destino siguiendo los padres.
     * Solo tiene sentido si hayRuta(destino) es verdadero.
     */
    public List<Integer> reconstruirRuta(int destino) {
        List<Integer> ruta = new ArrayList<>();
        int limite = distancias.length + 1; // proteccion ante padres corruptos
        for (int nodo = destino; nodo != -1 && limite > 0; nodo = padre[nodo], limite--) {
            ruta.add(nodo);
        }
        Collections.reverse(ruta);
        return ruta;
    }
}
