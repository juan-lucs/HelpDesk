package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.Statuschamado;
import com.juanlucas.helpdesk.Exception.ChamadoFechadoouCanceladoException;
import com.juanlucas.helpdesk.Exception.ChamadoNaoEncontradoException;
import com.juanlucas.helpdesk.Exception.UsuarioNaoEncontradoException;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.ComentariosChamadoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
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
    public void criarComentario(ComentariosChamado com, Chamado chamado, Usuario autor) {
        Chamado chamadoBanco = repositoryChamado.findById(chamado.getId())
                .orElseThrow(() ->
                        new ChamadoNaoEncontradoException("Chamado não existe!"));

        if (chamadoBanco.getStatus() == Statuschamado.FECHADO || chamadoBanco.getStatus() == Statuschamado.CANCELADO ) {
            throw new ChamadoFechadoouCanceladoException("Um chamado " + chamadoBanco.getStatus() + " não pode receber comentários!");
        }

        Usuario autorBanco = repositoryUsuario.findById(autor.getId())
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException("Usuário não encontrado!"));

        com.setAutor(autor);
        com.setDataComentario(LocalDateTime.now());
        chamadoBanco.getComentarios().add(com);
        repositoryChamado.save(chamadoBanco);
    }
}
