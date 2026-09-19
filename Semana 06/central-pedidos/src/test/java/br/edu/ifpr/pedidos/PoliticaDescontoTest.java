package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PoliticaDescontoTest {

    private final PoliticaDesconto politica = new PoliticaDesconto();

    private Cliente clienteComum(int comprasAnteriores) {
        return new Cliente(false, false, comprasAnteriores);
    }

    private Cliente clienteVip(int comprasAnteriores) {
        return new Cliente(true, false, comprasAnteriores);
    }

    @Test
    void subtotalNegativoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> politica.calcular(clienteComum(0), -1, null));
    }

    @Test
    void vipRecebeDezPorCentoSemCupom() {
        assertEquals(10_000L, politica.calcular(clienteVip(0), 100_000, null));
    }

    @Test
    void clienteComumAbaixoDoLimiarDeCincoPorCentoNaoTemDesconto() {
        assertEquals(0L, politica.calcular(clienteComum(0), 49_999, null));
    }

    @Test
    void clienteComumNoLimiteExatoDeCincoPorCento_limiteInferior() {
        assertEquals(2_500L, politica.calcular(clienteComum(0), 50_000, null));
    }

    @Test
    void clienteComumAcimaDoLimiarDeCincoPorCento() {
        assertEquals(2_500L, politica.calcular(clienteComum(0), 50_001, null));
    }

    @Test
    void cupomEmBrancoMantemApenasODescontoBase() {
        assertEquals(2_500L, politica.calcular(clienteComum(0), 50_000, "   "));
    }

    @Test
    void cupomBemvindoElegivelNoLimiteDeSubtotal_semComprasAnteriores() {
        // sem compras anteriores + subtotal no limite exato (10.000) => +20,00
        assertEquals(2_000L, politica.calcular(clienteComum(0), 10_000, "bemvindo"));
    }

    @Test
    void cupomBemvindoAbaixoDoLimiteDeSubtotalNaoAplica() {
        assertEquals(0L, politica.calcular(clienteComum(0), 9_999, "BEMVINDO"));
    }

    @Test
    void cupomBemvindoComComprasAnterioresNaoElegivel() {
        // segundo operando do && (compras==0) é falso: o subtotal>=10000 não chega a ser avaliado
        assertEquals(0L, politica.calcular(clienteComum(1), 10_000, "BEMVINDO"));
    }

    @Test
    void cupomExtra10ElegivelNoLimiteDeSubtotal() {
        assertEquals(2_000L, politica.calcular(clienteComum(0), 20_000, "extra10"));
    }

    @Test
    void cupomExtra10AbaixoDoLimiteNaoAplica() {
        assertEquals(0L, politica.calcular(clienteComum(0), 19_999, "EXTRA10"));
    }

    @Test
    void cupomDesconhecidoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> politica.calcular(clienteComum(0), 10_000, "INVALIDO"));
    }

    @Test
    void descontoCombinadoRespeitaTetoDeVintePorCento() {
        // vip (10% = 1.000) + BEMVINDO (+2.000) = 3.000, mas teto é 20% de 10.000 = 2.000
        assertEquals(2_000L, politica.calcular(clienteVip(0), 10_000, "BEMVINDO"));
    }
}
