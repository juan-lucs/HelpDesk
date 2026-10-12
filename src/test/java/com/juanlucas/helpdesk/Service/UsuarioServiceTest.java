package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.EspecialidadeEnum;
import com.juanlucas.helpdesk.Exception.EmailJaCadastradoException;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    @Test
    void deveSalvarUsuarioComEmailNovo() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail("teste@example.com");
        when(repository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(repository.save(usuario)).thenReturn(usuario);

        Usuario salvo = service.salvarUsuario(usuario);

        assertSame(usuario, salvo);
        verify(repository).save(usuario);
    }

    @Test
    void deveImpedirUsuarioComEmailDuplicado() {
        Usuario usuario = new Usuario();
        usuario.setEmail("duplicado@example.com");
        when(repository.existsByEmail(usuario.getEmail())).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> service.salvarUsuario(usuario));
        verify(repository, never()).save(any());
    }

    @Test
    void deveSalvarTecnicoComEspecialidade() throws Exception {
        Tecnico tecnico = new Tecnico();
        tecnico.setNome("Técnico de teste");
        tecnico.setEmail("tecnico@example.com");
        tecnico.setEspecialidade(EspecialidadeEnum.values()[0]);
        when(repository.existsByEmail(tecnico.getEmail())).thenReturn(false);
        when(repository.save(tecnico)).thenReturn(tecnico);

        Tecnico salvo = service.salvarTecnico(tecnico);

        assertSame(tecnico, salvo);
        verify(repository).save(tecnico);
    }

    @Test
    void deveImpedirTecnicoSemEspecialidade() {
        Tecnico tecnico = new Tecnico();
        tecnico.setEmail("tecnico@example.com");
        when(repository.existsByEmail(tecnico.getEmail())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.salvarTecnico(tecnico));
        verify(repository, never()).save(any());
    }

    @Test
    void deveBuscarUsuarioPorId() {
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        when(repository.findById(id)).thenReturn(Optional.of(usuario));

        assertTrue(service.pegarPorId(id).isPresent());
        assertSame(usuario, service.pegarPorId(id).orElseThrow());
    }

    @Test
    void deveRetornarTodosUsuarios() {
        List<Usuario> usuarios = List.of(new Usuario(), new Usuario());
        when(repository.buscarUsuarios()).thenReturn(usuarios);

        assertEquals(usuarios, service.pegarTodosUsuarios());
    }

    @Test
    void deveRetornarTodosTecnicos() {
        List<Tecnico> tecnicos = List.of(new Tecnico());
        when(repository.buscarTecnicos()).thenReturn(tecnicos);

        assertEquals(tecnicos, service.pegarTodosTecnicos());
    }
}
