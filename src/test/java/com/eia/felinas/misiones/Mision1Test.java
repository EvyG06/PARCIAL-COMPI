package com.eia.felinas.misiones;

import com.eia.felinas.parser.ErrorDeEntrada;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests de la Mision 1 (requisito 7.4: al menos un test por algoritmo,
 * usando los samples del enunciado como valores esperados).
 */
class Mision1Test {

    @Test
    void sampleDelEnunciadoProduceLaSalidaExacta() {
        String salida = Mision1.resolver(Samples.deMision(1));
        // Comparacion caracter por caracter contra el enunciado (seccion 2.2).
        assertEquals("Case #1: BFS 18 DFS 32\n", salida);
    }

    @Test
    void inicioIgualAlDestinoCuestaCero() {
        String entrada = "3 3  0  1 1  1 1  0 0";
        assertEquals("Case #1: BFS 0 DFS 0\n", Mision1.resolver(entrada));
    }

    @Test
    void bombaEnElDestinoEsInalcanzable() {
        String entrada = "3 3  1  2 1 2  0 0  2 2  0 0";
        assertEquals("Case #1: Nina is unreachable\n", Mision1.resolver(entrada));
    }

    @Test
    void destinoEncerradoPorBombasEsInalcanzable() {
        // Bombas en (0,1), (1,1) y (1,2): los dos unicos vecinos del destino
        // (0,2) estan minados, asi que queda completamente encerrado.
        String entrada = "3 3  2  0 1 1  1 2 1 2  0 0  0 2  0 0";
        assertEquals("Case #1: Nina is unreachable\n", Mision1.resolver(entrada));
    }

    @Test
    void variosCasosSeNumeranDesdeUno() {
        String entrada = "2 2 0 0 0 1 1  2 2 0 0 0 0 0  0 0";
        assertEquals("Case #1: BFS 2 DFS 2\nCase #2: BFS 0 DFS 0\n",
                Mision1.resolver(entrada));
    }

    @Test
    void laEntradaTolerabaLineasVaciasYEspacios() {
        String entrada = "  2 2\n\n   0\n 0 0 \n\n 1 1   \n 0 0  \n";
        assertEquals("Case #1: BFS 2 DFS 2\n", Mision1.resolver(entrada));
    }

    @Test
    void grillaDeUnMillonDeCeldasNoDesbordaLaPila() {
        // 1000 x 1000 sin bombas: la razon de que el DFS use pila explicita.
        // Un DFS recursivo aqui muere con StackOverflowError (seccion 2.1).
        String entrada = "1000 1000  0  0 0  999 999  0 0";
        String salida = Mision1.resolver(entrada);
        // BFS: distancia Manhattan minima = 999 + 999 = 1998.
        assertEquals(true, salida.startsWith("Case #1: BFS 1998 DFS "));
    }

    @Test
    void entradaIncompletaProduceErrorLegible() {
        ErrorDeEntrada error = assertThrows(ErrorDeEntrada.class,
                () -> Mision1.resolver("10 10 0 0 0"));
        // El mensaje debe describir que faltaba, nunca ser un stack trace mudo.
        assertEquals(true, error.getMessage().contains("fila del destino")
                || error.getMessage().contains("termino antes de tiempo"));
    }

    @Test
    void tokenNoNumericoProduceErrorLegible() {
        assertThrows(ErrorDeEntrada.class, () -> Mision1.resolver("2 gato"));
    }
}
