package com.juanlucas.helpdesk.repository;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.ChamadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
        usuario.setEmail("usuario.chamado-" + UUID.randomUUID() + "@teste.com");
        usuario = usuarioRepository.save(usuario);

        var c = new Chamado();
        c.setTitulo("TESTE TITULO");
        c.setDescricao("TESTE DESCRICAO");
        c.setCategoria(CategoriaChamado.REDE);
        c.setPrioridade(PrioridadeChamado.ALTA);
        // c.setStatus(); NAO PRECISA PORQUE O CHAMADO JÁ É CRIADO COM STATUS = ABERTO
        service.criarChamado(c, usuario);

        assertNotNull(c.getId());
        assertNotNull(c.getDataCriacao());
        assertEquals(StatusChamado.ABERTO, c.getStatus());
        assertTrue(repository.findById(c.getId()).isPresent());

//        repository.delete(c);
//        usuarioRepository.delete(usuario); NÃO PRECISA GRAÇAS AO H2
    }
}
