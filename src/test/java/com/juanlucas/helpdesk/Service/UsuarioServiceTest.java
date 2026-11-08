package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.EspecialidadeEnum;
import com.juanlucas.helpdesk.Exception.EmailJaCadastradoException;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UsuarioServiceTest {

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository repository;

    private String emailUnico(String prefixo) {
        return prefixo + "-" + UUID.randomUUID() + "@example.com";
    }

    @Test
    void deveSalvarUsuarioComEmailNovo() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail(emailUnico("usuario"));
        var test = usuario.getEmail();
        service.salvarUsuario(usuario);

        assertNotNull(usuario.getId());
        assertEquals(test, usuario.getEmail());
        assertTrue(repository.findById(usuario.getId()).isPresent());
    }

    @Test
    void deveImpedirUsuarioComEmailDuplicado() {
        Usuario existente = new Usuario();
        existente.setNome("Usuário existente");
        existente.setEmail(emailUnico("duplicado"));
        service.salvarUsuario(existente);

        Usuario repetido = new Usuario();
        repetido.setNome("Outro usuário");
        repetido.setEmail(existente.getEmail());

        assertThrows(EmailJaCadastradoException.class,
                () -> service.salvarUsuario(repetido));
    }

    @Test
    void deveSalvarTecnicoComEspecialidade() {
        Tecnico tecnico = new Tecnico();
        tecnico.setNome("Técnico de teste");
        tecnico.setEmail(emailUnico("tecnico"));
        tecnico.setEspecialidade(EspecialidadeEnum.values()[0]);
        var testeEmail = tecnico.getEmail();
        var testeEspeci = tecnico.getEspecialidade();
        service.salvarTecnico(tecnico);

        assertNotNull(tecnico.getId());
        assertEquals(testeEmail, tecnico.getEmail());
        assertEquals(testeEspeci, tecnico.getEspecialidade());
    }

    @Test
    void deveImpedirTecnicoSemEspecialidade() {
        Tecnico tecnico = new Tecnico();
        tecnico.setNome("Técnico de teste");
        tecnico.setEmail(emailUnico("tecnico-sem-especialidade"));

        assertThrows(IllegalArgumentException.class,
                () -> service.salvarTecnico(tecnico));
    }

    @Test
    void deveBuscarUsuarioPorId() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário para busca");
        usuario.setEmail(emailUnico("busca"));
        service.salvarUsuario(usuario);

        assertEquals(usuario.getId(), service.pegarPorId(usuario.getId()).orElseThrow().getId());
    }

    @Test
    void deveRetornarVazioQuandoUsuarioNaoExiste() {
        assertTrue(service.pegarPorId(UUID.randomUUID()).isEmpty());
    }
}
