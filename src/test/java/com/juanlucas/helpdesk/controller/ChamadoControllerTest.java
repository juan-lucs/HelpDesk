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
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChamadoControllerTest {

    @Autowired
    private ChamadoService service;

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarChamadoQuandoEncontrado() {
        var usuario = new Usuario();
        usuario.setNome("Usuário controller");
        usuario.setEmail("controller-" + UUID.randomUUID() + "@example.com");
        usuario = usuarioRepository.save(usuario);

        var chamado = new Chamado();
        chamado.setTitulo("Chamado controller");
        chamado.setDescricao("Descrição");
        chamado.setCategoria(CategoriaChamado.SOFTWARE);
        chamado.setPrioridade(PrioridadeChamado.MEDIA);
        chamado.setStatus(Statuschamado.ABERTO);
        chamado.setUsuarioCriador(usuario);
        service.criarChamado(chamado);

        var resposta = service.pegarPorId(chamado.getId());

        assertTrue(resposta.isPresent());
        assertEquals(chamado.getId(), resposta.orElseThrow().getId());

        chamadoRepository.delete(chamado);
        usuarioRepository.delete(usuario);
    }

    @Test
    void deveRetornarNaoEncontradoQuandoChamadoNaoExiste() {
        assertTrue(service.pegarPorId(UUID.randomUUID()).isEmpty());
    }
}
