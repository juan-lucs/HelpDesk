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
    public void salvarMesmoEmail() {
        String email = "teste-" + UUID.randomUUID() + "@gmail.com";

        var existente = new Usuario();
        existente.setNome("existente");
        existente.setEmail(email);
        var salvo = service.salvarUsuario(existente);

        var repetido = new Usuario();
        repetido.setNome("teste");
        repetido.setEmail(email);

        try {
            assertThrows(
                    EmailJaCadastradoException.class,
                    () -> service.salvarUsuario(repetido)
            );
        } finally {
            repository.delete(salvo);
        }
    }
}
