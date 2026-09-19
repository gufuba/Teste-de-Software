package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemPedidoTest {

    // --- SKU ---

    @Test
    void skuNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido(null, 1_000, 1, 1, 100, false));
    }

    @Test
    void skuEmBrancoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("   ", 1_000, 1, 1, 100, false));
    }

    // --- Preço: limites 1 e 1_000_000 ---

    @Test
    void precoNoLimiteInferiorValido() {
        ItemPedido item = new ItemPedido("SKU1", 1, 1, 1, 100, false);
        assertEquals(1, item.precoCentavos());
    }

    @Test
    void precoAbaixoDoLimiteInferiorLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU1", 0, 1, 1, 100, false));
    }

    @Test
    void precoNoLimiteSuperiorValido() {
        ItemPedido item = new ItemPedido("SKU1", 1_000_000, 1, 1, 100, false);
        assertEquals(1_000_000, item.precoCentavos());
    }

    @Test
    void precoAcimaDoLimiteSuperiorLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU1", 1_000_001, 1, 1, 100, false));
    }

    // --- Quantidade: 0 a 100 (0 = linha inativa, ainda assim válida na construção) ---

    @Test
    void quantidadeZeroEValidaERepresentaLinhaInativa() {
        ItemPedido item = new ItemPedido("SKU1", 1_000, 0, 1, 100, false);
        assertEquals(0, item.quantidade());
    }

    @Test
    void quantidadeNoLimiteSuperiorValida() {
        ItemPedido item = new ItemPedido("SKU1", 1_000, 100, 200, 100, false);
        assertEquals(100, item.quantidade());
    }

    @Test
    void quantidadeAcimaDoLimiteSuperiorLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU1", 1_000, 101, 200, 100, false));
    }

    @Test
    void quantidadeNegativaLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU1", 1_000, -1, 200, 100, false));
    }

    // --- Estoque: não negativo ---

    @Test
    void estoqueZeroEValido() {
        ItemPedido item = new ItemPedido("SKU1", 1_000, 0, 0, 100, false);
        assertEquals(0, item.estoque());
    }

    @Test
    void estoqueNegativoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU1", 1_000, 1, -1, 100, false));
    }

    // --- Peso: 1 a 100_000 ---

    @Test
    void pesoNoLimiteInferiorValido() {
        ItemPedido item = new ItemPedido("SKU1", 1_000, 1, 1, 1, false);
        assertEquals(1, item.pesoGramas());
    }

    @Test
    void pesoAbaixoDoLimiteInferiorLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU1", 1_000, 1, 1, 0, false));
    }

    @Test
    void pesoNoLimiteSuperiorValido() {
        ItemPedido item = new ItemPedido("SKU1", 1_000, 1, 1, 100_000, false);
        assertEquals(100_000, item.pesoGramas());
    }

    @Test
    void pesoAcimaDoLimiteSuperiorLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU1", 1_000, 1, 1, 100_001, false));
    }

    // --- totalCentavos() ---

    @Test
    void totalCentavosMultiplicaPrecoPelaQuantidade() {
        ItemPedido item = new ItemPedido("SKU1", 2_500, 3, 10, 100, false);
        assertEquals(7_500L, item.totalCentavos());
    }

    @Test
    void totalCentavosEZeroQuandoQuantidadeEZero() {
        ItemPedido item = new ItemPedido("SKU1", 2_500, 0, 10, 100, false);
        assertEquals(0L, item.totalCentavos());
    }

    // --- disponivel(): quantidade <= estoque ---

    @Test
    void disponivelQuandoQuantidadeIgualAoEstoque_limite() {
        ItemPedido item = new ItemPedido("SKU1", 1_000, 5, 5, 100, false);
        assertTrue(item.disponivel());
    }

    @Test
    void disponivelQuandoQuantidadeMenorQueEstoque() {
        ItemPedido item = new ItemPedido("SKU1", 1_000, 3, 5, 100, false);
        assertTrue(item.disponivel());
    }

    @Test
    void indisponivelQuandoQuantidadeMaiorQueEstoque() {
        ItemPedido item = new ItemPedido("SKU1", 1_000, 6, 5, 100, false);
        assertFalse(item.disponivel());
    }
}
