package br.edu.ifpr.boletim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BoletimTest {
    private final Boletim boletim = new Boletim();

    @Test
    void deveAprovarAlunoComMediaOito() {
        // Preparar: criar o objeto que será testado.
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(8);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveRecuperarAlunoComMediaQuatro() {
        // Preparar: criar o objeto que será testado.
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(4);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("RECUPERACAO", resultado);
    }

    @Test
    void deveReprovarAlunoComMediaDois() {
        // Preparar: criar o objeto que será testado.
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(2);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("REPROVADO", resultado);
    }

    @Test
    void deveCalcularMediaCorretamente() {
        double resultado = boletim.calcularMedia(8, 6);

        assertEquals(7.0, resultado);
    }

    @Test
    void deveCalcularMediaComNotasIguais() {
        double resultado = boletim.calcularMedia(5, 5);

        assertEquals(5.0, resultado);
    }

    @Test
    void deveCalcularMediaComNotasDecimais() {
        double resultado = boletim.calcularMedia(7.5, 8.5);

        assertEquals(8.0, resultado);
    }

    @Test
    void deveContarQuantidadeDeAprovados() {
        double[] medias = {7.0, 8.0, 5.0, 3.0, 9.0};

        int resultado = boletim.contarAprovados(medias);

        assertEquals(3, resultado);
    }

    @Test
    void deveRetornarZeroQuandoNaoHaAprovados() {
        double[] medias = {3.0, 4.0, 5.0, 6.9};

        int resultado = boletim.contarAprovados(medias);

        assertEquals(0, resultado);
    }

    @Test
    void deveContarTodosQuandoTodosEstaoAprovados() {
        double[] medias = {7.0, 8.0, 9.0, 10.0};

        int resultado = boletim.contarAprovados(medias);

        assertEquals(4, resultado);
    }

    @Test
    void deveRetornarZeroParaArrayVazio() {
        double[] medias = {};

        int resultado = boletim.contarAprovados(medias);

        assertEquals(0, resultado);
    }
}
