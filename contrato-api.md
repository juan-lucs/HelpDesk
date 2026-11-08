# Contrato de API — Helpdesk

Versão proposta: 1.0 — 08/11/2026.

Este documento define o comportamento esperado da API para orientar sua implementação e o frontend. Não representa uma garantia de que todas as rotas e validações já estejam implementadas. Foi organizado a partir do exemplo de [Cadastro de Autor](https://whimsical.com/cadastro-de-autor-8jTJxcmJu3pFjKhDp35msT), dos requisitos do projeto e do código atual.

## 1. Descrição

Permitir cadastrar usuários e técnicos, abrir e acompanhar chamados, atribuir atendimento, alterar status, trocar comentários e consultar o histórico. Disponibilizar contagens por status para o dashboard.

## 2. Atores

- **Usuário:** pessoa que abre chamados e participa das conversas.
- **Técnico:** usuário com especialidade, responsável pelo atendimento; também pode abrir chamados.

O projeto ainda não possui autenticação. Nesta versão didática, os IDs de usuário e autor são informados nas requisições e sua existência deve ser validada. Isso identifica registros, mas não comprova a identidade de quem faz a chamada. Controle de permissões por pessoa fica fora deste contrato inicial.

## 3. Convenções

- Endereço local: `http://localhost:8080`.
- Recursos no plural: `/usuarios`, `/tecnicos`, `/chamados`.
- Corpos de requisição e resposta usam `application/json`.
- IDs são UUIDs. Nos exemplos, `uuid` é um marcador a substituir por um UUID real.
- Datas usam ISO 8601 sem fuso, conforme o `LocalDateTime` atual: `2026-11-08T10:30:00`. O ambiente deve utilizar `America/Sao_Paulo`.
- POST bem-sucedido retorna `201 Created`, cabeçalho `Location` com a URI do recurso criado e nenhum corpo.
- PATCH bem-sucedido retorna `204 No Content`, sem corpo.
- GET retorna `200 OK` com corpo; listas vazias retornam `[]`.
- Nesta versão, listas não possuem paginação nem filtros.
- Campos de controle, como ID, status inicial e datas, são definidos pelo servidor. Não são aceitos nos corpos de criação, salvo IDs de referências expressamente documentados.
- Campos desconhecidos nos corpos retornam `400`.
- Cada operação de alteração e seus registros de histórico devem ser persistidos juntos; se houver falha, toda a operação é desfeita.

### Respostas de erro

Todos os erros previstos usam o mesmo formato:

```json
{
  "status": 422,
  "message": "Erro de validação",
  "errors": [
    { "field": "nome", "error": "Nome é obrigatório" }
  ]
}
```

| Código | Quando usar |
|---|---|
| `400 Bad Request` | JSON malformado, UUID inválido, enum desconhecido ou campo desconhecido |
| `404 Not Found` | Recurso ou referência informada não existe |
| `409 Conflict` | E-mail duplicado, transição proibida ou operação incompatível com o estado atual |
| `422 Unprocessable Entity` | Campo obrigatório ausente/nulo/vazio, tamanho excedido, e-mail inválido ou usuário que não é técnico informado como técnico |

Erros sem campo específico retornam `errors: []`. Falhas inesperadas retornam `500` com mensagem genérica, sem detalhes internos.

## 4. Cadastro e consulta de usuários — RF01/RF02

### Campos solicitados pelo negócio

| Campo | Tipo | Regra |
|---|---|---|
| `nome` | string | Obrigatório; de 1 a 100 caracteres após remover espaços das extremidades |
| `email` | string | Obrigatório; formato de e-mail válido; até 150 caracteres |

### Campos lógicos

`id`: UUID gerado pelo servidor.

### Regras de negócio propostas

O e-mail deve ser único entre usuários e técnicos. Remover espaços das extremidades e converter o e-mail para minúsculas antes de validar duplicidade e salvar. Essa normalização ainda precisa ser implementada.

### Cadastrar usuário

`POST /usuarios`

```json
{
  "nome": "Ana Souza",
  "email": "ana@example.com"
}
```

Sucesso: `201`, com `Location: /usuarios/{id}`. Erros: `400`, `422` ou `409` para e-mail já cadastrado.

Exemplo de duplicidade:

```json
{
  "status": 409,
  "message": "E-mail já cadastrado",
  "errors": []
}
```

### Consultar usuário

`GET /usuarios/{id}`

Sucesso: `200`.

```json
{
  "id": "uuid",
  "nome": "Ana Souza",
  "email": "ana@example.com"
}
```

Erros: `400` ou `404`. Como técnico também é usuário, esta consulta aceita seu ID e retorna os campos comuns; a especialidade é consultada em `/tecnicos/{id}`.

### Listar usuários comuns

`GET /usuarios`

Sucesso: `200`, com um array de objetos no formato acima. Não inclui técnicos, seguindo a separação existente nas consultas do projeto. Ordenar por nome e, em empate, ID.

## 5. Cadastro e consulta de técnicos — RF03/RF04

### Campos e regras

Mesmas regras de nome e e-mail do usuário, acrescidas de `especialidade` obrigatória, com um dos valores: `HARDWARE`, `SOFTWARE`, `REDES`, `OUTROS`.

O ID é gerado pelo servidor. A especialidade não restringe quais categorias o técnico pode atender nesta versão, seguindo o comportamento atual.

### Cadastrar técnico

`POST /tecnicos`

```json
{
  "nome": "Carlos Lima",
  "email": "carlos@example.com",
  "especialidade": "SOFTWARE"
}
```

Sucesso: `201`, com `Location: /tecnicos/{id}`. Erros: `400`, `422` ou `409` para e-mail duplicado.

### Consultar técnico

`GET /tecnicos/{id}`

Sucesso: `200`.

```json
{
  "id": "uuid",
  "nome": "Carlos Lima",
  "email": "carlos@example.com",
  "especialidade": "SOFTWARE"
}
```

Erros: `400` ou `404`. Se o ID pertencer somente a um usuário comum, retornar `404` nesta rota.

### Listar técnicos

`GET /tecnicos`

Sucesso: `200`, com um array no formato acima. Ordenar por nome e, em empate, ID.

## 6. Abertura de chamados — RF05/RF06

### Campos solicitados pelo negócio

| Campo | Tipo | Regra |
|---|---|---|
| `titulo` | string | Obrigatório; de 1 a 250 caracteres após remover espaços das extremidades |
| `descricao` | string | Obrigatório; de 1 a 1.000 caracteres após remover espaços das extremidades |
| `categoria` | enum | `HARDWARE`, `SOFTWARE`, `REDE`, `ACESSO`, `OUTRO` |
| `prioridade` | enum | `BAIXA`, `MEDIA`, `ALTA`, `CRITICA` |
| `usuarioCriadorId` | UUID | Obrigatório; deve identificar um usuário existente, incluindo técnico |

Categoria e prioridade são obrigatórias. Os nomes `OUTRO` e `REDES` usados neste documento preservam os enums atuais, mesmo com a diferença entre categoria e especialidade.

### Campos lógicos e regras

Gerar ID e data de criação; forçar status `ABERTO`; iniciar sem técnico responsável, comentários ou histórico fornecidos pelo cliente. Registrar a abertura no histórico com o usuário criador como autor.

### Abrir chamado

`POST /chamados`

```json
{
  "titulo": "Computador sem acesso à rede",
  "descricao": "A conexão parou de funcionar nesta manhã.",
  "categoria": "REDE",
  "prioridade": "ALTA",
  "usuarioCriadorId": "uuid"
}
```

Sucesso: `201`, com `Location: /chamados/{id}`. Erros: `400`, `422` ou `404` para usuário inexistente.

## 7. Consulta de chamados — RF07/RF08

### Listar chamados

`GET /chamados`

Sucesso: `200`. Ordenar por data de criação decrescente e, em empate, ID.

```json
[
  {
    "id": "uuid",
    "titulo": "Computador sem acesso à rede",
    "categoria": "REDE",
    "prioridade": "ALTA",
    "status": "ABERTO",
    "tecnicoResponsavel": null,
    "dataCriacao": "2026-11-08T10:30:00"
  }
]
```

Quando atribuído, `tecnicoResponsavel` é um objeto com `id` e `nome`.

### Visualizar detalhes

`GET /chamados/{id}`

Sucesso: `200`.

```json
{
  "id": "uuid",
  "titulo": "Computador sem acesso à rede",
  "descricao": "A conexão parou de funcionar nesta manhã.",
  "categoria": "REDE",
  "prioridade": "ALTA",
  "status": "EM_ATENDIMENTO",
  "usuarioCriador": { "id": "uuid", "nome": "Ana Souza" },
  "tecnicoResponsavel": { "id": "uuid", "nome": "Carlos Lima" },
  "dataCriacao": "2026-11-08T10:30:00"
}
```

`tecnicoResponsavel` pode ser `null`. Comentários e histórico são consultados nas rotas próprias. As respostas não incluem referências de volta para o chamado nem coleções de entidades relacionadas. Erros: `400` ou `404`.

## 8. Atribuição de técnico — RF09

`PATCH /chamados/{id}/tecnico`

```json
{
  "tecnicoId": "uuid"
}
```

### Regras de negócio

- `tecnicoId` é obrigatório e deve identificar um técnico cadastrado.
- Chamado fechado ou cancelado não pode receber atribuição.
- Chamado que já possui técnico não pode receber outra atribuição, mesmo ao mesmo técnico.
- Associar o técnico e mudar o status para `EM_ATENDIMENTO`.
- Registrar a atribuição e, se houver mudança de status, a transição no histórico; nesta operação de assumir atendimento, o autor registrado é o próprio técnico informado.

Sucesso: `204`. Erros: `400`, `422`, `404` para chamado/técnico inexistente ou `409` para chamado encerrado ou já atribuído.

## 9. Alteração de status — RF10

`PATCH /chamados/{id}/status`

```json
{
  "status": "AGUARDANDO_USUARIO",
  "autorId": "uuid"
}
```

Ambos os campos são obrigatórios. O autor deve existir. O status deve pertencer ao enum abaixo.

### Fluxo adotado a partir do código atual

| Estado atual | Próximos estados permitidos |
|---|---|
| `ABERTO` | `EM_ATENDIMENTO`, `AGUARDANDO_USUARIO`, `CANCELADO` |
| `EM_ATENDIMENTO` | `AGUARDANDO_USUARIO`, `CANCELADO` |
| `AGUARDANDO_USUARIO` | `EM_ATENDIMENTO`, `FECHADO`, `CANCELADO` |
| `FECHADO` | `EM_ATENDIMENTO` — reabertura |
| `CANCELADO` | Nenhum |

Toda mudança, exceto para `CANCELADO`, exige técnico responsável. Repetir o estado atual gera `409`. Cancelamento utiliza esta mesma rota e pode ocorrer antes da atribuição.

O status `RESOLVIDO` não faz parte desta versão. O fluxo escolhido fecha o chamado após `AGUARDANDO_USUARIO` e permite reabrir um chamado `FECHADO`, preservando o técnico. Isso é diferente do exemplo do documento inicial, que permite escolher e documentar as regras. Não existe ainda uma etapa específica para registrar confirmação de solução pelo usuário.

Registrar autor, data/hora, status anterior e novo status no histórico.

Sucesso: `204`. Erros: `400`, `422`, `404` para chamado/autor inexistente ou `409` para transição inválida ou ausência de técnico.

Exemplo:

```json
{
  "status": 409,
  "message": "Um chamado CANCELADO não pode ser alterado para EM_ATENDIMENTO",
  "errors": []
}
```

## 10. Comentários — RF11/RF12

### Adicionar comentário

`POST /chamados/{id}/comentarios`

```json
{
  "autorId": "uuid",
  "conteudo": "Já reiniciei o computador e o problema continua."
}
```

### Campos e regras

- `autorId`: UUID obrigatório de usuário ou técnico existente.
- `conteudo`: obrigatório; de 1 a 1.000 caracteres após remover espaços das extremidades.
- Gerar ID e data/hora pelo servidor e vincular ao chamado da URI.
- Não permitir comentários em chamados `FECHADO` ou `CANCELADO`.
- Registrar no histórico que o autor adicionou um comentário.

Sucesso: `201`, com `Location: /chamados/{id}/comentarios/{comentarioId}`. Erros: `400`, `422`, `404` para chamado/autor inexistente ou `409` para chamado encerrado.

### Consultar comentários

`GET /chamados/{id}/comentarios`

Sucesso: `200`, ordenado por data/hora crescente e, em empate, ID.

```json
[
  {
    "id": "uuid",
    "autor": { "id": "uuid", "nome": "Ana Souza" },
    "conteudo": "Já reiniciei o computador e o problema continua.",
    "dataComentario": "2026-11-08T10:45:00"
  }
]
```

`GET /chamados/{id}/comentarios/{comentarioId}` retorna um único objeto nesse mesmo formato, para permitir consultar a URI de criação. Erros das consultas: `400` ou `404`. Comentário pertencente a outro chamado retorna `404`.

## 11. Histórico — RF13

`GET /chamados/{id}/historico`

Sucesso: `200`, ordenado por data/hora crescente e, em empate, ID.

```json
[
  {
    "id": "uuid",
    "autor": { "id": "uuid", "nome": "Ana Souza" },
    "descricao": "Chamado aberto por Ana Souza",
    "statusAtual": "ABERTO",
    "horaDaAlteracao": "2026-11-08T10:30:00"
  }
]
```

Registrar abertura, atribuição, alterações de status e inclusão de comentários. `statusAtual` representa o status no instante do evento. Na alteração de status, a descrição informa os estados anterior e novo. O histórico é gerado pelo sistema; não há endpoint para editar ou excluir eventos.

Erros: `400` ou `404` para chamado inexistente.

## 12. Dashboard — RF14

`GET /dashboard`

Sucesso: `200`.

```json
{
  "total": 20,
  "abertos": 8,
  "emAtendimento": 5,
  "aguardandoUsuario": 3,
  "fechados": 3,
  "cancelados": 1
}
```

Contar todos os chamados, sem filtro de período. `total` deve ser igual à soma dos cinco grupos. Na ausência de chamados, retornar todos os valores iguais a zero. Não incluir contagem de resolvidos enquanto esse estado não fizer parte do fluxo.

## 13. Escopo desta versão

Não estão previstos atualização/exclusão de usuários e técnicos, edição/exclusão de chamados e comentários, transferência de técnico, anexos, login, filtros ou paginação. Essas operações não são necessárias para reproduzir o escopo funcional inicial e podem ser contratadas depois. A interface visual (RF15) deve consumir as rotas descritas aqui.

## 14. Diferenças para a implementação observada

| Tema | Situação atual | Adequação ao contrato |
|---|---|---|
| Criar usuário | `POST /usuarios` usa DTO e já retorna `201` com `Location` | Adicionar validações, normalização de e-mail e erros padronizados |
| Listar usuários | `GET /usuarios/usuarios` | Mover para `GET /usuarios` |
| Técnicos | Cadastro e lista ficam sob `/usuarios` | Expor `/tecnicos` e consulta específica por ID |
| Criar chamado | `POST /chamado/chamado`, com `Usuario` separado do corpo | Receber um único DTO em `POST /chamados`, com `usuarioCriadorId` |
| Status inicial | Valor padrão na entidade, mas o serviço aceita status recebido | Forçar `ABERTO` e impedir campos de controle no pedido |
| Consulta de chamados | Retorna entidades em `/chamado` | Padronizar `/chamados` e criar respostas sem referências circulares |
| Atribuir técnico | IDs como parâmetros; registra atribuição | Receber DTO e registrar também a transição de status |
| Alterar status | Parâmetros separados em `/chamado/alterarStatus/{id}` | Adotar URI e corpo JSON deste contrato |
| Comentários | Controller declara dois `@RequestBody` | Receber um único DTO e extrair chamado da URI |
| Histórico de comentários | Método existe, mas não é chamado pelo serviço de comentários | Integrar registro à mesma transação |
| Consultar comentários/histórico | Sem endpoints próprios | Criar as consultas documentadas |
| Dashboard | Sem implementação localizada | Implementar contagens e endpoint |
| Validações e erros | Restrições de banco e exceções sem tratamento HTTP padronizado | Validar entradas e implementar respostas `400`, `404`, `409` e `422` |

As regras novas de normalização, ordenação, formato de resposta e erros acima são decisões propostas para tornar o contrato consistente. O fluxo de status e a possibilidade de técnico abrir chamado foram preservados a partir do código atual.
