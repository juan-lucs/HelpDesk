package com.juanlucas.helpdesk.repository;

import com.juanlucas.helpdesk.Exception.EmailJaCadastradoException;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioService service;

    @Test
    void deveRejeitarTecnicoSemEspecialidade() {
        Tecnico tecnico = new Tecnico();
        tecnico.setNome("Técnico");
        tecnico.setEmail("sem-especialidade-" + UUID.randomUUID() + "@example.com");

        assertThrows(IllegalArgumentException.class, () -> service.salvarTecnico(tecnico));
    }

    @Test
    void deveRejeitarEmailDuplicado() {
        String email = "teste-" + UUID.randomUUID() + "@example.com";

        Usuario existente = new Usuario();
        existente.setNome("Usuário existente");
        existente.setEmail(email);
        service.salvarUsuario(existente);

        Usuario repetido = new Usuario();
        repetido.setNome("Outro usuário");
        repetido.setEmail(email);

        assertThrows(EmailJaCadastradoException.class, () -> service.salvarUsuario(repetido));
    }
}
