**Casos de Teste - Sistema de Aluguel de Salas**

---

## CT-01 - Reservar sala disponível para turma compatível (RF-01)

**Descrição:** Verificar se o sistema permite reservar uma sala disponível cuja capacidade e recursos sejam compatíveis com a turma selecionada.

**Pré-condições:** O usuário está autenticado no sistema e possui permissão para reservar salas na unidade acessada.

**Passos:**

1. Acessar a página de reserva de salas.
2. Selecionar a data e o horário desejados.
3. Selecionar a sala disponível.
4. Selecionar a turma e o responsável.
5. Clicar no botão "Salvar".

**Cenário 1 - Reserva bem-sucedida:**

- Dados de Teste:
  - Sala: "Sala 101" (capacidade 30, recursos: projetor)
  - Turma: "Turma A" (25 alunos, requer projetor)
  - Data/Horário: 21/08/2026, 08h00–10h00
  - Responsável: "Prof. João Silva"

- Resultado Esperado:
  - O sistema confirma a reserva e a exibe na lista de reservas do usuário.
  - A sala passa a constar como ocupada no intervalo reservado.

**Cenário 2 - Turma incompatível com recursos da sala:**

- Dados de Teste:
  - Sala: "Sala 102" (sem projetor)
  - Turma: "Turma B" (requer projetor)

- Resultado Esperado:
  - O sistema exibe uma mensagem informando que a sala não possui os recursos exigidos pela turma.
  - A reserva não é criada.

**Cenário 3 - Sala inexistente ou não selecionada:**

- Dados de Teste:
  - Sala: (campo em branco)

- Resultado Esperado:
  - O sistema exibe uma mensagem de erro indicando que a seleção de sala é obrigatória.
  - A reserva não é criada.

**Pós-condições:** A reserva é criada e visível na agenda da sala, ou o usuário permanece na tela de reserva com a mensagem de erro correspondente.

---

## CT-02 - Impedir sobreposição de horário na mesma sala (RF-02 / Risco: dupla ocupação)

**Descrição:** Verificar se o sistema impede que a mesma sala seja reservada por dois usuários em horários que se sobrepõem.

**Pré-condições:** Existe uma reserva confirmada para a "Sala 101" no dia 21/08/2026 das 08h00 às 10h00.

**Passos:**

1. Acessar a página de reserva de salas.
2. Selecionar a mesma sala já reservada.
3. Selecionar uma data/horário que conflite com a reserva existente.
4. Clicar no botão "Salvar".

**Cenário 1 - Sobreposição total de horário:**

- Dados de Teste:
  - Sala: "Sala 101"
  - Data/Horário: 21/08/2026, 08h00–10h00

- Resultado Esperado:
  - O sistema exibe uma mensagem de erro indicando conflito de horário.
  - A nova reserva não é criada e a reserva original permanece inalterada.

**Cenário 2 - Sobreposição parcial de horário:**

- Dados de Teste:
  - Sala: "Sala 101"
  - Data/Horário: 21/08/2026, 09h00–11h00

- Resultado Esperado:
  - O sistema exibe uma mensagem de erro indicando conflito de horário.
  - A nova reserva não é criada.

---

## CT-03 - Impedir turma maior que a capacidade da sala (RF-03 / Risco: capacidade insegura)

**Descrição:** Verificar se o sistema impede a reserva quando o número de alunos da turma excede a capacidade da sala selecionada.

**Pré-condições:** Usuário autenticado com permissão para criar reservas.

**Passos:**

1. Acessar a página de reserva de salas.
2. Selecionar uma sala.
3. Selecionar uma turma.
4. Clicar no botão "Salvar".

**Cenário 1 - Turma maior que a capacidade:**

- Dados de Teste:
  - Sala: "Sala 103" (capacidade 20)
  - Turma: "Turma C" (28 alunos)

- Resultado Esperado:
  - O sistema exibe uma mensagem de erro informando que a capacidade da sala foi excedida.
  - A reserva não é criada.

**Cenário 2 - Turma igual à capacidade da sala:**

- Dados de Teste:
  - Sala: "Sala 103" (capacidade 20)
  - Turma: "Turma D" (20 alunos)

- Resultado Esperado:
  - O sistema permite a reserva normalmente.

**Cenário 3 - Turma menor que a capacidade da sala:**

- Dados de Teste:
  - Sala: "Sala 103" (capacidade 20)
  - Turma: "Turma E" (15 alunos)

- Resultado Esperado:
  - O sistema permite a reserva normalmente.

**Pós-condições:** Nenhuma reserva com turma acima da capacidade é registrada no sistema.

---

## CT-04 - Bloquear sala em manutenção (RF-04)

**Descrição:** Verificar se o sistema impede reservas em salas marcadas como em manutenção durante o período correspondente.

**Pré-condições:** A "Sala 104" está com manutenção agendada para 21/08/2026, das 08h00 às 18h00.

**Passos:**

1. Acessar a página de reserva de salas.
2. Selecionar a sala em manutenção.
3. Selecionar um horário dentro do período de manutenção.
4. Clicar no botão "Salvar".

**Cenário 1 - Tentativa de reserva durante a manutenção:**

- Dados de Teste:
  - Sala: "Sala 104"
  - Data/Horário: 21/08/2026, 09h00–10h00

- Resultado Esperado:
  - O sistema exibe uma mensagem informando que a sala está em manutenção e indisponível para reserva.
  - A reserva não é criada.

**Cenário 2 - Reserva fora do período de manutenção:**

- Dados de Teste:
  - Sala: "Sala 104"
  - Data/Horário: 22/08/2026, 09h00–10h00

- Resultado Esperado:
  - O sistema permite a reserva normalmente, pois a manutenção não abrange essa data/horário.

**Pós-condições:** Nenhuma reserva é registrada para salas em manutenção durante o período bloqueado.

---

## CT-05 - Permitir reservas apenas entre 07h30 e 22h30 (RF-05)

**Descrição:** Verificar se o sistema restringe as reservas ao intervalo de funcionamento permitido (07h30 às 22h30).

**Pré-condições:** Usuário autenticado com permissão para criar reservas.

**Passos:**

1. Acessar a página de reserva de salas.
2. Selecionar sala, data e horário.
3. Clicar no botão "Salvar".

**Cenário 1 - Horário dentro do intervalo permitido:**

- Dados de Teste:
  - Data/Horário: 21/08/2026, 10h00–11h00

- Resultado Esperado:
  - O sistema permite a reserva normalmente.

**Cenário 2 - Horário de início antes das 07h30:**

- Dados de Teste:
  - Data/Horário: 21/08/2026, 06h00–08h00

- Resultado Esperado:
  - O sistema exibe uma mensagem de erro informando que o horário está fora do funcionamento permitido.
  - A reserva não é criada.

**Cenário 3 - Horário de término após as 22h30:**

- Dados de Teste:
  - Data/Horário: 21/08/2026, 22h00–23h00

- Resultado Esperado:
  - O sistema exibe uma mensagem de erro informando que o horário está fora do funcionamento permitido.
  - A reserva não é criada.

---

## CT-06 - Restringir alteração de reserva de outro professor (RF-06 / Risco: alteração sem autorização)

**Descrição:** Verificar se apenas a coordenação pode alterar reservas feitas por outros professores, enquanto um professor comum só pode alterar as próprias reservas.

**Pré-condições:** Existe uma reserva feita pelo "Prof. João Silva". Os usuários "Prof. Maria Souza" (professora) e "Coordenação Acadêmica" (coordenação) estão autenticados em sessões distintas.

**Passos:**

1. Acessar a lista de reservas.
2. Selecionar a reserva do "Prof. João Silva".
3. Tentar alterar data, horário ou sala da reserva.
4. Clicar no botão "Salvar".

**Cenário 1 - Professor tenta alterar a própria reserva:**

- Dados de Teste:
  - Usuário: "Prof. João Silva"
  - Reserva: própria reserva

- Resultado Esperado:
  - O sistema permite a alteração normalmente.

**Cenário 2 - Professor tenta alterar reserva de outro professor:**

- Dados de Teste:
  - Usuário: "Prof. Maria Souza"
  - Reserva: reserva do "Prof. João Silva"

- Resultado Esperado:
  - O sistema bloqueia a alteração e exibe uma mensagem informando que o usuário não tem permissão.
  - A reserva original permanece inalterada.


---

## CT-07 - Cancelamento de reserva libera horário e registra histórico (RF-07)

**Descrição:** Verificar se o cancelamento de uma reserva libera o horário da sala para novas reservas e registra o cancelamento no histórico.

**Pré-condições:** Existe uma reserva confirmada para a "Sala 105" em 21/08/2026, das 14h00 às 16h00.

**Passos:**

1. Acessar a lista de reservas.
2. Selecionar a reserva a ser cancelada.
3. Clicar no botão "Cancelar".
4. Confirmar o cancelamento.

**Cenário 1 - Cancelamento bem-sucedido:**

- Dados de Teste:
  - Reserva: "Sala 105", 21/08/2026, 14h00–16h00

- Resultado Esperado:
  - A reserva é removida da agenda ativa da sala.
  - O histórico do sistema registra o cancelamento com data, hora e usuário responsável.

**Cenário 2 - Horário liberado disponível para nova reserva:**

- Dados de Teste:
  - Nova reserva: "Sala 105", 21/08/2026, 14h00–16h00 (mesmo horário do cancelamento)

- Resultado Esperado:
  - O sistema permite que outro usuário reserve a sala nesse mesmo horário, sem indicar conflito.

**Pós-condições:** A sala aparece como disponível no horário anteriormente ocupado, e o histórico contém o registro do cancelamento.

---

## CT-08 - Notificação em alterações e cancelamentos (RF-08 / Risco: falha de notificação)

**Descrição:** Verificar se alterações e cancelamentos de reservas geram notificações ao responsável envolvido.

**Pré-condições:** Existe uma reserva confirmada com responsável "Prof. João Silva" cadastrado com e-mail/canal de notificação válido.

**Passos:**

1. Acessar a reserva existente.
2. Realizar uma alteração (ex.: mudar horário) ou cancelamento.
3. Confirmar a ação.
4. Verificar o envio da notificação.

**Cenário 1 - Alteração gera notificação:**

- Dados de Teste:
  - Ação: alterar horário da reserva de 14h00–16h00 para 16h00–18h00

- Resultado Esperado:
  - O sistema envia uma notificação ao responsável informando a alteração realizada.
  - A notificação é registrada no histórico do sistema.

**Cenário 2 - Cancelamento gera notificação:**

- Dados de Teste:
  - Ação: cancelar a reserva

- Resultado Esperado:
  - O sistema envia uma notificação ao responsável informando o cancelamento.
  - A notificação é registrada no histórico do sistema.


---

## CT-09 - Tempo de resposta da busca de salas (RNF-01)

**Descrição:** Verificar se a busca por salas disponíveis responde em até 2 segundos.

**Pré-condições:** O sistema está em operação normal, com volume de dados representativo do ambiente de produção.

**Passos:**

1. Acessar a página de busca de salas disponíveis.
2. Informar os filtros de busca (data, horário, capacidade, recursos).
3. Executar a busca.
4. Medir o tempo entre a requisição e a exibição dos resultados.

**Cenário 1 - Busca dentro do tempo esperado:**

- Dados de Teste:
  - Filtros: data 21/08/2026, horário 10h00–12h00, capacidade mínima 20

- Resultado Esperado:
  - O sistema retorna os resultados da busca em até 2 segundos.


---

## CT-10 - Trilha de auditoria das operações (RNF-02)

**Descrição:** Verificar se todas as operações realizadas no sistema (criação, alteração e cancelamento de reservas) geram registro de auditoria.

**Pré-condições:** Usuário autenticado com permissão para realizar operações de reserva.

**Passos:**

1. Realizar uma operação no sistema (criar, alterar ou cancelar reserva).
2. Acessar o log/trilha de auditoria.
3. Verificar o registro correspondente à operação realizada.

**Cenário 1 - Auditoria de criação de reserva:**

- Dados de Teste:
  - Ação: criar reserva na "Sala 106"

- Resultado Esperado:
  - O log de auditoria registra usuário, data/hora, ação realizada e dados da reserva criada.

**Cenário 2 - Auditoria de alteração de reserva:**

- Dados de Teste:
  - Ação: alterar horário de uma reserva existente

- Resultado Esperado:
  - O log de auditoria registra usuário, data/hora, ação realizada e os valores antes/depois da alteração.

**Cenário 3 - Auditoria de cancelamento de reserva:**

- Dados de Teste:
  - Ação: cancelar uma reserva existente

- Resultado Esperado:
  - O log de auditoria registra usuário, data/hora e a ação de cancelamento.

**Pós-condições:** Toda operação realizada no sistema possui registro correspondente na trilha de auditoria.

---

## CT-11 - Acesso limitado às unidades autorizadas (RNF-03)

**Descrição:** Verificar se o usuário consegue visualizar e reservar salas apenas das unidades para as quais está autorizado.

**Pré-condições:** O usuário "Prof. João Silva" está autorizado apenas para a unidade "Campus Central". A "Sala 201" pertence à unidade "Campus Norte".

**Passos:**

1. Autenticar-se no sistema com o usuário de teste.
2. Acessar a página de busca/reserva de salas.
3. Tentar visualizar ou reservar uma sala de uma unidade não autorizada.

**Cenário 1 - Acesso a sala de unidade autorizada:**

- Dados de Teste:
  - Sala: "Sala 101" (unidade "Campus Central")

- Resultado Esperado:
  - O sistema permite a visualização e a reserva normalmente.

**Cenário 2 - Tentativa de acesso a sala de unidade não autorizada:**

- Dados de Teste:
  - Sala: "Sala 201" (unidade "Campus Norte")

- Resultado Esperado:
  - O sistema não exibe a sala nas opções de busca, ou bloqueia a tentativa de reserva com mensagem de acesso não autorizado.

**Pós-condições:** O usuário só consegue realizar operações em salas das unidades para as quais possui autorização.

---

**Observação:** Os casos de teste acima cobrem os requisitos funcionais RF-01 a RF-08, os requisitos não funcionais RNF-01 a RNF-03, e os riscos críticos identificados no plano de teste (dupla ocupação, capacidade insegura, alteração sem autorização e falha de notificação).
