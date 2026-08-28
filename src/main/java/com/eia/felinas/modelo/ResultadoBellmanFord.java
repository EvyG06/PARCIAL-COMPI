package com.eia.felinas.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de Bellman-Ford maximizante desde un origen fijo: el mejor
 * churun hacia cada nodo, la marca de nodos no acotados, los padres para
 * reconstruir rutas y un ciclo de ganancia positiva "responsable" (si hay)
 * para que la visualizacion lo resalte.
 */
public final class ResultadoBellmanFord {

    private final long[] mejores;
    private final boolean[] noAcotado;
    private final int[] padre;
    private final int[] cicloResponsable; // vacio si no hay ciclo positivo alcanzable

    public ResultadoBellmanFord(long[] mejores, boolean[] noAcotado,
                                int[] padre, int[] cicloResponsable) {
        this.mejores = mejores;
        this.noAcotado = noAcotado;
        this.padre = padre;
        this.cicloResponsable = cicloResponsable;
    }

    public boolean hayRuta(int nodo) {
        return mejores[nodo] != Pesos.SIN_RUTA;
    }

    public boolean esNoAcotado(int nodo) {
        return noAcotado[nodo];
    }

    /** Mejor churun hasta el nodo; solo valido si hayRuta y no esNoAcotado. */
    public long mejor(int nodo) {
        return mejores[nodo];
    }

    /** Nodos de un ciclo de ganancia positiva, en orden de recorrido; vacio si no hay. */
    public int[] cicloResponsable() {
        return cicloResponsable;
    }

    /**
     * Reconstruye la ruta origen -> destino siguiendo los padres.
     * Solo tiene sentido para nodos alcanzables y acotados (en un grafo sin
     * ciclos positivos utiles, los padres forman un arbol sin bucles).
     */
    public List<Integer> reconstruirRuta(int destino) {
        List<Integer> ruta = new ArrayList<>();
        int limite = mejores.length + 1; // proteccion ante padres corruptos
        for (int nodo = destino; nodo != -1 && limite > 0; nodo = padre[nodo], limite--) {
            ruta.add(nodo);
        }
        java.util.Collections.reverse(ruta);
        return ruta;
    }
}
