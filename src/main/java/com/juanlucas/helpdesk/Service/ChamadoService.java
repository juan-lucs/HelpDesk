package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Exception.*;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Historico;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import jakarta.persistence.criteria.Predicate;

import static com.juanlucas.helpdesk.Enum.StatusChamado.CANCELADO;

@Service
@RequiredArgsConstructor
public class ChamadoService {
    private final ChamadoRepository repositoryChamado;
    private final UsuarioRepository repositoryUsuario;
    private final HistoricoService serviceHistorico;

    @Transactional // IMPORTANTE PARA GARANTIR QUE O CHAMADO VAI SER SALVO JUNTO COM A ALTERAÇÃO DO HISTÓRICO
    public void criarChamado(Chamado c, UUID idUsuario) { // Chamado vem com titulo, descricao, categoria, prioridade e Status já definidos
        c.setDataCriacao(LocalDateTime.now());
        var usuario = repositoryUsuario.findById(idUsuario).orElseThrow(() -> new RuntimeException("Usuário inválido!"));
        c.setUsuarioCriador(usuario);
        repositoryChamado.save(c);
        serviceHistorico.registrarAbertura(c);
    }

    public Optional<Chamado> pegarPorId(UUID idChamado) {
        return repositoryChamado.findById(idChamado);
    }

    public List<Historico> pegarTodoHistoricoPorId(UUID idChamado) {
        return repositoryChamado.findByIdComHistorico(idChamado).orElseThrow(() -> new ChamadoNaoEncontradoException("Chamado não encontrado!"));
    }

    // import jakarta.persistence.criteria.Predicate;  <- precisa ser este Predicate

    public List<Chamado> filtrar(String titulo, CategoriaChamado categoria,
                                 PrioridadeChamado prioridade, LocalDate dataCriacao) {

        // Specification<Chamado> é uma interface funcional do Spring Data JPA:
        // tem um único método ABSTRATO, Predicate toPredicate(Root<T> root , CriteriaQuery<?> query , CriteriaBuilder cb ).
        // O <Chamado> é a entidade que está sendo filtrada (a mesma do List<Chamado> retornado).

        Specification<Chamado> filtros = ((root, query, cb) -> {
            // Isto é uma EXPRESSÃO LAMBDA: root, query e cb são PARÂMETROS (entrada).
            // Quem chama a lambda é o Spring, dentro do findAll(filtros), e entrega os três já prontos.

            // root  -> representa a entidade Chamado no FROM da consulta.
            //         ↳ root.get("x") acessa um ATRIBUTO da classe Java (o nome do atributo, não o nome da coluna no banco).
            // query -> a consulta inteira (CriteriaQuery), já criada pelo Spring. Aqui não é usada.
            // cb    -> CriteriaBuilder: uma "fábrica" que CRIA objetos PREDICATE DO CRITERIA (LIKE, =, AND...).
            //         ↳ Ele NÃO gera SQL. Quem converte tudo em SQL, no final, é o Hibernate.
            //↳↳↳↳↳↳
            // Predicate: um objeto pequeno que DESCREVE uma condição do WHERE. Ainda não é SQL.
            List<Predicate> condicoes = new ArrayList<>();

            // O que o cb cria:
            //   cb.equal(a, b) -> Predicate de  a = b
            //   cb.like(a, b)  -> Predicate de  a LIKE b
            //   cb.lower(x)    -> expressão     LOWER(x)
            //   cb.and(...)    -> Predicate que junta vários com AND

            if (titulo != null && !titulo.isBlank()) {
                // strip() tira os espaços das pontas e toLowerCase() deixa tudo em minúsculas.
                // String é IMUTÁVEL: esses métodos não alteram o "titulo" original, eles devolvem uma String NOVA.
                // Por isso o resultado é guardado em "trecho".
                String trecho = titulo.strip().toLowerCase();

                // root.get("titulo") pega o ATRIBUTO "titulo" da classe Chamado.
                // cb.lower(...) cria a expressão LOWER(titulo).
                // "%" + trecho + "%" significa "contém o trecho em qualquer posição".
                condicoes.add(cb.like(cb.lower(root.get("titulo")), "%" + trecho + "%"));
                // SQL aproximado: LOWER(titulo) LIKE '%erro%'
            }

            if (prioridade != null) {
                condicoes.add(
                        cb.equal(root.get("prioridade"), prioridade));
            } // cria o Predicate: prioridade = ?   (o ? recebe o valor do parâmetro)

            if (categoria != null) {
                condicoes.add(
                        cb.equal(root.get("categoria"), categoria));
            } // cria o Predicate: categoria = ?

            if (dataCriacao != null) {
                // atStartOfDay() transforma o LocalDate em LocalDateTime à meia-noite.
                LocalDateTime inicio = dataCriacao.atStartOfDay();
                LocalDateTime fim = dataCriacao.atTime(LocalTime.MAX);
                condicoes.add(
                        cb.between(root.get("dataCriacao"), inicio, fim)
                );
            } // cria o Predicate: dataCriacao BETWEEN inicio , fim

            // cb.and(Predicate...) é varargs: aceita predicados avulsos OU um array de Predicate.
            // Como a quantidade de condições varia, elas estão numa List, então convertemos em array.
            // toArray(new Predicate[0]): o array vazio só informa o TIPO (Predicate[]).
            // O toArray cria e devolve um array NOVO do tamanho certo, com as mesmas referências.
            // Se a lista estiver vazia, cb.and() vira uma condição sempre verdadeira (sem filtro)
            // e o findAll retorna tudo.
            return cb.and(condicoes.toArray(new Predicate[0]));
        });

        return repositoryChamado.findAll(filtros);
    }

    @Transactional
    public void atribuirTecnico(String idChamado, String idTecnico) {
        var chamado = repositoryChamado.findById(UUID.fromString(idChamado)).orElseThrow(() -> new RuntimeException("Chamado não encontrado!"));

        var usuario = repositoryUsuario.findById(UUID.fromString(idTecnico)).orElseThrow(() -> new RuntimeException("Técnico não encontrado!"));

        if (!(usuario instanceof Tecnico tecnico)) { // verifica se Usuario é um OBJETO tipo Tecnico, não Usuário.
            throw new RuntimeException("Apenas Tecnicos podem assumir chamados!");
        }
        if (chamado.getTecnicoResponsavel() != null) { // Verifica se o chamado já tem um técnico, se não for null ele dá erro, se for null ele passa reto
            throw new ChamadocomTecnicojaResponsavelException("Este chamado já possuí um Tecnico responsável");
        }
        if (chamado.getStatus() == StatusChamado.FECHADO || chamado.getStatus() == CANCELADO) {
            throw new ChamadoFechadoouCanceladoException("Um chamado " + chamado.getStatus() + " não pode ter Tecnicos atribuidos!");
        }

        chamado.setTecnicoResponsavel(tecnico);
        chamado.setStatus(StatusChamado.EM_ATENDIMENTO);
        repositoryChamado.save(chamado);
        serviceHistorico.registrarAtribuicaoTecnico(chamado); // registra a atribuicao em historico deste chamado
    }

    @Transactional
    public void alterarStatus(String idChamado, StatusChamado statusNovo, String idAutorDaAlteracao) {
        var chamado = repositoryChamado.findById(UUID.fromString(idChamado)).orElseThrow(() -> new ChamadoNaoEncontradoException("Chamado não encontrado!"));
        var autorDaAlteracao = repositoryUsuario.findById(UUID.fromString(idAutorDaAlteracao)).orElseThrow(() -> new UsuarioNaoEncontradoException("AUTOR DA MUDANÇA DE STATUS NÃO ENCONTRADO!"));
        if ( !(chamado.getStatus().alteracao(statusNovo)) ) {
            throw new TransicaoinvalidaException("Um chamado " + chamado.getStatus() + " Não pode ser alterado para " + statusNovo);
        } else if (chamado.getTecnicoResponsavel() == null && !(statusNovo == StatusChamado.CANCELADO)) {
            throw new TransicaoinvalidaException("Um chamado sem Tecnico responsável não pode mudar de estado!");
        }
        var statusAnterior = chamado.getStatus();
        chamado.setStatus(statusNovo);
        repositoryChamado.save(chamado);
        serviceHistorico.registrarAlteracaoStatus(chamado,autorDaAlteracao,statusAnterior,statusNovo);
    }

}
