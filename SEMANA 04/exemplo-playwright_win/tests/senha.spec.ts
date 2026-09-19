import { test, expect } from '@playwright/test';

async function preencherECadastrar(page, senha: string, confirmacao: string) {
  await page.goto('/senha');
  await page.getByLabel('Nova senha').fill(senha);
  await page.getByLabel('Confirmar senha').fill(confirmacao);
  await page.getByRole('button', { name: 'Cadastrar senha' }).click();
}

test.describe('cadastro de senha — formato: válidos, valores-limite e classes inválidas', () => {
  const casosFormato = [
    { senha: 'Abcdefg1', aceito: true, classe: 'limite mínimo de tamanho (8 caracteres)' },
    { senha: 'Abcdefghijklmnopqr12', aceito: true, classe: 'limite máximo de tamanho (20 caracteres)' },
    { senha: 'Abcdefg12', aceito: true, classe: 'tamanho válido dentro do intervalo' },
    { senha: 'Abcdef1', aceito: false, classe: 'abaixo do tamanho mínimo (7 caracteres)' },
    { senha: 'Abcdefghijklmnopqr123', aceito: false, classe: 'acima do tamanho máximo (21 caracteres)' },
    { senha: 'abcdefg1', aceito: false, classe: 'sem letra maiúscula' },
    { senha: 'ABCDEFG1', aceito: false, classe: 'sem letra minúscula' },
    { senha: 'Abcdefgh', aceito: false, classe: 'sem dígito' },
    { senha: 'Abcdef 1', aceito: false, classe: 'contém espaço' },
    { senha: '', aceito: false, classe: 'vazia' },
  ];

  for (const caso of casosFormato) {
    test(`senha "${caso.senha || '(vazia)'}" — ${caso.classe}`, async ({ page }) => {
      await preencherECadastrar(page, caso.senha, caso.senha);

      const resultado = page.locator('#resultado');
      await expect(resultado).toBeVisible();
      await expect(resultado).toHaveText(caso.aceito ? 'Senha cadastrada' : 'Senha fora do padrão');
      await expect(resultado).toHaveAttribute('role', caso.aceito ? 'status' : 'alert');
    });
  }
});

test.describe('cadastro de senha — confirmação e sucesso', () => {
  test('nega quando confirmação não coincide com uma senha em formato válido', async ({ page }) => {
    await preencherECadastrar(page, 'Abcdefg1', 'Abcdefg2');

    const resultado = page.locator('#resultado');
    await expect(resultado).toBeVisible();
    await expect(resultado).toHaveText('As senhas não coincidem');
    await expect(resultado).toHaveAttribute('role', 'alert');
  });

  test('cadastra com sucesso quando formato é válido e confirmação coincide', async ({ page }) => {
    await preencherECadastrar(page, 'Abcdefg1', 'Abcdefg1');

    const resultado = page.locator('#resultado');
    await expect(resultado).toBeVisible();
    await expect(resultado).toHaveText('Senha cadastrada');
    await expect(resultado).toHaveAttribute('role', 'status');

    await expect(page.getByLabel('Nova senha')).toHaveValue('');
    await expect(page.getByLabel('Confirmar senha')).toHaveValue('');
  });
});
