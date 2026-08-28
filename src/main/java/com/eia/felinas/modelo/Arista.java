package com.eia.felinas.modelo;

/**
 * Arista con peso entre dos nodos. La interpretacion depende de la mision:
 * en la Mision 3 es DIRIGIDA (desde -> hacia, enunciado seccion 5); en las
 * Misiones 2 y 4 el algoritmo la trata como bidireccional. El peso es long
 * por la regla de la seccion 2.1 (acumulados en long).
 */
public record Arista(int desde, int hacia, long peso) {
}
