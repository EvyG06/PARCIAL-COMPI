package com.eia.felinas.misiones;

import com.eia.felinas.algoritmos.BellmanFord;
import com.eia.felinas.algoritmos.FloydWarshall;
import com.eia.felinas.modelo.Arista;
import com.eia.felinas.modelo.ResultadoBellmanFord;
import com.eia.felinas.modelo.ResultadoFloydWarshall;
import com.eia.felinas.parser.ErrorDeEntrada;
import com.eia.felinas.parser.LectorTokens;

import java.util.ArrayList;
import java.util.List;

/**
 * Mision 3: el maximo churun con Floyd-Warshall y Bellman-Ford.
 *
 * Ambos algoritmos se ejecutan en CADA caso (requisito del enunciado) y sus
 * resultados para el destino se comparan entre si: alcanzabilidad, marca de
 * no acotado y valor maximo deben coincidir. La GUI usa crossCheckCoincide
 * para mostrar la advertencia de discrepancia que pide la seccion 5.2.
 *
 * Salida por caso, en este orden ESTRICTO de precedencia (seccion 5):
 *   1. "Case #k: Limon blocked the way"  si D no es alcanzable desde S.
 *   2. "Case #k: Infinite churun!"       si un ciclo de ganancia positiva
 *      alcanzable desde S puede alcanzar a D.
 *   3. "Case #k: <maximo>"               en el resto (puede ser negativo).
 * El valor impreso es la celda (S, D) de la matriz de Floyd-Warshall.
 */
public final class Mision3 {

    /** Datos completos de un caso resuelto; la GUI los usa para dibujar. */
    public record Caso(int numero, int n, List<Arista> aristas,
                       int origen, int destino,
                       ResultadoFloydWarshall floydWarshall,
                       ResultadoBellmanFord bellmanFord,
                       boolean crossCheckCoincide) {
    }

    public record Resultado(String salida, List<Caso> casos) {
    }

    private Mision3() {
    }

    /** Version para tests y GUI: solo el texto de salida. */
    public static String resolver(String entrada) {
        return resolverDetallado(entrada).salida();
    }

    public static Resultado resolverDetallado(String entrada) {
        LectorTokens lector = new LectorTokens(entrada);
        StringBuilder salida = new StringBuilder();
        List<Caso> casos = new ArrayList<>();

        int totalCasos = lector.siguienteEntero("T (numero de casos)");
        if (totalCasos < 0) {
            throw new ErrorDeEntrada("T (numero de casos) no puede ser negativo: " + totalCasos);
        }
        for (int numeroCaso = 1; numeroCaso <= totalCasos; numeroCaso++) {
            int n = lector.siguienteEntero("N (numero de nodos)");
            int m = lector.siguienteEntero("M (numero de pasadizos)");
            validarRango(n, 1, 100, "N (numero de nodos)");
            validarRango(m, 0, 5000, "M (numero de pasadizos)");
            int origen = lector.siguienteEntero("S (nodo de inicio)");
            int destino = lector.siguienteEntero("D (nodo destino)");
            validarRango(origen, 0, n - 1, "S (nodo de inicio)");
            validarRango(destino, 0, n - 1, "D (nodo destino)");

            List<Arista> aristas = new ArrayList<>(m);
            for (int i = 0; i < m; i++) {
                int desde = lector.siguienteEntero("origen del pasadizo " + (i + 1));
                int hacia = lector.siguienteEntero("destino del pasadizo " + (i + 1));
                long peso = lector.siguienteLong("churun del pasadizo " + (i + 1));
                validarRango(desde, 0, n - 1, "origen del pasadizo " + (i + 1));
                validarRango(hacia, 0, n - 1, "destino del pasadizo " + (i + 1));
                // Pasadizos repetidos: cada uno es una arista aparte (seccion 5).
                aristas.add(new Arista(desde, hacia, peso));
            }

            ResultadoFloydWarshall fw = FloydWarshall.calcular(n, aristas);
            ResultadoBellmanFord bf = BellmanFord.calcular(n, aristas, origen);
            boolean coincide = coinciden(fw, bf, origen, destino);

            salida.append("Case #").append(numeroCaso).append(": ");
            if (!fw.hayRuta(origen, destino)) {
                salida.append("Limon blocked the way");
            } else if (fw.esNoAcotado(origen, destino)) {
                salida.append("Infinite churun!");
            } else {
                salida.append(fw.maximo(origen, destino));
            }
            salida.append('\n');

            casos.add(new Caso(numeroCaso, n, aristas, origen, destino, fw, bf, coincide));
        }
        return new Resultado(salida.toString(), casos);
    }

    /**
     * Cross-check exigido por el enunciado: para el destino, Floyd-Warshall
     * y Bellman-Ford deben coincidir en alcanzabilidad, en la marca de no
     * acotado y, cuando el maximo es finito, en su valor exacto.
     */
    private static boolean coinciden(ResultadoFloydWarshall fw, ResultadoBellmanFord bf,
                                     int origen, int destino) {
        if (fw.hayRuta(origen, destino) != bf.hayRuta(destino)) {
            return false;
        }
        if (fw.esNoAcotado(origen, destino) != bf.esNoAcotado(destino)) {
            return false;
        }
        if (fw.hayRuta(origen, destino) && !fw.esNoAcotado(origen, destino)) {
            return fw.maximo(origen, destino) == bf.mejor(destino);
        }
        return true;
    }

    private static void validarRango(int valor, int minimo, int maximo, String descripcion) {
        if (valor < minimo || valor > maximo) {
            throw new ErrorDeEntrada(
                    "Valor fuera de rango para " + descripcion + ": " + valor
                    + " (se esperaba entre " + minimo + " y " + maximo + ")");
        }
    }
}
