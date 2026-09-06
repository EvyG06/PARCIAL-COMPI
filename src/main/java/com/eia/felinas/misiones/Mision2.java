package com.eia.felinas.misiones;

import com.eia.felinas.algoritmos.Dijkstra;
import com.eia.felinas.modelo.Arista;
import com.eia.felinas.modelo.ResultadoDijkstra;
import com.eia.felinas.parser.ErrorDeEntrada;
import com.eia.felinas.parser.LectorTokens;

import java.util.ArrayList;
import java.util.List;

/**
 * Mision 2: recuperar las cuentas de Claude con el algoritmo de Dijkstra.
 *
 * Formato de entrada (enunciado, seccion 4): una linea con T (numero de
 * casos). Por cada caso: una linea "N C S D", luego C lineas "A B W" (una
 * conexion bidireccional entre A y B con costo W). Conexiones repetidas
 * entre el mismo par y auto-lazos se toleran sin fallar.
 *
 * Salida por caso, exacta caracter por caracter:
 *   "Case #k: <costo>"  o  "Case #k: Nina is very sad"
 * Si S = D la respuesta es 0 (no requiere caso especial: la distancia del
 * origen a si mismo ya es 0 en Dijkstra).
 */
public final class Mision2 {

    /** Datos completos de un caso resuelto; la GUI los usa para dibujar. */
    public record Caso(int numero, int n, List<Arista> aristas,
                       int origen, int destino, ResultadoDijkstra dijkstra) {
    }

    /** Salida textual exacta mas el detalle de cada caso para la visualizacion. */
    public record Resultado(String salida, List<Caso> casos) {
    }

    private Mision2() {
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
            int c = lector.siguienteEntero("C (numero de conexiones)");
            validarRango(n, 1, 10_000, "N (numero de nodos)");
            validarRango(c, 0, 100_000, "C (numero de conexiones)");
            int origen = lector.siguienteEntero("S (nodo de inicio)");
            int destino = lector.siguienteEntero("D (nodo destino)");
            validarRango(origen, 0, n - 1, "S (nodo de inicio)");
            validarRango(destino, 0, n - 1, "D (nodo destino)");

            List<Arista> aristas = new ArrayList<>(c);
            for (int i = 0; i < c; i++) {
                int a = lector.siguienteEntero("extremo A de la conexion " + (i + 1));
                int b = lector.siguienteEntero("extremo B de la conexion " + (i + 1));
                long peso = lector.siguienteLong("costo de la conexion " + (i + 1));
                validarRango(a, 0, n - 1, "extremo A de la conexion " + (i + 1));
                validarRango(b, 0, n - 1, "extremo B de la conexion " + (i + 1));
                if (peso < 0) {
                    // Dijkstra exige pesos no negativos (enunciado, seccion 4):
                    // se rechaza aqui en vez de producir un resultado silenciosamente incorrecto.
                    throw new ErrorDeEntrada(
                            "El peso de la conexion " + (i + 1) + " es negativo: " + peso
                            + " (Dijkstra exige pesos no negativos, seccion 4 del enunciado)");
                }
                // Repetidas y auto-lazos se guardan todas: Dijkstra se queda
                // con la mas barata al relajar, sin necesidad de deduplicar antes.
                aristas.add(new Arista(a, b, peso));
            }

            ResultadoDijkstra resultado = Dijkstra.calcular(n, aristas, origen);

            salida.append("Case #").append(numeroCaso).append(": ");
            if (resultado.hayRuta(destino)) {
                salida.append(resultado.distancia(destino));
            } else {
                // Mensaje especial exacto, ASCII plano (seccion 2.2).
                salida.append("Nina is very sad");
            }
            salida.append('\n');

            casos.add(new Caso(numeroCaso, n, aristas, origen, destino, resultado));
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
