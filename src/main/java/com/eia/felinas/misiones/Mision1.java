package com.eia.felinas.misiones;

import com.eia.felinas.algoritmos.Bfs;
import com.eia.felinas.algoritmos.Dfs;
import com.eia.felinas.modelo.Grilla;
import com.eia.felinas.modelo.ResultadoBusqueda;
import com.eia.felinas.parser.ErrorDeEntrada;
import com.eia.felinas.parser.LectorTokens;

import java.util.ArrayList;
import java.util.List;

/**
 * Mision 1: rescatar a Nina del campo minado con BFS y DFS.
 *
 * Formato de entrada (enunciado, seccion 3): casos repetidos hasta un caso
 * con R = 0 y C = 0 que NO se procesa. Cada caso: R C, luego el numero de
 * filas con bombas y por cada una (fila, cantidad, columnas...), luego el
 * inicio (fila, columna) y el destino (fila, columna).
 *
 * Salida por caso, exacta caracter por caracter:
 *   "Case #k: BFS <b> DFS <d>"  o  "Case #k: Nina is unreachable"
 */
public final class Mision1 {

    /** Datos completos de un caso resuelto; la GUI los usa para dibujar. */
    public record Caso(int numero, Grilla grilla,
                       int filaInicio, int colInicio,
                       int filaDestino, int colDestino,
                       ResultadoBusqueda bfs, ResultadoBusqueda dfs) {
    }

    /** Salida textual exacta mas el detalle de cada caso para la visualizacion. */
    public record Resultado(String salida, List<Caso> casos) {
    }

    private Mision1() {
    }

    /** Version para tests y GUI: solo el texto de salida. */
    public static String resolver(String entrada) {
        return resolverDetallado(entrada).salida();
    }

    public static Resultado resolverDetallado(String entrada) {
        LectorTokens lector = new LectorTokens(entrada);
        StringBuilder salida = new StringBuilder();
        List<Caso> casos = new ArrayList<>();
        int numeroCaso = 1;

        while (true) {
            int filas = lector.siguienteEntero("R (numero de filas)");
            int columnas = lector.siguienteEntero("C (numero de columnas)");
            if (filas == 0 && columnas == 0) {
                break; // Terminador: este caso no se procesa.
            }
            validarRango(filas, 1, 1000, "R (numero de filas)");
            validarRango(columnas, 1, 1000, "C (numero de columnas)");

            Grilla grilla = new Grilla(filas, columnas);
            int filasConBombas = lector.siguienteEntero("numero de filas con bombas");
            validarRango(filasConBombas, 0, filas, "numero de filas con bombas");
            for (int i = 0; i < filasConBombas; i++) {
                int fila = lector.siguienteEntero("numero de fila con bombas");
                validarRango(fila, 0, filas - 1, "numero de fila con bombas");
                int cantidad = lector.siguienteEntero("cantidad de bombas de la fila " + fila);
                validarRango(cantidad, 0, columnas, "cantidad de bombas de la fila " + fila);
                for (int j = 0; j < cantidad; j++) {
                    int columna = lector.siguienteEntero("columna de bomba en la fila " + fila);
                    validarRango(columna, 0, columnas - 1, "columna de bomba en la fila " + fila);
                    grilla.ponerBomba(fila, columna);
                }
            }

            int filaInicio = lector.siguienteEntero("fila de inicio");
            int colInicio = lector.siguienteEntero("columna de inicio");
            int filaDestino = lector.siguienteEntero("fila del destino");
            int colDestino = lector.siguienteEntero("columna del destino");
            validarRango(filaInicio, 0, filas - 1, "fila de inicio");
            validarRango(colInicio, 0, columnas - 1, "columna de inicio");
            validarRango(filaDestino, 0, filas - 1, "fila del destino");
            validarRango(colDestino, 0, columnas - 1, "columna del destino");

            ResultadoBusqueda bfs = Bfs.buscar(grilla, filaInicio, colInicio, filaDestino, colDestino);
            ResultadoBusqueda dfs = Dfs.buscar(grilla, filaInicio, colInicio, filaDestino, colDestino);

            if (bfs.esAlcanzable()) {
                salida.append("Case #").append(numeroCaso)
                      .append(": BFS ").append(bfs.movimientos())
                      .append(" DFS ").append(dfs.movimientos())
                      .append('\n');
            } else {
                // Mensaje especial exacto, ASCII plano (seccion 2.2).
                salida.append("Case #").append(numeroCaso)
                      .append(": Nina is unreachable\n");
            }
            casos.add(new Caso(numeroCaso, grilla, filaInicio, colInicio,
                    filaDestino, colDestino, bfs, dfs));
            numeroCaso++;
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
