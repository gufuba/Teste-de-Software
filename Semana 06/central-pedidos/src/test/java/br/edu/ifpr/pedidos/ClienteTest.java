package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void criaClienteComHistoricoZero_limiteInferiorValido() {
        Cliente cliente = new Cliente(false, false, 0);
        assertAll(
            () -> assertFalse(cliente.vip()),
            () -> assertFalse(cliente.bloqueado()),
            () -> assertEquals(0, cliente.comprasAnteriores())
        );
    }

    @Test
    void criaClienteVipEBloqueadoComHistoricoPositivo() {
        Cliente cliente = new Cliente(true, true, 42);
        assertAll(
            () -> assertTrue(cliente.vip()),
            () -> assertTrue(cliente.bloqueado()),
            () -> assertEquals(42, cliente.comprasAnteriores())
        );
    }

    @Test
    void historicoNegativoLancaExcecao_limiteInferiorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente(false, false, -1));
    }
}
