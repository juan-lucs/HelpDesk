package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Exception.ChamadoFechadoouCanceladoException;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.ComentariosChamadoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ComentarioServiceTest {

    @Autowired
    private ComentarioService service;
    @Autowired
    private ChamadoRepository repositoryChamado;
    @Autowired
    private ComentariosChamadoRepository repository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário teste");
        usuario.setEmail("usuario-" + UUID.randomUUID() + "@example.com");

        return usuarioRepository.save(usuario);
    }

    private Chamado criarChamado(Usuario usuario) {
        Chamado chamado = new Chamado();
        chamado.setTitulo("Chamado de teste");
        chamado.setDescricao("Descrição do chamado de teste");
        chamado.setCategoria(CategoriaChamado.SOFTWARE);
        chamado.setPrioridade(PrioridadeChamado.MEDIA);
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setUsuarioCriador(usuario);
        chamado.setDataCriacao(LocalDateTime.now());
        return repositoryChamado.save(chamado);
    }
    @Transactional
    @Test
    void criarComentarioEadicionarAoChamado() {
        var usuario = criarUsuario();
        var chamado = criarChamado(usuario); // AMBOS VÃO CONTINUAR NO DETACHE POR CONTA DO TRANSACTIONAL, MAS ELE NÃO ESTÁ AQUI SÓ POR ISSO


        var comentario = new ComentariosChamado();
        comentario.setConteudo("Teste Conteudo");

        service.criarComentario(comentario, chamado.getId().toString(), usuario.getId().toString());

//        Chamado chamadoAtualizado = repositoryChamado.findById(chamado.getId()).orElseThrow();
        ComentariosChamado comentarioSalvo = chamado.getComentarios().stream()
                .findFirst()
                .orElseThrow();

        assertEquals(1, chamado.getComentarios().size());
        assertEquals("Teste Conteudo", comentarioSalvo.getConteudo());
        assertEquals(usuario.getId(), comentarioSalvo.getAutor().getId());
        assertNotNull(comentarioSalvo.getDataComentario());

        repositoryChamado.delete(chamado); //ao Deletar o chamado, o Cascade também faz deletar os comentarios ligados a ele
        usuarioRepository.delete(usuario);
    }
    @Transactional
    @Test
    void adicionarComentarioAoChamadoFechado() {
        var usuario = criarUsuario();
        var chamado = criarChamado(usuario);
        chamado.setStatus(StatusChamado.FECHADO);

        var comentario = new ComentariosChamado();
        comentario.setConteudo("Teste Conteudo");

        assertThrows(ChamadoFechadoouCanceladoException.class, () -> service.criarComentario(comentario, chamado.getId().toString(), usuario.getId().toString()));

        repositoryChamado.delete(chamado);
        usuarioRepository.delete(usuario);
    }
}
