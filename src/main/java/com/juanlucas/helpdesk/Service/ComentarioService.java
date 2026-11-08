package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Exception.ChamadoFechadoouCanceladoException;
import com.juanlucas.helpdesk.Exception.ChamadoNaoEncontradoException;
import com.juanlucas.helpdesk.Exception.UsuarioNaoEncontradoException;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import org.springframework.transaction.annotation.Transactional;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComentarioService {

//    private final ComentariosChamadoRepository repository;
    private final ChamadoRepository repositoryChamado;
    private final UsuarioRepository repositoryUsuario;

    @Transactional
    public void criarComentario(ComentariosChamado com, String idChamado, String idAutor) throws ChamadoNaoEncontradoException{
        var chamado = repositoryChamado.findById(UUID.fromString(idChamado)).orElseThrow(() -> new ChamadoNaoEncontradoException("Chamado não existe!"));
        var Autor = repositoryUsuario.findById(UUID.fromString(idAutor)).orElseThrow( () -> new UsuarioNaoEncontradoException("Usuario não encontrado!"));
        if ( (repositoryChamado.verificarStatusChamado(chamado.getId()) == StatusChamado.FECHADO) || repositoryChamado.verificarStatusChamado(chamado.getId()) == StatusChamado.CANCELADO ) {
            throw new ChamadoFechadoouCanceladoException("Um chamado " + chamado.getStatus() + " não pode receber comentários!");
        }
            com.setAutor(Autor);
            com.setDataComentario(LocalDateTime.now());
            repositoryChamado.findById(chamado.getId()).ifPresent(c -> c.getComentarios().add(com));
            // vai salvar sem o repository.save ao fechar a transação
        // e o comentário também graças ao cascade
    }
}
