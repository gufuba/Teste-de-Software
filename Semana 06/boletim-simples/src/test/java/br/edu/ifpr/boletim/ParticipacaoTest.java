package br.edu.ifpr.boletim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParticipacaoTest {
    private final Participacao participacao = new Participacao();

    @Test
    void deveRetornarZeroQuandoNaoEntregouENaoParticipou() {
        int resultado = participacao.calcularPontos(false, false);

        assertEquals(0, resultado);
    }

    @Test
    void deveRetornarDoisPontosQuandoEntregouAtividadeENaoParticipou() {
        int resultado = participacao.calcularPontos(true, false);

        assertEquals(2, resultado);
    }

    @Test
    void deveRetornarUmPontoQuandoNaoEntregouAtividadeMasParticipou() {
        int resultado = participacao.calcularPontos(false, true);

        assertEquals(1, resultado);
    }

    @Test
    void deveRetornarTresPontosQuandoEntregouEParticipou() {
        int resultado = participacao.calcularPontos(true, true);

        assertEquals(3, resultado);
    }
}
