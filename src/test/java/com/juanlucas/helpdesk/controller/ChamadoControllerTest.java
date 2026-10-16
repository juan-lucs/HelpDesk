package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.Statuschamado;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.ChamadoService;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ChamadoControllerTest {

    // Estes testes exercitam o serviço diretamente; não validam requisições HTTP.
    @Autowired
    private ChamadoService service;

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarChamadoQuandoEncontrado() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail("controller-" + UUID.randomUUID() + "@example.com");
        usuarioRepository.save(usuario);

        Chamado chamado = new Chamado();
        chamado.setTitulo("Chamado de teste");
        chamado.setDescricao("Descrição");
        chamado.setCategoria(CategoriaChamado.SOFTWARE);
        chamado.setPrioridade(PrioridadeChamado.MEDIA);
        chamado.setStatus(Statuschamado.ABERTO);
        chamado.setUsuarioCriador(usuario);
        service.criarChamado(chamado);

        var resposta = service.pegarPorId(chamado.getId());

        assertTrue(resposta.isPresent());
        assertEquals(chamado.getId(), resposta.orElseThrow().getId());
        assertEquals("Chamado de teste", resposta.orElseThrow().getTitulo());
    }

    @Test
    void deveRetornarVazioQuandoChamadoNaoExiste() {
        assertTrue(service.pegarPorId(UUID.randomUUID()).isEmpty());
    }
}
