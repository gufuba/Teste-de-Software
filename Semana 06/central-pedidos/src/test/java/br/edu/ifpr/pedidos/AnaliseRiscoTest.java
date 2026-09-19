package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnaliseRiscoTest {

    private final AnaliseRisco risco = new AnaliseRisco();

    @Test
    void totalNegativoLancaExcecao() {
        Cliente cliente = new Cliente(false, false, 0);
        assertThrows(IllegalArgumentException.class, () -> risco.avaliar(cliente, -1, false));
    }

    @Test
    void clienteBloqueadoEhSempreRecusadoIndependenteDoResto() {
        // total alto e expresso, que isoladamente gerariam REVISAO, mas bloqueado tem prioridade
        Cliente cliente = new Cliente(false, true, 0);
        assertEquals("RECUSADO", risco.avaliar(cliente, 999_999, true));
    }

    @Test
    void semComprasAnteriores_totalNoLimiteEAprovado() {
        Cliente cliente = new Cliente(false, false, 0);
        assertEquals("APROVADO", risco.avaliar(cliente, 100_000, false));
    }

    @Test
    void semComprasAnteriores_totalAcimaDoLimiteVaiParaRevisao() {
        Cliente cliente = new Cliente(false, false, 0);
        assertEquals("REVISAO", risco.avaliar(cliente, 100_001, false));
    }

    @Test
    void semComprasAnteriores_totalBaixoMasExpressoVaiParaRevisao_segundoOperandoDoOr() {
        Cliente cliente = new Cliente(false, false, 0);
        assertEquals("REVISAO", risco.avaliar(cliente, 100, true));
    }

    @Test
    void semComprasAnteriores_totalBaixoENaoExpressoEAprovado() {
        Cliente cliente = new Cliente(false, false, 0);
        assertEquals("APROVADO", risco.avaliar(cliente, 100, false));
    }

    @Test
    void comComprasAnteriores_totalNoLimiteNaoVipEAprovado() {
        Cliente cliente = new Cliente(false, false, 1);
        assertEquals("APROVADO", risco.avaliar(cliente, 500_000, false));
    }

    @Test
    void comComprasAnteriores_totalAcimaDoLimiteNaoVipVaiParaRevisao() {
        Cliente cliente = new Cliente(false, false, 1);
        assertEquals("REVISAO", risco.avaliar(cliente, 500_001, false));
    }

    @Test
    void comComprasAnteriores_totalAltoMasVipEAprovado_segundoOperandoDoAnd() {
        Cliente cliente = new Cliente(true, false, 1);
        assertEquals("APROVADO", risco.avaliar(cliente, 999_999, false));
    }
}
