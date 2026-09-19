package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    private ItemPedido item(long preco, int qtd, int estoque, int peso, boolean fragil) {
        return new ItemPedido("SKU", preco, qtd, estoque, peso, fragil);
    }

    // --- Validação da lista de itens ---

    @Test
    void listaDeItensNulaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null, "PR", false, null));
    }

    @Test
    void listaComAteCemItensEValida_limiteSuperior() {
        List<ItemPedido> itens = new ArrayList<>();
        for (int i = 0; i < 100; i++) itens.add(item(1_000, 1, 5, 100, false));
        Pedido pedido = new Pedido(itens, "PR", false, null);
        assertEquals(100, pedido.itens().size());
    }

    @Test
    void listaComMaisDeCemItensLancaExcecao() {
        List<ItemPedido> itens = new ArrayList<>();
        for (int i = 0; i < 101; i++) itens.add(item(1_000, 1, 5, 100, false));
        assertThrows(IllegalArgumentException.class, () -> new Pedido(itens, "PR", false, null));
    }

    @Test
    void listaVaziaEValidaNaConstrucao() {
        Pedido pedido = new Pedido(List.of(), "PR", false, null);
        assertTrue(pedido.itens().isEmpty());
    }

    @Test
    void listaECopiadaDefensivamente() {
        List<ItemPedido> original = new ArrayList<>();
        original.add(item(1_000, 1, 5, 100, false));
        Pedido pedido = new Pedido(original, "PR", false, null);
        original.add(item(2_000, 1, 5, 100, false));
        assertEquals(1, pedido.itens().size(), "alterar a lista original não deve afetar o pedido");
    }

    // --- Validação de UF ---

    @Test
    void ufComDuasLetrasMaiusculasEValida() {
        Pedido pedido = new Pedido(List.of(), "SP", false, null);
        assertEquals("SP", pedido.uf());
    }

    @Test
    void ufMinusculaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "sp", false, null));
    }

    @Test
    void ufComUmaLetraLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "S", false, null));
    }

    @Test
    void ufComTresLetrasLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "SPX", false, null));
    }

    @Test
    void ufComDigitosLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "S1", false, null));
    }

    @Test
    void ufNulaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), null, false, null));
    }

    // --- subtotalCentavos(): laço com continue para itens inativos ---

    @Test
    void subtotalSomaApenasItensAtivos() {
        Pedido pedido = new Pedido(List.of(
            item(10_000, 1, 5, 100, false),
            item(5_000, 0, 5, 100, false) // inativo, quantidade 0
        ), "PR", false, null);
        assertEquals(10_000L, pedido.subtotalCentavos());
    }

    @Test
    void subtotalDeListaVaziaEZero() {
        Pedido pedido = new Pedido(List.of(), "PR", false, null);
        assertEquals(0L, pedido.subtotalCentavos());
    }

    @Test
    void subtotalComVariosItensAtivosSoma() {
        Pedido pedido = new Pedido(List.of(
            item(10_000, 2, 5, 100, false),
            item(3_000, 3, 5, 100, false)
        ), "PR", false, null);
        assertEquals(29_000L, pedido.subtotalCentavos());
    }

    // --- pesoGramas(): soma peso*quantidade; item inativo (qtd 0) contribui zero automaticamente ---

    @Test
    void pesoMultiplicaPesoUnitarioPelaQuantidadeESomaItensAtivos() {
        Pedido pedido = new Pedido(List.of(
            item(10_000, 2, 5, 500, false),  // 500*2 = 1000
            item(5_000, 0, 5, 300, false)    // inativo: 300*0 = 0
        ), "PR", false, null);
        assertEquals(1_000, pedido.pesoGramas());
    }

    // --- temFragil(): retorno antecipado ao achar o primeiro frágil ativo ---

    @Test
    void temFragilTrueQuandoAlgumItemAtivoEhFragil() {
        Pedido pedido = new Pedido(List.of(
            item(10_000, 1, 5, 100, false),
            item(5_000, 1, 5, 100, true)
        ), "PR", false, null);
        assertTrue(pedido.temFragil());
    }

    @Test
    void temFragilFalseQuandoItemFragilEstaInativo() {
        Pedido pedido = new Pedido(List.of(
            item(5_000, 0, 5, 100, true) // frágil, mas quantidade 0
        ), "PR", false, null);
        assertFalse(pedido.temFragil());
    }

    @Test
    void temFragilFalseQuandoNenhumItemEhFragil() {
        Pedido pedido = new Pedido(List.of(item(10_000, 1, 5, 100, false)), "PR", false, null);
        assertFalse(pedido.temFragil());
    }

    // --- estoqueSuficiente(): laço com break no primeiro item insuficiente ---

    @Test
    void estoqueSuficienteTrueQuandoTodosOsItensTemEstoque() {
        Pedido pedido = new Pedido(List.of(
            item(10_000, 2, 5, 100, false),
            item(5_000, 1, 1, 100, false)
        ), "PR", false, null);
        assertTrue(pedido.estoqueSuficiente());
    }

    @Test
    void estoqueSuficienteFalseNoPrimeiroItemInsuficiente() {
        Pedido pedido = new Pedido(List.of(
            item(10_000, 6, 5, 100, false), // insuficiente, deve interromper o laço aqui
            item(5_000, 1, 5, 100, false)
        ), "PR", false, null);
        assertFalse(pedido.estoqueSuficiente());
    }

    @Test
    void estoqueSuficienteFalseQuandoItemAoFinalDaListaFalha() {
        Pedido pedido = new Pedido(List.of(
            item(10_000, 1, 5, 100, false),
            item(5_000, 1, 5, 100, false),
            item(3_000, 10, 1, 100, false) // insuficiente, último da lista
        ), "PR", false, null);
        assertFalse(pedido.estoqueSuficiente());
    }
}
