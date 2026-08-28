package com.eia.felinas.misiones;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Carga los samples del enunciado empaquetados como recursos en
 * /samples/misionN.txt. Los usan el boton "cargar sample" de la GUI
 * (requisito 7.1) y los tests automatizados (requisito 7.4), de modo que
 * ambos trabajan siempre con exactamente el mismo texto.
 */
public final class Samples {

    private Samples() {
    }

    /** Devuelve el sample de la mision indicada (1 a 4). */
    public static String deMision(int numeroMision) {
        String recurso = "/samples/mision" + numeroMision + ".txt";
        try (InputStream flujo = Samples.class.getResourceAsStream(recurso)) {
            if (flujo == null) {
                throw new IllegalStateException("No existe el recurso " + recurso);
            }
            return new String(flujo.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el recurso " + recurso, e);
        }
    }
}
