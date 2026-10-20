# Projeto — Sistema de Atendimento e Suporte

Desenvolva uma aplicação para gerenciamento de chamados de suporte técnico.

O sistema deverá permitir que usuários registrem problemas, acompanhem o andamento dos chamados e consultem seu histórico.

Também deverá existir o conceito de técnicos responsáveis pelo atendimento.

A aplicação deverá possuir um backend/API e uma interface para utilização do sistema.

---

## Funcionalidades obrigatórias

### 1. Cadastro de usuários

A aplicação deverá permitir cadastrar usuários.

Cada usuário deverá possuir, no mínimo:

- ID
- Nome
- E-mail

> O e-mail deve ser único.

### 2. Cadastro de técnicos

A aplicação deverá permitir cadastrar técnicos responsáveis pelo atendimento dos chamados.

Cada técnico deverá possuir, no mínimo:

- ID
- Nome
- E-mail
- Especialidade

### 3. Abertura de chamado

Um usuário deverá conseguir abrir um chamado informando:

- Título
- Descrição
- Categoria
- Prioridade

O chamado deverá receber automaticamente:

- ID
- Data de criação
- Status inicial

O status inicial deverá ser: **ABERTO**

### 4. Categorias

O sistema deverá possuir categorias para os chamados.

Exemplos:

- `HARDWARE`
- `SOFTWARE`
- `REDE`
- `ACESSO`
- `OUTROS`

> Você pode escolher outras categorias.

### 5. Prioridade

Cada chamado deverá possuir uma prioridade:

- `BAIXA`
- `MEDIA`
- `ALTA`
- `CRITICA`

### 6. Atendimento do chamado

Um técnico deverá conseguir assumir um chamado.

Ao assumir o chamado, seu status deverá mudar para: **EM_ATENDIMENTO**

O sistema deverá registrar qual técnico ficou responsável pelo atendimento.

### 7. Alteração de status

O chamado deverá possuir um fluxo de estados.

**Sugestão de fluxo:**

```
ABERTO
  ↓
EM_ATENDIMENTO
  ↓
AGUARDANDO_USUARIO
  ↓
RESOLVIDO
  ↓
FECHADO
```

Também deverá existir: **CANCELADO**

O sistema deve impedir alterações de status que não sejam permitidas. Por exemplo:

- Um chamado `FECHADO` não pode voltar para `EM_ATENDIMENTO`;
- Um chamado `CANCELADO` não pode ser atendido;
- Um chamado só pode ser fechado depois de ser resolvido.

> As regras exatas ficam a critério do desenvolvedor, desde que sejam documentadas.

### 8. Comentários

Usuários e técnicos deverão poder adicionar comentários ao chamado.

Cada comentário deverá possuir:

- Auv   tor
- Conteúdo
- Data/hora

Os comentários deverão fazer parte do histórico do chamado.

### 9. Histórico

O sistema deverá registrar alterações importantes realizadas no chamado.

**Exemplo:**

```
10:32 — João abriu o chamado
10:41 — Chamado atribuído ao técnico Carlos
11:15 — Status alterado para EM_ATENDIMENTO
11:48 — Carlos adicionou um comentário
13:20 — Status alterado para RESOLVIDO
```

O histórico deverá ser consultável através da aplicação.

---

## API

A aplicação deverá disponibilizar uma API para as operações do sistema.

**Exemplos de rotas:**

```
POST   /usuarios
GET    /usuarios
GET    /usuarios/{id}

POST   /tecnicos
GET    /tecnicos
GET    /tecnicos/{id}

POST   /chamados
GET    /chamados
GET    /chamados/{id}

PATCH  /chamados/{id}/status
PATCH  /chamados/{id}/tecnico

POST   /chamados/{id}/comentarios
GET    /chamados/{id}/comentarios

GET    /chamados/{id}/historico
```

> As rotas acima são apenas uma sugestão. A organização e o desenho final da API fazem parte do desafio.

---

## Regras de negócio

A aplicação deverá possuir regras de negócio além de simples operações de CRUD.

Alguns exemplos:

- Um e-mail não pode estar associado a dois usuários. //
- Um chamado deve possuir um usuário responsável pela abertura. //
- Um chamado deve possuir título e descrição. //
- Um chamado fechado não pode receber novos comentários. 
- Um chamado cancelado não pode ser atribuído a um técnico. // 
- Um chamado deve possuir um técnico antes de entrar em `EM_ATENDIMENTO`. //
- Toda alteração de status deve ser registrada no histórico.
- Toda atribuição de técnico deve ser registrada no histórico.

> Você pode criar regras adicionais. As regras escolhidas devem ser documentadas no README.

---

## Interface

Desenvolva uma interface simples para utilização do sistema.

A interface deverá permitir, no mínimo:

### Dashboard

Exibir informações como:

| Métrica | Exemplo |
|---|---|
| Chamados abertos | 12 |
| Em atendimento | 7 |
| Aguardando usuário | 3 |
| Resolvidos | 5 |

### Lista de chamados

Exibir:

- ID
- Título
- Categoria
- Prioridade
- Status
- Técnico responsável
- Data de criação

### Detalhes do chamado

Permitir visualizar:

- Descrição
- Usuário
- Técnico
- Status
- Prioridade
- Comentários
- Histórico

---

## Testes

A aplicação deverá possuir testes automatizados para as principais regras de negócio.

Não é necessário possuir 100% de cobertura.

Priorize testes para comportamentos importantes, especialmente regras que impedem operações inválidas.

**Exemplos:**

- [ ] Não deve fechar chamado aberto
- [ ] Não deve atribuir técnico a chamado cancelado
- [ ] Deve registrar alteração de status no histórico
- [ ] Não deve permitir e-mail duplicado
- [ ] Deve permitir reabertura de chamado resolvido
