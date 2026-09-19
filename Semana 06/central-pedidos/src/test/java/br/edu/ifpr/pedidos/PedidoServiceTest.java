package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedidoServiceTest {
    @Test
    void deveFecharPedidoDeClienteComumComFreteDoParanaEPagamentoAprovado() {
        // 1. Preparar: cliente comum, uma compra anterior e item disponível de R$ 100,00.
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        // Simula o pagamento e registra as cobranças, sem banco ou serviço externo.
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        // 2. Executar: percorrer um caminho completo do fechamento.
        ResultadoPedido resultado = service.fechar(pedido, cliente);

        // 3. Verificar: sem desconto; frete de R$ 12,00; total de R$ 112,00.
        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(10_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(1_200L, resultado.freteCentavos()),
            () -> assertEquals(11_200L, resultado.totalCentavos()),
            // A lista comprova uma única cobrança, com o valor correto.
            () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void pedidoNuloLancaNullPointerException() {
        PedidoService service = new PedidoService(total -> true);
        Cliente cliente = new Cliente(false, false, 0);
        assertThrows(NullPointerException.class, () -> service.fechar(null, cliente));
    }

    @Test
    void clienteNuloLancaNullPointerException() {
        PedidoService service = new PedidoService(total -> true);
        Pedido pedido = new Pedido(List.of(new ItemPedido("SKU", 1_000, 1, 5, 100, false)), "PR", false, null);
        assertThrows(NullPointerException.class, () -> service.fechar(pedido, null));
    }

    @Test
    void clienteBloqueadoRetornaBloqueadoComTudoZeroSemChamarPagamento() {
        Cliente cliente = new Cliente(false, true, 0);
        ItemPedido item = new ItemPedido("SKU", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> { cobrancas.add(total); return true; });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("BLOQUEADO", resultado.status()),
            () -> assertEquals(0L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(0L, resultado.freteCentavos()),
            () -> assertEquals(0L, resultado.totalCentavos()),
            () -> assertTrue(cobrancas.isEmpty(), "bloqueado não deve nem avaliar itens/cupom, nem cobrar")
        );
    }

    @Test
    void pedidoSemItensAtivosLancaExcecao() {
        Cliente cliente = new Cliente(false, false, 0);
        ItemPedido inativo = new ItemPedido("SKU", 10_000, 0, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(inativo), "PR", false, null);
        PedidoService service = new PedidoService(total -> true);

        assertThrows(IllegalArgumentException.class, () -> service.fechar(pedido, cliente));
    }

    @Test
    void faltaDeEstoqueRetornaSemEstoqueMesmoComCupomInvalido() {
        // Prova a ordem do contrato: estoque é avaliado antes do cupom, então um cupom
        // desconhecido não deveria nem chegar a lançar exceção aqui.
        Cliente cliente = new Cliente(false, false, 0);
        ItemPedido semEstoque = new ItemPedido("SKU", 10_000, 5, 1, 1_000, false);
        Pedido pedido = new Pedido(List.of(semEstoque), "PR", false, "CUPOM-INEXISTENTE");
        PedidoService service = new PedidoService(total -> true);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("SEM_ESTOQUE", resultado.status()),
            () -> assertEquals(0L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.totalCentavos())
        );
    }

    @Test
    void riscoEmRevisaoRetornaValoresCalculadosSemCobranca() {
        // Sem compras anteriores e total > R$ 1.000,00 aciona REVISAO antes do pagamento.
        Cliente cliente = new Cliente(false, false, 0);
        ItemPedido item = new ItemPedido("SKU", 200_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> { cobrancas.add(total); return true; });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("REVISAO", resultado.status()),
            () -> assertEquals(200_000L, resultado.subtotalCentavos()),
            () -> assertEquals(10_000L, resultado.descontoCentavos()), // 5% de 200.000
            () -> assertEquals(0L, resultado.freteCentavos()),          // líquido 190.000 >= 30.000, não expresso -> grátis
            () -> assertEquals(190_000L, resultado.totalCentavos()),
            () -> assertTrue(cobrancas.isEmpty(), "pedido em revisão não deve ser cobrado")
        );
    }

    @Test
    void pagamentoRecusadoRetornaStatusCorretoComValoresCalculados() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("SKU", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);
        PedidoService service = new PedidoService(total -> false);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("PAGAMENTO_RECUSADO", resultado.status()),
            () -> assertEquals(11_200L, resultado.totalCentavos())
        );
    }

    @Test
    void cupomExtra10AplicadoAtravesDoFechamento() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("SKU", 30_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, "EXTRA10");
        PedidoService service = new PedidoService(total -> true);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(30_000L, resultado.subtotalCentavos()),
            () -> assertEquals(3_000L, resultado.descontoCentavos()), // 10% de 30.000
            () -> assertEquals(1_200L, resultado.freteCentavos()),
            () -> assertEquals(28_200L, resultado.totalCentavos())
        );
    }

    @Test
    void clienteVipRecebeDescontoEFretePelaMetade() {
        Cliente cliente = new Cliente(true, false, 1);
        ItemPedido item = new ItemPedido("SKU", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);
        PedidoService service = new PedidoService(total -> true);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(1_000L, resultado.descontoCentavos()), // 10% VIP
            () -> assertEquals(600L, resultado.freteCentavos()),       // 1.200 / 2
            () -> assertEquals(9_600L, resultado.totalCentavos())
        );
    }

    @Test
    void indisponibilidadeTemporariaNoFechamentoTentaNovamenteAteAprovar() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("SKU", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        boolean[] primeiraChamada = { true };
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            if (primeiraChamada[0]) {
                primeiraChamada[0] = false;
                throw new IllegalStateException("indisponível");
            }
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(List.of(11_200L, 11_200L), cobrancas, "deve repetir a cobrança do mesmo total")
        );
    }

    @Test
    void itemInativoNaoContaNoSubtotalPesoOuFragilidade() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido ativo = new ItemPedido("SKU-1", 10_000, 1, 5, 1_000, false);
        ItemPedido inativoFragilPesado = new ItemPedido("SKU-2", 999_999, 0, 0, 99_999, true);
        Pedido pedido = new Pedido(List.of(ativo, inativoFragilPesado), "PR", false, null);
        PedidoService service = new PedidoService(total -> true);

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals(10_000L, resultado.subtotalCentavos()),
            () -> assertEquals(1_200L, resultado.freteCentavos()) // sem excedente de peso, sem adicional de frágil
        );
    }

    // Observação: AnaliseRisco.avaliar retorna "RECUSADO" quando cliente.bloqueado() é verdadeiro,
    // mas PedidoService.fechar já intercepta e retorna "BLOQUEADO" antes de chamar a análise de
    // risco. Esse ramo de AnaliseRisco é, portanto, coberto no teste unitário da própria classe,
    // mas é INALCANÇÁVEL a partir da colaboração via PedidoService — ver RELATORIO.md.
}
