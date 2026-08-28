package com.eia.felinas.algoritmos;

import com.eia.felinas.modelo.Arista;
import com.eia.felinas.modelo.Pesos;
import com.eia.felinas.modelo.ResultadoBellmanFord;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Bellman-Ford MAXIMIZANTE para la Mision 3: mejor churun desde el origen
 * hacia todos los nodos, con deteccion de ciclos de ganancia positiva.
 *
 * Complejidad temporal: O(N * M) — hasta N-1 rondas relajando las M aristas,
 * mas O(N + M) de la propagacion de no acotados. Con N <= 100 y M <= 5000
 * son a lo sumo 5 * 10^5 relajaciones.
 * Complejidad espacial: O(N + M).
 *
 * Por que es el algoritmo correcto: soporta pesos negativos (Dijkstra no) y
 * su ronda extra detecta exactamente los ciclos de ganancia positiva que
 * hacen el churun infinito. Fundamento: una caminata optima ACOTADA no
 * repite nodos (todo ciclo que valiera la pena repetir tendria ganancia
 * positiva, y entonces no seria acotada), asi que usa a lo sumo N-1 aristas;
 * tras N-1 rondas todos los valores acotados ya convergieron. Si algo sigue
 * mejorando en la ronda N, ese nodo pertenece a un ciclo positivo o recibe
 * churun de uno.
 *
 * Criterio de "no acotado" (identico al de Floyd-Warshall, seccion 5.2 del
 * enunciado): la marca se propaga a TODO nodo alcanzable desde un nodo que
 * mejoro en la ronda extra. El destino es infinito solo si queda marcado;
 * sin esta propagacion identica, el cross-check fallaria en cada grafo con
 * un ciclo positivo que no alcanza al destino.
 */
public final class BellmanFord {

    private BellmanFord() {
    }

    public static ResultadoBellmanFord calcular(int n, List<Arista> aristas, int origen) {
        long[] mejores = new long[n];
        int[] padre = new int[n];
        for (int i = 0; i < n; i++) {
            mejores[i] = Pesos.SIN_RUTA;
            padre[i] = -1;
        }
        mejores[origen] = 0;

        // N-1 rondas de relajacion maximizante. Si una ronda completa no
        // cambia nada, ya convergio y las siguientes tampoco cambiarian.
        for (int ronda = 1; ronda < n; ronda++) {
            boolean cambio = false;
            for (Arista arista : aristas) {
                if (relajar(arista, mejores, padre)) {
                    cambio = true;
                }
            }
            if (!cambio) {
                break;
            }
        }

        // Ronda extra: todo nodo que AUN mejora esta en un ciclo de ganancia
        // positiva o es alimentado por uno. Son las semillas del contagio.
        List<Integer> semillas = new ArrayList<>();
        for (Arista arista : aristas) {
            if (relajar(arista, mejores, padre)) {
                semillas.add(arista.hacia());
            }
        }

        // Propagacion: no acotado = alcanzable desde alguna semilla.
        boolean[] noAcotado = new boolean[n];
        List<List<Integer>> adyacencia = construirAdyacencia(n, aristas);
        Deque<Integer> porVisitar = new ArrayDeque<>();
        for (int semilla : semillas) {
            if (!noAcotado[semilla]) {
                noAcotado[semilla] = true;
                porVisitar.add(semilla);
            }
        }
        while (!porVisitar.isEmpty()) {
            int nodo = porVisitar.poll();
            for (int vecino : adyacencia.get(nodo)) {
                if (!noAcotado[vecino]) {
                    noAcotado[vecino] = true;
                    porVisitar.add(vecino);
                }
            }
        }

        int[] ciclo = semillas.isEmpty()
                ? new int[0]
                : extraerCiclo(n, padre, semillas.get(0));
        return new ResultadoBellmanFord(mejores, noAcotado, padre, ciclo);
    }

    /** Relaja una arista maximizando; nunca opera sobre el centinela (2.1). */
    private static boolean relajar(Arista arista, long[] mejores, int[] padre) {
        if (mejores[arista.desde()] == Pesos.SIN_RUTA) {
            return false;
        }
        long candidato = mejores[arista.desde()] + arista.peso();
        if (mejores[arista.hacia()] == Pesos.SIN_RUTA || candidato > mejores[arista.hacia()]) {
            mejores[arista.hacia()] = candidato;
            padre[arista.hacia()] = arista.desde();
            return true;
        }
        return false;
    }

    private static List<List<Integer>> construirAdyacencia(int n, List<Arista> aristas) {
        List<List<Integer>> adyacencia = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adyacencia.add(new ArrayList<>());
        }
        for (Arista arista : aristas) {
            adyacencia.get(arista.desde()).add(arista.hacia());
        }
        return adyacencia;
    }

    /**
     * Extrae un ciclo de ganancia positiva para que la GUI lo resalte.
     * Tecnica estandar: desde una semilla se retrocede N veces por los
     * padres — eso garantiza aterrizar DENTRO del ciclo (la cadena de padres
     * de una semilla desemboca siempre en el, y el ciclo tiene a lo sumo N
     * nodos) — y luego se recorre el ciclo hasta volver al punto de aterrizaje.
     */
    private static int[] extraerCiclo(int n, int[] padre, int semilla) {
        int dentro = semilla;
        for (int paso = 0; paso < n; paso++) {
            if (padre[dentro] == -1) {
                return new int[0]; // defensa: cadena rota, no hay ciclo que resaltar
            }
            dentro = padre[dentro];
        }
        List<Integer> nodos = new ArrayList<>();
        int actual = dentro;
        do {
            nodos.add(actual);
            actual = padre[actual];
        } while (actual != dentro && actual != -1);
        // La cadena de padres recorre el ciclo hacia atras; se invierte para
        // entregarlo en el sentido real de las aristas.
        java.util.Collections.reverse(nodos);
        int[] ciclo = new int[nodos.size()];
        for (int i = 0; i < ciclo.length; i++) {
            ciclo[i] = nodos.get(i);
        }
        return ciclo;
    }
}
