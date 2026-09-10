package com.eia.felinas.misiones;

import com.eia.felinas.algoritmos.Kruskal;
import com.eia.felinas.modelo.Arista;
import com.eia.felinas.modelo.ResultadoMst;
import com.eia.felinas.parser.ErrorDeEntrada;
import com.eia.felinas.parser.LectorTokens;

import java.util.ArrayList;
import java.util.List;

/**
 * Mision 4: reconectar la red de la universidad con Kruskal.
 *
 * Formato de entrada (enunciado, seccion 6): una linea con T (numero de
 * casos). Por cada caso: una linea con N (intersecciones), una linea con C
 * (cables disponibles) y luego C lineas "inicio fin costo", con las
 * intersecciones numeradas de 1 a N. Cables duplicados y auto-lazos se
 * toleran sin fallar.
 *
 * Salida por caso, exacta caracter por caracter:
 *   "Case #k: <costo total del MST>"  o  "Case #k: Limon cut too many cables"
 */
public final class Mision4 {

    /** Datos completos de un caso resuelto; la GUI los usa para dibujar. */
    public record Caso(int numero, int n, List<Arista> cables, ResultadoMst mst) {
    }

    /** Salida textual exacta mas el detalle de cada caso para la visualizacion. */
    public record Resultado(String salida, List<Caso> casos) {
    }

    private Mision4() {
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
            int n = lector.siguienteEntero("N (numero de intersecciones)");
            validarRango(n, 1, 10_000, "N (numero de intersecciones)");
            int c = lector.siguienteEntero("C (numero de cables disponibles)");
            validarRango(c, 0, 100_000, "C (numero de cables disponibles)");

            // Las intersecciones vienen 1-indexadas (seccion 6 del enunciado);
            // se convierten a 0-indexado aqui para reutilizar el mismo modelo
            // Arista/UnionFind que el resto del proyecto.
            List<Arista> cables = new ArrayList<>(c);
            for (int i = 0; i < c; i++) {
                int inicio = lector.siguienteEntero("interseccion de inicio del cable " + (i + 1));
                int fin = lector.siguienteEntero("interseccion de fin del cable " + (i + 1));
                long costo = lector.siguienteLong("costo del cable " + (i + 1));
                validarRango(inicio, 1, n, "interseccion de inicio del cable " + (i + 1));
                validarRango(fin, 1, n, "interseccion de fin del cable " + (i + 1));
                cables.add(new Arista(inicio - 1, fin - 1, costo));
            }

            ResultadoMst mst = Kruskal.calcular(n, cables);

            salida.append("Case #").append(numeroCaso).append(": ");
            if (mst.conectado()) {
                salida.append(mst.costoTotal());
            } else {
                // Mensaje especial exacto, ASCII plano (seccion 2.2).
                salida.append("Limon cut too many cables");
            }
            salida.append('\n');

            casos.add(new Caso(numeroCaso, n, cables, mst));
        }
        return new Resultado(salida.toString(), casos);
    }

    private static void validarRango(int valor, int minimo, int maximo, String descripcion) {
        if (valor < minimo || valor > maximo) {
            throw new ErrorDeEntrada(
                    "Valor fuera de rango para " + descripcion + ": " + valor
                    + " (se esperaba entre " + minimo + " y " + maximo + ")");
        }
    }
}
