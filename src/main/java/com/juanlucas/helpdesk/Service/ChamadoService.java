package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.Statuschamado;
import com.juanlucas.helpdesk.Exception.ChamadoFechadoouCanceladoException;
import com.juanlucas.helpdesk.Exception.ChamadocomTecnicojaResponsavelException;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.juanlucas.helpdesk.Enum.Statuschamado.CANCELADO;

@Service
@RequiredArgsConstructor
public class ChamadoService {
    private final ChamadoRepository repositoryChamado;
    private final UsuarioRepository repositoryTecnico;

    public void criarChamado(Chamado c) { // Chamado vem com titulo, descricao, categoria, prioridade e Status já definidos
        c.setDataCriacao(LocalDateTime.now());
        repositoryChamado.save(c);
    }

    public Optional<Chamado> pegarPorId(UUID idChamado) {
        return repositoryChamado.findById(idChamado);
    }

    public List<Chamado> pegarTodos() {
        return repositoryChamado.findAll();
    }

    public void atribuirTecnico(String idChamado, String idTecnico) {
        var chamado = repositoryChamado.findById(UUID.fromString(idChamado))
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado!"));
        var usuario = repositoryTecnico.findById(UUID.fromString(idTecnico))
                .orElseThrow(() -> new RuntimeException("Técnico não encontrado!"));

        if (!(usuario instanceof Tecnico tecnico)) {
            throw new RuntimeException("Apenas Tecnicos podem assumir chamados!");
        }
        if (chamado.getTecnicoResponsavel() != null) {
            throw new ChamadocomTecnicojaResponsavelException("Este chamado já possuí um Tecnico responsável");
        }
        if (chamado.getStatus() == Statuschamado.FECHADO || chamado.getStatus() == CANCELADO) {
            throw new ChamadoFechadoouCanceladoException("Um chamado " + chamado.getStatus() + " não pode ter Tecnicos atribuidos!");
        }

        chamado.setTecnicoResponsavel(tecnico);
        chamado.setStatus(Statuschamado.EM_ATENDIMENTO);
        repositoryChamado.save(chamado);
    }

    public void alterarStatus(String idChamado, Statuschamado statusNovo) {
        var chamado = repositoryChamado.findById(UUID.fromString(idChamado)).orElseThrow(() -> new RuntimeException("Chamado não encontrado!"));
        if ( !(chamado.getStatus().alteracao(statusNovo)) ) {
            throw new ChamadoFechadoouCanceladoException("Um chamado + " + chamado.getStatus()+ " não pode mudar de estado");
        } else if ( chamado.getTecnicoResponsavel() == null && !(statusNovo == CANCELADO)) {
            throw new RuntimeException("Um chamado sem Tecnico responsável não pode mudar de estado!");
        }
        chamado.setStatus(statusNovo);
        repositoryChamado.save(chamado);
    }


}
