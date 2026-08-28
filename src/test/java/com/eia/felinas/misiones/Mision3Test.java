package com.eia.felinas.misiones;

import com.eia.felinas.parser.ErrorDeEntrada;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests de la Mision 3: Floyd-Warshall y Bellman-Ford maximizantes.
 * El sample del enunciado cubre los tres tipos de respuesta: maximo finito,
 * churun infinito y maximo negativo.
 */
class Mision3Test {

    @Test
    void sampleDelEnunciadoProduceLaSalidaExacta() {
        String salida = Mision3.resolver(Samples.deMision(3));
        assertEquals("Case #1: 110\nCase #2: Infinite churun!\nCase #3: -65\n", salida);
    }

    @Test
    void elCrossCheckCoincideEnTodosLosCasosDelSample() {
        Mision3.Resultado resultado = Mision3.resolverDetallado(Samples.deMision(3));
        for (Mision3.Caso caso : resultado.casos()) {
            assertTrue(caso.crossCheckCoincide(),
                    "Discrepancia FW/BF en el caso " + caso.numero());
        }
    }

    @Test
    void destinoInalcanzableImprimeLimonBlockedTheWay() {
        // Dos nodos sin aristas entre ellos.
        assertEquals("Case #1: Limon blocked the way\n",
                Mision3.resolver("1  2 0 0 1"));
    }

    @Test
    void origenIgualADestinoValeCero() {
        assertEquals("Case #1: 0\n", Mision3.resolver("1  3 1 2 2  0 1 5"));
    }

    @Test
    void cicloPositivoQueNoAlcanzaAlDestinoNoEsInfinito() {
        // 0 -> 1 (10) y 1 -> 1 (+5): ciclo positivo alcanzable desde 0.
        // 0 -> 2 (7) es la unica via al destino 2; el ciclo NO alcanza a 2.
        // Criterio de la seccion 5.2: la respuesta debe ser finita (7).
        assertEquals("Case #1: 7\n",
                Mision3.resolver("1  3 3 0 2  0 1 10  1 1 5  0 2 7"));
    }

    @Test
    void cicloPositivoQueSiAlcanzaAlDestinoEsInfinito() {
        // Mismo grafo pero con el puente 1 -> 2: el ciclo ahora alcanza a D.
        assertEquals("Case #1: Infinite churun!\n",
                Mision3.resolver("1  3 4 0 2  0 1 10  1 1 5  0 2 7  1 2 1"));
    }

    @Test
    void pasadizosRepetidosSeTratanComoAristasSeparadas() {
        // Dos pasadizos 0 -> 1 con distinto churun: gana el de 9.
        assertEquals("Case #1: 9\n",
                Mision3.resolver("1  2 2 0 1  0 1 4  0 1 9"));
    }

    @Test
    void laCaminataPuedeRepetirNodosSinCicloPositivo() {
        // Ciclo 1 -> 2 -> 1 de ganancia total 0: repetirlo no ayuda ni
        // impide terminar; el maximo sigue siendo finito.
        assertEquals("Case #1: 25\n",
                Mision3.resolver("1  3 4 0 2  0 1 10  1 2 15  2 1 -15  1 2 15"));
    }

    @Test
    void laMatrizUsaGuionParaSinRutaEInfParaNoAcotado() {
        Mision3.Resultado resultado = Mision3.resolverDetallado(
                "1  3 3 0 2  0 1 10  1 1 5  0 2 7");
        Mision3.Caso caso = resultado.casos().get(0);
        assertEquals("inf", caso.floydWarshall().textoCelda(0, 1)); // via ciclo en 1
        assertEquals("7", caso.floydWarshall().textoCelda(0, 2));   // finito
        assertEquals("-", caso.floydWarshall().textoCelda(2, 0));   // sin ruta
    }

    @Test
    void bellmanFordEntregaUnCicloParaResaltar() {
        Mision3.Resultado resultado = Mision3.resolverDetallado(
                "1  3 4 0 2  0 1 10  1 1 5  0 2 7  1 2 1");
        int[] ciclo = resultado.casos().get(0).bellmanFord().cicloResponsable();
        // El unico ciclo positivo es el lazo del nodo 1.
        assertArrayEquals(new int[] {1}, ciclo);
    }

    @Test
    void nodoFueraDeRangoProduceErrorLegible() {
        ErrorDeEntrada error = assertThrows(ErrorDeEntrada.class,
                () -> Mision3.resolver("1  3 1 0 2  0 7 10"));
        assertTrue(error.getMessage().contains("fuera de rango"));
    }

    @Test
    void casoSinAristasYSinRutaNoEsNoAcotado() {
        Mision3.Resultado resultado = Mision3.resolverDetallado("1  2 0 0 1");
        Mision3.Caso caso = resultado.casos().get(0);
        assertFalse(caso.bellmanFord().esNoAcotado(1));
        assertFalse(caso.floydWarshall().esNoAcotado(0, 1));
        assertTrue(caso.crossCheckCoincide());
    }
}
