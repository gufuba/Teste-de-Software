package br.edu.ifpr.pedidos;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculadoraFreteTest {

    private final CalculadoraFrete calculadora = new CalculadoraFrete();

    private Pedido pedido(String uf, int pesoGramas, boolean expresso, boolean fragil) {
        // um único item cujo peso*quantidade totaliza pesoGramas
        ItemPedido item = new ItemPedido("SKU", 1_000, 1, 5, pesoGramas, fragil);
        return new Pedido(List.of(item), uf, expresso, null);
    }

    private Cliente cliente(boolean vip) {
        return new Cliente(vip, false, 0);
    }

    @Test
    void valorLiquidoNegativoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> calculadora.calcular(pedido("PR", 2_000, false, false), cliente(false), -1));
    }

    // --- switch de UF ---

    @Test
    void ufParanaUsaTarifaBase() {
        assertEquals(1_200L, calculadora.calcular(pedido("PR", 2_000, false, false), cliente(false), 1_000));
    }

    @Test
    void ufSaoPauloUsaTarifaIntermediaria() {
        assertEquals(2_000L, calculadora.calcular(pedido("SP", 2_000, false, false), cliente(false), 1_000));
    }

    @Test
    void ufRioDeJaneiroUsaMesmaTarifaDeSaoPaulo() {
        assertEquals(2_000L, calculadora.calcular(pedido("RJ", 2_000, false, false), cliente(false), 1_000));
    }

    @Test
    void ufOutraUsaTarifaPadrao() {
        assertEquals(3_000L, calculadora.calcular(pedido("BA", 2_000, false, false), cliente(false), 1_000));
    }

    // --- while de peso excedente: 0, 1 e várias iterações, e fração ---

    @Test
    void pesoNoLimiteDeDoisQuilosNaoGeraAdicional_zeroIteracoes() {
        assertEquals(1_200L, calculadora.calcular(pedido("PR", 2_000, false, false), cliente(false), 1_000));
    }

    @Test
    void umGramaAcimaDoLimiteJaContaUmaIteracaoPorFracao() {
        assertEquals(1_500L, calculadora.calcular(pedido("PR", 2_001, false, false), cliente(false), 1_000));
    }

    @Test
    void umQuiloExcedenteExatoContaUmaIteracao() {
        assertEquals(1_500L, calculadora.calcular(pedido("PR", 3_000, false, false), cliente(false), 1_000));
    }

    @Test
    void fracaoAcimaDeUmQuiloExcedenteContaSegundaIteracao() {
        assertEquals(1_800L, calculadora.calcular(pedido("PR", 3_001, false, false), cliente(false), 1_000));
    }

    @Test
    void tresQuilosExcedentesContamTresIteracoes() {
        assertEquals(2_100L, calculadora.calcular(pedido("PR", 5_000, false, false), cliente(false), 1_000));
    }

    // --- frete grátis por valor líquido, condicionado a entrega não expressa ---

    @Test
    void valorLiquidoNoLimiteZeraFreteBase_entregaNormal() {
        assertEquals(0L, calculadora.calcular(pedido("BA", 2_000, false, false), cliente(false), 30_000));
    }

    @Test
    void valorLiquidoAbaixoDoLimiteNaoZeraFrete() {
        assertEquals(3_000L, calculadora.calcular(pedido("BA", 2_000, false, false), cliente(false), 29_999));
    }

    @Test
    void valorLiquidoAltoComEntregaExpressaNaoZeraFrete_segundoOperandoDoAnd() {
        assertEquals(4_500L, calculadora.calcular(pedido("BA", 2_000, true, false), cliente(false), 30_000));
    }

    // --- VIP paga metade ---

    @Test
    void clienteVipPagaMetadeDoFreteBase() {
        assertEquals(1_500L, calculadora.calcular(pedido("BA", 2_000, false, false), cliente(true), 1_000));
    }

    // --- adicionais de expresso e frágil, isolados e combinados ---

    @Test
    void entregaExpressaAcrescentaQuinzeReais() {
        assertEquals(2_700L, calculadora.calcular(pedido("PR", 2_000, true, false), cliente(false), 1_000));
    }

    @Test
    void itemFragilAcrescentaCincoReais() {
        assertEquals(1_700L, calculadora.calcular(pedido("PR", 2_000, false, true), cliente(false), 1_000));
    }

    @Test
    void expressaEFragilAcrescentamOsDoisValores() {
        assertEquals(3_200L, calculadora.calcular(pedido("PR", 2_000, true, true), cliente(false), 1_000));
    }

    @Test
    void adicionaisIncidemMesmoQuandoBaseFoiZerada() {
        assertEquals(500L, calculadora.calcular(pedido("BA", 2_000, false, true), cliente(false), 30_000));
    }

    @Test
    void combinacaoCompletaVipExpressoFragil_baseNaoZeradaPorSerExpressa() {
        // liquido>=30000 mas expresso=true: a gratuidade NÃO se aplica (2º operando do && é falso).
        // base 3.000 (BA) -> vip: /2 = 1.500 -> +1.500 (expresso) -> +500 (frágil) = 3.500
        assertEquals(3_500L, calculadora.calcular(pedido("BA", 2_000, true, true), cliente(true), 30_000));
    }
}
