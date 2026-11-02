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
@Service
@RequiredArgsConstructor
public class ComentarioService {

//    private final ComentariosChamadoRepository repository;
    private final ChamadoRepository repositoryChamado;
    private final UsuarioRepository repositoryUsuario;

    @Transactional
    public void criarComentario(ComentariosChamado com, Chamado chamado, Usuario Autor) {
        if ( !(repositoryChamado.existsById(chamado.getId())) ) {
            throw new ChamadoNaoEncontradoException("Chamado não existe!");
        } else if ( (repositoryChamado.verificarStatusChamado(chamado.getId()) == StatusChamado.FECHADO) || repositoryChamado.verificarStatusChamado(chamado.getId()) == StatusChamado.CANCELADO ) {
            throw new ChamadoFechadoouCanceladoException("Um chamado " + chamado.getStatus() + " não pode receber comentários!");
        } else if ( !(repositoryUsuario.existsById(Autor.getId())) ) {
            throw new UsuarioNaoEncontradoException("Usuario não encontrado!");
        }
            com.setAutor(Autor);
            com.setDataComentario(LocalDateTime.now());
            chamado.getComentarios().add(com);
            repositoryChamado.save(chamado); // vai salvar o comentário também graças ao cascade
    }
}
