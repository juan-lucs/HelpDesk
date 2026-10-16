package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UsuarioControllerTest {

    // Estes testes exercitam o serviço diretamente; não validam requisições HTTP.
    @Autowired
    private UsuarioService service;

    @Test
    void deveSalvarEBuscarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail("controller-" + UUID.randomUUID() + "@example.com");

        Usuario salvo = service.salvarUsuario(usuario);

        assertNotNull(salvo.getId());
        assertEquals(salvo.getId(), service.pegarPorId(salvo.getId()).orElseThrow().getId());
    }

    @Test
    void deveRetornarVazioQuandoUsuarioNaoExiste() {
        assertTrue(service.pegarPorId(UUID.randomUUID()).isEmpty());
    }
}
