import { test, expect } from '@playwright/test';

test.describe('cálculo de frete — caminhos válidos e valores-limite', () => {
  const casosValidos = [
    { cep: '80000000', valor: '199,99', esperado: 'Frete: R$ 15,00', classe: 'CEP inicia com 8, abaixo do limite de frete grátis' },
    { cep: '80000000', valor: '199', esperado: 'Frete: R$ 15,00', classe: 'CEP inicia com 8, valor inteiro' },
    { cep: '90000000', valor: '199,99', esperado: 'Frete: R$ 25,00', classe: 'CEP não inicia com 8, abaixo do limite de frete grátis' },
    { cep: '80000000', valor: '200', esperado: 'Frete grátis', classe: 'valor no limite mínimo do frete grátis (200,00)' },
    { cep: '90000000', valor: '200,01', esperado: 'Frete grátis', classe: 'valor acima do limite do frete grátis' },
    { cep: '80000000', valor: '0,01', esperado: 'Frete: R$ 15,00', classe: 'valor no limite mínimo válido (> 0)' },
  ];

  for (const caso of casosValidos) {
    test(`CEP ${caso.cep} / valor ${caso.valor} — ${caso.classe}`, async ({ page }) => {
      await page.goto('/frete');
      await page.getByLabel('CEP').fill(caso.cep);
      await page.getByLabel('Valor do pedido').fill(caso.valor);
      await page.getByRole('button', { name: 'Calcular frete' }).click();

      const resultado = page.locator('#resultado');
      await expect(resultado).toBeVisible();
      await expect(resultado).toHaveText(caso.esperado);
      await expect(resultado).toHaveAttribute('role', 'status');
    });
  }
});

test.describe('cálculo de frete — classes inválidas', () => {
  const casosInvalidos = [
    { cep: '8000000', valor: '100', classe: 'CEP com 7 dígitos (abaixo do exigido)' },
    { cep: '800000000', valor: '100', classe: 'CEP com 9 dígitos (acima do exigido)' },
    { cep: 'abcdefgh', valor: '100', classe: 'CEP não numérico' },
    { cep: '', valor: '100', classe: 'CEP vazio' },
    { cep: '80000000', valor: '0', classe: 'valor igual a zero (não é > 0)' },
    { cep: '80000000', valor: '-10', classe: 'valor negativo' },
    { cep: '80000000', valor: 'dez', classe: 'valor não numérico' },
    { cep: '80000000', valor: '10,999', classe: 'valor com 3 casas decimais' },
    { cep: '80000000', valor: '', classe: 'valor vazio' },
  ];

  for (const caso of casosInvalidos) {
    test(`CEP "${caso.cep}" / valor "${caso.valor}" — ${caso.classe}`, async ({ page }) => {
      await page.goto('/frete');
      await page.getByLabel('CEP').fill(caso.cep);
      await page.getByLabel('Valor do pedido').fill(caso.valor);
      await page.getByRole('button', { name: 'Calcular frete' }).click();

      const resultado = page.locator('#resultado');
      await expect(resultado).toBeVisible();
      await expect(resultado).toHaveText('Dados inválidos');
      await expect(resultado).toHaveAttribute('role', 'alert');
    });
  }
});
