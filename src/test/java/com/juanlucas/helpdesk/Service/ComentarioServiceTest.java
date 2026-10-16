package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.Statuschamado;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.ComentariosChamadoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    void criarComentarioEadicionarAoChamado() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário teste");
        usuario.setEmail("usuario-" + UUID.randomUUID() + "@example.com");
        usuarioRepository.save(usuario);

        Chamado chamado = new Chamado();
        chamado.setTitulo("Chamado de teste");
        chamado.setDescricao("Descrição do chamado de teste");
        chamado.setCategoria(CategoriaChamado.SOFTWARE);
        chamado.setPrioridade(PrioridadeChamado.MEDIA);
        chamado.setStatus(Statuschamado.ABERTO);
        chamado.setUsuarioCriador(usuario);
        chamado.setDataCriacao(LocalDateTime.now());
        repositoryChamado.save(chamado);

        var comentario = new ComentariosChamado();
        comentario.setConteudo("Teste Conteudo");

        service.criarComentario(comentario, chamado, usuario);

        Chamado chamadoAtualizado = repositoryChamado.findById(chamado.getId()).orElseThrow();
        ComentariosChamado comentarioSalvo = chamadoAtualizado.getComentarios().stream()
                .findFirst()
                .orElseThrow();

        assertEquals(1, chamadoAtualizado.getComentarios().size());
        assertEquals("Teste Conteudo", comentarioSalvo.getConteudo());
        assertEquals(usuario.getId(), comentarioSalvo.getAutor().getId());
        assertNotNull(comentarioSalvo.getDataComentario());

        repositoryChamado.delete(chamadoAtualizado);
        usuarioRepository.delete(usuario);
    }
}
