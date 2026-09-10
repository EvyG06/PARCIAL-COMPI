package com.eia.felinas.gui.viz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests de los umbrales de dibujo (seccion 2.3 del enunciado). Son logica
 * pura, sin JavaFX, asi que no requieren abrir ninguna ventana.
 */
class LimitesVisualizacionTest {

    @Test
    void grillaDe50x50EstaEnElLimiteYSiSeDibuja() {
        assertTrue(LimitesVisualizacion.debeDibujarGrilla(50, 50));
    }

    @Test
    void grillaDe51EnCualquierDimensionYaNoSeDibuja() {
        assertFalse(LimitesVisualizacion.debeDibujarGrilla(51, 50));
        assertFalse(LimitesVisualizacion.debeDibujarGrilla(50, 51));
    }

    @Test
    void grafoDe60NodosSiSeDibujaDe61No() {
        assertTrue(LimitesVisualizacion.debeDibujarGrafo(60));
        assertFalse(LimitesVisualizacion.debeDibujarGrafo(61));
    }

    @Test
    void redDeMision4RespetaAmbosLimitesIndependientes() {
        assertTrue(LimitesVisualizacion.debeDibujarRedMision4(100, 300));
        assertFalse(LimitesVisualizacion.debeDibujarRedMision4(101, 300));
        assertFalse(LimitesVisualizacion.debeDibujarRedMision4(100, 301));
    }

    @Test
    void laMatrizDeFloydWarshallSiempreSeMuestraHastaN100() {
        assertTrue(LimitesVisualizacion.debeDibujarMatriz(1));
        assertTrue(LimitesVisualizacion.debeDibujarMatriz(100));
    }

    @Test
    void elMensajeDeOmisionMencionaLaInstancia() {
        String mensaje = LimitesVisualizacion.mensajeOmitido("la grilla de 200 x 200");
        assertTrue(mensaje.contains("200 x 200"));
    }
}
