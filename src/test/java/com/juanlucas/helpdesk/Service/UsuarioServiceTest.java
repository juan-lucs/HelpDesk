package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.EspecialidadeEnum;
import com.juanlucas.helpdesk.Exception.EmailJaCadastradoException;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsuarioServiceTest {

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository repository;

    @Test
    void deveSalvarUsuarioComEmailNovo() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail("usuario-" + UUID.randomUUID() + "@example.com");

        Usuario salvo = service.salvarUsuario(usuario);

        assertNotNull(salvo.getId());
        assertEquals(usuario.getEmail(), salvo.getEmail());
        repository.delete(salvo);
    }

    @Test
    void deveImpedirUsuarioComEmailDuplicado() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail("duplicado-" + UUID.randomUUID() + "@example.com");
        Usuario salvo = service.salvarUsuario(usuario);

        Usuario repetido = new Usuario();
        repetido.setNome("Outro usuário");
        repetido.setEmail(salvo.getEmail());

        assertThrows(EmailJaCadastradoException.class, () -> service.salvarUsuario(repetido));
        repository.delete(salvo);
    }

    @Test
    void deveSalvarTecnicoComEspecialidade() throws Exception {
        Tecnico tecnico = new Tecnico();
        tecnico.setNome("Técnico de teste");
        tecnico.setEmail("tecnico-" + UUID.randomUUID() + "@example.com");
        tecnico.setEspecialidade(EspecialidadeEnum.values()[0]);

        Tecnico salvo = service.salvarTecnico(tecnico);

        assertNotNull(salvo.getId());
        assertEquals(tecnico.getEmail(), salvo.getEmail());
        repository.delete(salvo);
    }

    @Test
    void deveImpedirTecnicoSemEspecialidade() {
        Tecnico tecnico = new Tecnico();
        tecnico.setNome("Técnico de teste");
        tecnico.setEmail("tecnico-sem-especialidade-" + UUID.randomUUID() + "@example.com");

        assertThrows(IllegalArgumentException.class, () -> service.salvarTecnico(tecnico));
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Busca");
        usuario.setEmail("busca-" + UUID.randomUUID() + "@example.com");
        Usuario salvo = service.salvarUsuario(usuario);

        assertTrue(service.pegarPorId(salvo.getId()).isPresent());
        repository.delete(salvo);
    }
}
