package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Exception.*;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Historico;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.juanlucas.helpdesk.Enum.StatusChamado.CANCELADO;

@Service
@RequiredArgsConstructor
public class ChamadoService {
    private final ChamadoRepository repositoryChamado;
    private final UsuarioRepository repositoryUsuario;
    private final HistoricoService serviceHistorico;
    @Transactional // IMPORTANTE PARA GARANTIR QUE O CHAMADO VAI SER SALVO JUNTO COM A ALTERAÇÃO DO HISTÓRICO
    public Chamado criarChamado(Chamado c, Usuario u) { // Chamado vem com titulo, descricao, categoria, prioridade e Status já definidos
        c.setDataCriacao(LocalDateTime.now());
        var usuario = repositoryUsuario.findById(u.getId()).orElseThrow(() -> new RuntimeException("Usuário inválido!"));
        c.setUsuarioCriador(usuario);
        repositoryChamado.save(c);
        serviceHistorico.registrarAbertura(c);
        return c;
    }

    public Chamado pegarPorId(UUID idChamado) {
        return repositoryChamado.findById(idChamado).orElseThrow(() -> new ChamadoNaoEncontradoException("Chamado não encontrado!"));
    }

    public List<Historico> pegarTodoHistoricoPorId(UUID idChamado) {
        return repositoryChamado.findByIdComHistorico(idChamado).orElseThrow(() -> new ChamadoNaoEncontradoException("Chamado não encontrado!"));
    }

    public List<Chamado> pegarTodos() {
        return repositoryChamado.findAll();
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
