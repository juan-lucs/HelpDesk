package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.UsuarioService;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsuarioControllerTest {

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository repository;

    @Test
    void deveSalvarEBuscarUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário controller");
        usuario.setEmail("controller-" + UUID.randomUUID() + "@example.com");

        Usuario salvo = service.salvarUsuario(usuario);

        assertNotNull(salvo.getId());
        assertTrue(service.pegarPorId(salvo.getId()).isPresent());

        repository.delete(salvo);
    }

    @Test
    void deveRetornarVazioQuandoUsuarioNaoExiste() {
        assertTrue(service.pegarPorId(UUID.randomUUID()).isEmpty());
    }
}
