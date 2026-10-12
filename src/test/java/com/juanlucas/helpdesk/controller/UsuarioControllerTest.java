package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService service;

    @InjectMocks
    private UsuarioController controller;

    @Test
    void deveEncaminharCadastroDeUsuarioAoService() throws Exception {
        Usuario usuario = new Usuario();
        when(service.salvarUsuario(usuario)).thenReturn(usuario);

        assertSame(usuario, controller.salvarUsuario(usuario));
        verify(service).salvarUsuario(usuario);
    }

    @Test
    void deveEncaminharCadastroDeTecnicoAoService() throws Exception {
        Tecnico tecnico = new Tecnico();
        when(service.salvarTecnico(tecnico)).thenReturn(tecnico);

        assertSame(tecnico, controller.salvarTecnico(tecnico));
        verify(service).salvarTecnico(tecnico);
    }

    @Test
    void deveRetornarUsuarioQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        when(service.pegarPorId(id)).thenReturn(Optional.of(usuario));

        var resposta = controller.pegarPorId(id.toString());

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertSame(usuario, resposta.getBody());
    }

    @Test
    void deveRetornarNaoEncontradoQuandoUsuarioNaoExiste() {
        UUID id = UUID.randomUUID();
        when(service.pegarPorId(id)).thenReturn(Optional.empty());

        var resposta = controller.pegarPorId(id.toString());

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
        assertNull(resposta.getBody());
    }

    @Test
    void deveRetornarListaDeUsuarios() {
        List<Usuario> usuarios = List.of(new Usuario());
        when(service.pegarTodosUsuarios()).thenReturn(usuarios);

        assertEquals(usuarios, controller.pegarTodosUsuarios());
    }

    @Test
    void deveRetornarListaDeTecnicos() {
        List<Tecnico> tecnicos = List.of(new Tecnico());
        when(service.pegarTodosTecnicos()).thenReturn(tecnicos);

        assertEquals(tecnicos, controller.pegarTodosTecnicos());
    }
}
