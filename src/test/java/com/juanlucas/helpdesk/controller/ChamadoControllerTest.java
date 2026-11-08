package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Exception.ChamadoNaoEncontradoException;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.ChamadoService;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        var usuario = new Usuario();
        usuario.setNome("Usuário controller");
        usuario.setEmail("controller-" + UUID.randomUUID() + "@example.com");
        usuarioRepository.save(usuario);

        var chamado = new Chamado();
        chamado.setTitulo("Chamado controller");
        chamado.setDescricao("Descrição");
        chamado.setCategoria(CategoriaChamado.SOFTWARE);
        chamado.setPrioridade(PrioridadeChamado.MEDIA);
        chamado.setStatus(StatusChamado.ABERTO);
        service.criarChamado(chamado, usuario);

        var resposta = service.pegarPorId(chamado.getId());

        assertEquals(chamado.getId(), resposta.getId());

    }

    @Test
    void deveRetornarNaoEncontradoQuandoChamadoNaoExiste() {
        assertThrows(ChamadoNaoEncontradoException.class, () -> service.pegarPorId(UUID.randomUUID()));
    }
}
