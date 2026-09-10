package com.eia.felinas.misiones;

import com.eia.felinas.parser.ErrorDeEntrada;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests de la Mision 4 (requisito 7.4: al menos un test por algoritmo,
 * usando el sample del enunciado como valor esperado).
 */
class Mision4Test {

    @Test
    void sampleDelEnunciadoProduceLaSalidaExacta() {
        String salida = Mision4.resolver(Samples.deMision(4));
        assertEquals("Case #1: 55\n", salida);
    }

    @Test
    void redDesconectadaImprimeLimonCutTooManyCables() {
        // Nodos 1-2-3 conectados, el 4 aislado: no se puede reconectar todo.
        assertEquals("Case #1: Limon cut too many cables\n",
                Mision4.resolver("1  4  2  1 2 5  2 3 5"));
    }

    @Test
    void unSoloNodoSinCablesYaEstaConectado() {
        assertEquals("Case #1: 0\n", Mision4.resolver("1  1  0"));
    }

    @Test
    void cablesRepetidosSeQuedaConElMasBarato() {
        // Dos cables 1-2: Kruskal ordena por costo y usa primero el mas barato.
        assertEquals("Case #1: 4\n", Mision4.resolver("1  2  2  1 2 9  1 2 4"));
    }

    @Test
    void autoLazoSeDescartaSinAfectarElResultado() {
        assertEquals("Case #1: 7\n", Mision4.resolver("1  2  2  1 1 3  1 2 7"));
    }

    @Test
    void variosCasosSeNumeranDesdeUno() {
        assertEquals("Case #1: 55\nCase #2: 0\n",
                Mision4.resolver("2  4  5  1 2 10  2 3 20  3 4 30  4 1 40  1 3 15  1  0"));
    }

    @Test
    void interseccionFueraDeRangoProduceErrorLegible() {
        ErrorDeEntrada error = assertThrows(ErrorDeEntrada.class,
                () -> Mision4.resolver("1  2  1  0 1 5"));
        assertTrue(error.getMessage().contains("fuera de rango"));
    }
}
