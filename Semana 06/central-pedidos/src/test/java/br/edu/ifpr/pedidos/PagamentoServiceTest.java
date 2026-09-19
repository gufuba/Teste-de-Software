package br.edu.ifpr.pedidos;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PagamentoServiceTest {

    /** Stub controlável: cada chamada consome o próximo resultado da fila e registra o total recebido. */
    private static class ProcessadorStub implements ProcessadorPagamento {
        private final Deque<Object> respostas;
        final java.util.List<Long> chamadas = new java.util.ArrayList<>();

        ProcessadorStub(Object... respostas) {
            this.respostas = new ArrayDeque<>(List.of(respostas));
        }

        @Override
        public boolean autorizar(long totalCentavos) {
            chamadas.add(totalCentavos);
            Object resposta = respostas.poll();
            if (resposta instanceof RuntimeException excecao) throw excecao;
            return (Boolean) resposta;
        }
    }

    @Test
    void totalZeroLancaExcecao() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(0, 3));
    }

    @Test
    void totalNegativoLancaExcecao() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(-100, 3));
    }

    @Test
    void maxTentativasAbaixoDoLimiteLancaExcecao() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(10_000, 0));
    }

    @Test
    void maxTentativasAcimaDoLimiteLancaExcecao() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(10_000, 4));
    }

    @Test
    void aprovacaoNaPrimeiraTentativa_doWhileExecutaUmaVez() {
        ProcessadorStub stub = new ProcessadorStub(true);
        PagamentoService service = new PagamentoService(stub);
        assertTrue(service.pagar(10_000, 3));
        assertEquals(List.of(10_000L), stub.chamadas);
    }

    @Test
    void recusaDefinitivaNaoRepete() {
        ProcessadorStub stub = new ProcessadorStub(false);
        PagamentoService service = new PagamentoService(stub);
        assertFalse(service.pagar(10_000, 3));
        assertEquals(List.of(10_000L), stub.chamadas, "recusa (false) não deve gerar nova tentativa");
    }

    @Test
    void indisponibilidadeNaPrimeiraTentativaEAprovacaoNaSegunda() {
        ProcessadorStub stub = new ProcessadorStub(new IllegalStateException("indisponível"), true);
        PagamentoService service = new PagamentoService(stub);
        assertTrue(service.pagar(10_000, 3));
        assertEquals(List.of(10_000L, 10_000L), stub.chamadas);
    }

    @Test
    void duasIndisponibilidadesEAprovacaoNaTerceira_usaLimiteMaximo() {
        ProcessadorStub stub = new ProcessadorStub(
            new IllegalStateException(), new IllegalStateException(), true);
        PagamentoService service = new PagamentoService(stub);
        assertTrue(service.pagar(10_000, 3));
        assertEquals(3, stub.chamadas.size());
    }

    @Test
    void esgotarTentativasSoComIndisponibilidadeRetornaFalse() {
        ProcessadorStub stub = new ProcessadorStub(
            new IllegalStateException(), new IllegalStateException(), new IllegalStateException());
        PagamentoService service = new PagamentoService(stub);
        assertFalse(service.pagar(10_000, 3));
        assertEquals(3, stub.chamadas.size());
    }

    @Test
    void comApenasUmaTentativaPermitidaIndisponibilidadeRetornaFalseImediatamente() {
        ProcessadorStub stub = new ProcessadorStub(new IllegalStateException());
        PagamentoService service = new PagamentoService(stub);
        assertFalse(service.pagar(10_000, 1));
        assertEquals(1, stub.chamadas.size());
    }

    @Test
    void excecaoDiferenteDeIllegalStateExceptionPropaga() {
        ProcessadorStub stub = new ProcessadorStub(new RuntimeException("falha de rede"));
        PagamentoService service = new PagamentoService(stub);
        assertThrows(RuntimeException.class, () -> service.pagar(10_000, 3));
        assertEquals(1, stub.chamadas.size(), "não deve repetir para exceções que não sejam IllegalStateException");
    }
}
