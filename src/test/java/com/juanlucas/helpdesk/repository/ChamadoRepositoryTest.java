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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ChamadoRepositoryTest {

    @Autowired
    private ChamadoRepository repository;

    @Autowired
    private ChamadoService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveCriarChamadoComStatusInicialEDataDeCriacao() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail("usuario-chamado-" + UUID.randomUUID() + "@example.com");
        usuarioRepository.save(usuario);

        Chamado chamado = new Chamado();
        chamado.setTitulo("Título de teste");
        chamado.setDescricao("Descrição de teste");
        chamado.setCategoria(CategoriaChamado.REDE);
        chamado.setPrioridade(PrioridadeChamado.ALTA);
        chamado.setUsuarioCriador(usuario);

        service.criarChamado(chamado);

        assertNotNull(chamado.getId());
        assertNotNull(chamado.getDataCriacao());
        assertEquals(Statuschamado.ABERTO, chamado.getStatus());
        assertTrue(repository.findById(chamado.getId()).isPresent());
    }
}
