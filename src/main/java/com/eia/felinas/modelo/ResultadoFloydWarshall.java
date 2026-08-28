package com.eia.felinas.modelo;

/**
 * Resultado de Floyd-Warshall maximizante: la matriz N x N de maximos entre
 * todos los pares y la marca de pares no acotados (churun infinito).
 *
 * La GUI muestra la matriz con el formato que exige el enunciado (seccion
 * 5.1): "-" para pares sin ruta, "inf" para pares no acotados y el numero en
 * el resto. Ese formato vive aqui en textoCelda para que la GUI no tenga que
 * conocer el centinela.
 */
public final class ResultadoFloydWarshall {

    private final long[][] maximos;
    private final boolean[][] noAcotado;

    public ResultadoFloydWarshall(long[][] maximos, boolean[][] noAcotado) {
        this.maximos = maximos;
        this.noAcotado = noAcotado;
    }

    public int n() {
        return maximos.length;
    }

    public boolean hayRuta(int desde, int hacia) {
        return maximos[desde][hacia] != Pesos.SIN_RUTA;
    }

    public boolean esNoAcotado(int desde, int hacia) {
        return noAcotado[desde][hacia];
    }

    /** Maximo churun entre el par; solo valido si hayRuta y no esNoAcotado. */
    public long maximo(int desde, int hacia) {
        return maximos[desde][hacia];
    }

    /** Texto de la celda (i, j) de la matriz, con el formato del enunciado. */
    public String textoCelda(int desde, int hacia) {
        if (noAcotado[desde][hacia]) {
            return "inf";
        }
        if (!hayRuta(desde, hacia)) {
            return "-";
        }
        return String.valueOf(maximos[desde][hacia]);
    }
}
