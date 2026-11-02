package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Exception.ChamadoNaoEncontradoException;
import com.juanlucas.helpdesk.Exception.UsuarioNaoEncontradoException;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Historico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final ChamadoRepository repositoryChamado;
    private final UsuarioRepository repositoryUsuario;

    // Método base: cria um registro de histórico e vincula ao chamado
    // Segue o mesmo padrão do ComentarioService: adiciona na lista do chamado e faz save no chamado
    // (porque o Historico não tem @ManyToOne para Chamado, o dono da relação é Chamado.historico com @JoinColumn)
    @Transactional
    public Historico registrarHistorico(Chamado chamado, Usuario autor, String descricao) {
        if (chamado == null || chamado.getId() == null || !repositoryChamado.existsById(chamado.getId())) {
            throw new ChamadoNaoEncontradoException("Chamado não existe!");
        }
        if (autor == null || autor.getId() == null || !repositoryUsuario.existsById(autor.getId())) {
            throw new UsuarioNaoEncontradoException("Usuario não encontrado!");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição do histórico é obrigatória!");
        }

        // busca o chamado gerenciado pelo Hibernate para garantir que a lista está anexada
        var chamadoGerenciado = repositoryChamado.findById(chamado.getId())
                .orElseThrow(() -> new ChamadoNaoEncontradoException("Chamado não encontrado!"));

        Historico historico = new Historico();
        historico.setAutor(autor);
        historico.setDescricao(descricao);
        historico.setHoraDaAlteracao(LocalTime.now());
        historico.setStatusatual(chamadoGerenciado.getStatus());

        chamadoGerenciado.getHistorico().add(historico);
        repositoryChamado.save(chamadoGerenciado);

        return historico;
    }

    // Regra de negócio: Toda abertura de chamado deve ser registrada no histórico
    // Ex: "10:32 — João abriu o chamado"
    @Transactional
    public Historico registrarAbertura(Chamado chamado) {
        String descricao = chamado.getUsuarioCriador().getNome() + " abriu o chamado";
        return registrarHistorico(chamado, chamado.getUsuarioCriador(), descricao);
    }

    // Regra de negócio: Toda atribuição de técnico deve ser registrada no histórico
    @Transactional
    public Historico registrarAtribuicaoTecnico(Chamado chamado, Usuario autor) {
        // autor aqui é quem atribuiu (pode ser o próprio técnico ou um admin)
        // se não souber quem atribuiu, usa o técnico responsável
        Usuario responsavel = autor != null ? autor : chamado.getTecnicoResponsavel();
        String nomeTecnico = chamado.getTecnicoResponsavel() != null
                ? chamado.getTecnicoResponsavel().getNome()
                : "técnico";

        String descricao = "Chamado atribuído ao técnico " + nomeTecnico;
        return registrarHistorico(chamado, responsavel, descricao);
    }

    // Regra de negócio: Toda alteração de status deve ser registrada no histórico
    // Ex: "11:15 — Status alterado para EM_ATENDIMENTO"
    @Transactional
    public Historico registrarAlteracaoStatus(Chamado chamado, Usuario autor, StatusChamado statusAnterior, StatusChamado statusNovo) {
        String descricao;
        if (statusAnterior == null) {
            descricao = "Status alterado para " + statusNovo;
        } else {
            descricao = "Status alterado de " + statusAnterior + " para " + statusNovo;
        }
        return registrarHistorico(chamado, autor, descricao);
    }

    // Regra de negócio: Comentários também geram entrada no histórico (opcional, mas exigido na spec)
    // Ex: "11:48 — Carlos adicionou um comentário"
    @Transactional
    public Historico registrarComentario(Chamado chamado, Usuario autor) {
        String descricao = autor.getNome() + " adicionou um comentário";
        return registrarHistorico(chamado, autor, descricao);
    }

    // Consulta: histórico consultável através da aplicação (GET /chamados/{id}/historico)
    public List<Historico> buscarHistoricoPorChamado(UUID idChamado) {
        var chamado = repositoryChamado.findById(idChamado)
                .orElseThrow(() -> new ChamadoNaoEncontradoException("Chamado não encontrado!"));
        return chamado.getHistorico();
    }

    public List<Historico> buscarHistoricoPorChamado(String idChamado) {
        return buscarHistoricoPorChamado(UUID.fromString(idChamado));
    }
}
