# Helpdesk — Sistema de Atendimento e Suporte

Projeto de estudo desenvolvido para praticar a construção de uma API com Java e Spring Boot, utilizando um sistema de chamados de suporte técnico como domínio.

O cenário de negócio é fictício e foi escolhido como exercício de desenvolvimento. O projeto não atende a um cliente real e está em evolução, com foco no aprendizado de persistência, regras de negócio, organização de uma API e testes automatizados.

O código deste projeto é desenvolvido por mim como exercício de aprendizado. Utilizo IA como apoio para esclarecer conceitos, simular demandas de negócio, revisar a implementação e elaborar documentação

## Objetivos de aprendizado

- Organizar responsabilidades entre controllers, services e repositories.
- Modelar usuários, técnicos, chamados, comentários e histórico com JPA.
- Implementar regras que vão além de um CRUD, como atribuição de técnicos e transições de status.
- Trabalhar com DTOs, validação de entradas e respostas HTTP.
- Escrever testes das principais regras de negócio com banco em memória.
- Documentar os requisitos e o contrato da API.

## Tecnologias

- Java 21, versão configurada no projeto.
- Spring Boot 4.1.1.
- Spring Web MVC.
- Spring Data JPA e Hibernate.
- Jakarta Validation.
- PostgreSQL para execução local.
- H2 para testes.
- Lombok, JUnit e Maven Wrapper.

## Estado do projeto

O desenvolvimento está concentrado no backend. Já existem cadastros e consultas de usuários e técnicos, serviços de abertura e consulta de chamados, atribuição de técnico, controle de status, comentários e registros de histórico.

A API ainda passa por ajustes: algumas operações existem nos serviços, mas precisam de correções ou de rotas próprias para uso por HTTP. A presença de uma funcionalidade no serviço não significa que todo o seu fluxo pela API esteja concluído.

Próximas etapas previstas:

- Adequar as rotas e os corpos das requisições ao contrato proposto.
- Ampliar o uso de DTOs e as validações de entrada.
- Padronizar o tratamento de erros e os códigos de resposta.
- Corrigir a entrada de comentários e integrar seu registro ao histórico.
- Disponibilizar consultas próprias de comentários e histórico.
- Implementar métricas para o dashboard.
- Desenvolver a interface visual.
- Adicionar testes de requisições HTTP.

Autenticação e autorização ainda não estão implementadas.

## Regras de negócio implementadas no núcleo

- O e-mail não pode se repetir entre usuários e técnicos.
- O cadastro de técnico exige uma especialidade.
- O chamado deve estar associado a um usuário existente.
- Somente um usuário do tipo técnico pode assumir atendimento.
- Um chamado que já possui técnico não aceita outra atribuição.
- Chamados fechados ou cancelados não aceitam atribuição de técnico nem novos comentários.
- A atribuição muda o status para `EM_ATENDIMENTO`.
- Mudanças de status exigem técnico responsável, exceto o cancelamento.
- Abertura, atribuição e alteração de status geram registros de histórico.

### Fluxo de status atual

| Estado atual | Próximos estados permitidos |
|---|---|
| `ABERTO` | `EM_ATENDIMENTO`, `AGUARDANDO_USUARIO`, `CANCELADO` |
| `EM_ATENDIMENTO` | `AGUARDANDO_USUARIO`, `CANCELADO` |
| `AGUARDANDO_USUARIO` | `EM_ATENDIMENTO`, `FECHADO`, `CANCELADO` |
| `FECHADO` | `EM_ATENDIMENTO` — reabertura |
| `CANCELADO` | Nenhum |

Esse fluxo permite reabrir um chamado fechado, mantendo o técnico responsável. O estado `RESOLVIDO`, presente como sugestão no enunciado inicial, não faz parte da implementação atual.

## Como executar localmente

### Pré-requisitos

- JDK compatível com Java 21.
- PostgreSQL em execução.

O repositório inclui o Maven Wrapper, portanto não é necessário instalar o Maven separadamente. A primeira execução precisa de acesso à internet para baixar as dependências.

### 1. Preparar o banco

Crie um banco chamado `helpdesk` na sua instância PostgreSQL:

```sql
CREATE DATABASE helpdesk;
```

A configuração local em `src/main/resources/application.yaml` utiliza:

| Propriedade | Valor padrão para desenvolvimento |
|---|---|
| URL | `jdbc:postgresql://localhost:5432/helpdesk` |
| Usuário | `postgres` |
| Senha | `postgres` |

Para utilizar outras credenciais sem alterar o arquivo, configure as variáveis de ambiente do Spring:

```bash
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/helpdesk'
export SPRING_DATASOURCE_USERNAME='seu_usuario'
export SPRING_DATASOURCE_PASSWORD='sua_senha'
```

Os valores padrão são apenas para desenvolvimento local. Credenciais reais devem ficar fora do repositório.

O projeto usa `spring.jpa.hibernate.ddl-auto=update`, que permite ao Hibernate atualizar as tabelas durante a inicialização. O banco precisa existir antes de iniciar a aplicação.

### 2. Iniciar a aplicação

Na pasta do projeto:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

A API fica disponível em `http://localhost:8080`. Ainda não há interface visual na página inicial.

### 3. Exemplo: cadastrar usuário

```bash
curl -i -X POST http://localhost:8080/usuarios \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Ana Souza","email":"ana@example.com"}'
```

O cadastro bem-sucedido retorna `201 Created`, sem corpo, com o endereço do usuário criado no cabeçalho `Location`.

## Rotas presentes no código

Estas são as rotas atuais, que ainda diferem do contrato proposto:

| Método | Rota | Operação |
|---|---|---|
| POST | `/usuarios` | Cadastrar usuário |
| GET | `/usuarios/{id}` | Consultar usuário |
| GET | `/usuarios/usuarios` | Listar usuários comuns |
| POST | `/usuarios/tecnico` | Cadastrar técnico |
| GET | `/usuarios/tecnicos` | Listar técnicos |
| POST | `/chamado/chamado` | Abrir chamado — entrada HTTP em revisão |
| GET | `/chamado` | Listar chamados |
| GET | `/chamado/{id}` | Consultar chamado |
| PATCH | `/chamado/atribuirtecnicochamado` | Atribuir técnico |
| PATCH | `/chamado/alterarStatus/{id}` | Alterar status |
| POST | `/comentarios` | Adicionar comentário — entrada HTTP em revisão |

As rotas de chamados retornam entidades e ainda precisam de revisão dos objetos de resposta. Para o desenho esperado das entradas, saídas e erros, consulte o contrato abaixo.

## Testes

Execute:

```bash
./mvnw test
```

No Windows, utilize `.\mvnw.cmd test`.

Os testes utilizam H2 em memória, sem necessidade de PostgreSQL. A suíte inclui cenários de cadastro, e-mail duplicado, especialidade obrigatória, criação e atribuição de chamados, transições de status, comentários e histórico.

Atualmente, as classes chamadas `UsuarioControllerTest` e `ChamadoControllerTest` exercitam os serviços diretamente; elas não validam requisições HTTP.

## Organização

```text
src/main/java/com/juanlucas/helpdesk/
├── controller/       # Endpoints HTTP e DTOs
├── Service/          # Casos de uso e regras de negócio
├── repository/       # Acesso ao banco
├── Model/Entity/     # Entidades persistidas
├── Enum/             # Status, categorias, prioridades e especialidades
└── Exception/        # Exceções da aplicação

src/main/resources/   # Configuração da aplicação
src/test/             # Testes automatizados e configuração do H2
```

## Documentação

- [Enunciado e requisitos do projeto](sistema-atendimento-suporte.md).
- [Contrato de API proposto](contrato-api.md): campos, regras, requisições, respostas e diferenças para a implementação atual.

O contrato descreve o comportamento pretendido e serve como guia de evolução. As pendências registradas nele fazem parte do processo de estudo e desenvolvimento.
