package com.eia.felinas.algoritmos;

/**
 * Estructura Union-Find (conjuntos disjuntos) con compresion de camino y
 * union por tamano, exigida por el enunciado para soportar Kruskal en la
 * Mision 4.
 *
 * Complejidad temporal: O(alfa(N)) amortizado por operacion (alfa es la
 * inversa de la funcion de Ackermann, practicamente constante para
 * cualquier N realista), gracias a combinar compresion de camino con union
 * por tamano.
 * Complejidad espacial: O(N).
 *
 * Sin compresion de camino ni union por tamano cada operacion degradaria a
 * O(N) en el peor caso (una cadena larga de padres), lo que en Kruskal con
 * C <= 100,000 aristas podria costar O(N * C) en el peor escenario.
 */
public final class UnionFind {

    private final int[] padre;
    private final int[] tamano;

    public UnionFind(int n) {
        padre = new int[n];
        tamano = new int[n];
        for (int i = 0; i < n; i++) {
            padre[i] = i;
            tamano[i] = 1;
        }
    }

    /** Encuentra la raiz del conjunto de un nodo, comprimiendo el camino recorrido. */
    public int encontrar(int nodo) {
        while (padre[nodo] != nodo) {
            padre[nodo] = padre[padre[nodo]]; // compresion de camino (path halving)
            nodo = padre[nodo];
        }
        return nodo;
    }

    /** Indica si dos nodos ya pertenecen al mismo conjunto (agregar su arista cerraria un ciclo). */
    public boolean mismoConjunto(int a, int b) {
        return encontrar(a) == encontrar(b);
    }

    /**
     * Une los conjuntos de a y b (union por tamano: la raiz del conjunto
     * mas pequeno cuelga de la raiz del mas grande, para mantener los
     * arboles bajos). Devuelve false si ya estaban en el mismo conjunto.
     */
    public boolean unir(int a, int b) {
        int raizA = encontrar(a);
        int raizB = encontrar(b);
        if (raizA == raizB) {
            return false;
        }
        if (tamano[raizA] < tamano[raizB]) {
            int temp = raizA;
            raizA = raizB;
            raizB = temp;
        }
        padre[raizB] = raizA;
        tamano[raizA] += tamano[raizB];
        return true;
    }
}
