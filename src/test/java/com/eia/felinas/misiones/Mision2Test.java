package com.eia.felinas.misiones;

import com.eia.felinas.parser.ErrorDeEntrada;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests de la Mision 2 (requisito 7.4: al menos un test por algoritmo,
 * usando el sample del enunciado como valor esperado).
 */
class Mision2Test {

    @Test
    void sampleDelEnunciadoProduceLaSalidaExacta() {
        String salida = Mision2.resolver(Samples.deMision(2));
        assertEquals("Case #1: 100\nCase #2: 150\nCase #3: Nina is very sad\n", salida);
    }

    @Test
    void origenIgualADestinoValeCero() {
        assertEquals("Case #1: 0\n", Mision2.resolver("1  3 1 2 2  0 1 5"));
    }

    @Test
    void seQuedaConLaConexionMasBarataEntreElMismoPar() {
        // Dos conexiones 0-1 con distinto costo: Dijkstra usa la mas barata.
        assertEquals("Case #1: 4\n", Mision2.resolver("1  2 2 0 1  0 1 9  0 1 4"));
    }

    @Test
    void grafoSinConexionesEsInalcanzable() {
        assertEquals("Case #1: Nina is very sad\n", Mision2.resolver("1  2 0 0 1"));
    }

    @Test
    void autoLazoNoRompeElCalculo() {
        // Auto-lazo en 0 con costo 5: no debe afectar la ruta 0 -> 1.
        assertEquals("Case #1: 7\n", Mision2.resolver("1  2 2 0 1  0 0 5  0 1 7"));
    }

    @Test
    void variosCasosSeNumeranDesdeUno() {
        assertEquals("Case #1: 5\nCase #2: 0\n",
                Mision2.resolver("2  2 1 0 1  0 1 5  1 0 0 0"));
    }

    @Test
    void pesoNegativoProduceErrorLegible() {
        ErrorDeEntrada error = assertThrows(ErrorDeEntrada.class,
                () -> Mision2.resolver("1  2 1 0 1  0 1 -5"));
        assertTrue(error.getMessage().contains("negativo"));
    }

    @Test
    void nodoFueraDeRangoProduceErrorLegible() {
        ErrorDeEntrada error = assertThrows(ErrorDeEntrada.class,
                () -> Mision2.resolver("1  2 1 0 1  0 5 10"));
        assertTrue(error.getMessage().contains("fuera de rango"));
    }
}
