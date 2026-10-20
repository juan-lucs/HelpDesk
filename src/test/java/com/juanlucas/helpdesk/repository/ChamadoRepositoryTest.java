package com.juanlucas.helpdesk.repository;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.Statuschamado;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.ChamadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChamadoRepositoryTest {
    @Autowired
    ChamadoRepository repository;
    @Autowired
    ChamadoService service;
    @Autowired
    UsuarioRepository usuarioRepository;

    @Test
    public void criarChamado() {
        var usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail("usuario.chamado-" + UUID.randomUUID() + "@teste.com");
        usuario = usuarioRepository.save(usuario);

        var c = new Chamado();
        c.setTitulo("TESTE TITULO");
        c.setDescricao("TESTE DESCRICAO");
        c.setCategoria(CategoriaChamado.REDE);
        c.setPrioridade(PrioridadeChamado.ALTA);
        c.setUsuarioCriador(usuario);
        // c.setStatus(); NAO PRECISA PORQUE O CHAMADO JÁ É CRIADO COM STATUS = ABERTO
        service.criarChamado(c);

        assertNotNull(c.getId());
        assertNotNull(c.getDataCriacao());
        assertEquals(Statuschamado.ABERTO, c.getStatus());
        assertTrue(repository.findById(c.getId()).isPresent());

        repository.delete(c);
        usuarioRepository.delete(usuario);
    }
}