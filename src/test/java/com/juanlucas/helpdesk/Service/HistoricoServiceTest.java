package com.juanlucas.helpdesk.Service;


import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.EspecialidadeEnum;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Historico;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.HistoricoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class HistoricoServiceTest {
    @Autowired
    ChamadoService serviceChamado;
    @Autowired
    UsuarioRepository usuarioRepository;

    @Test
    void deveRegistrarChamadoCriado(){
        var autor = new Usuario();
        autor.setNome("Usuário de teste");
        autor.setEmail("autor.chamado-" + UUID.randomUUID() + "@teste.com");
        autor = usuarioRepository.save(autor);

        var c = new Chamado();
        c.setTitulo("TESTE TITULO");
        c.setDescricao("TESTE DESCRIÇÃO");
        c.setCategoria(CategoriaChamado.REDE);
        c.setPrioridade(PrioridadeChamado.ALTA);

        // c.setStatus(); NÃO PRECISA PORQUE O CHAMADO JÁ É CRIADO COM STATUS = ABERTO
        serviceChamado.criarChamado(c, autor.getId());

        assertEquals(1, c.getHistorico().size());
        assertEquals("Chamado aberto por Usuário de teste", c.getHistorico().getFirst().getDescricao());
    }

    @Test
    void deveRegistrarAtribuicaoTecnico(){
        var tecnicoResponsa = new Tecnico();
        tecnicoResponsa.setNome("Usuário de teste");
        tecnicoResponsa.setEmail("autor.chamado-" + UUID.randomUUID() + "@teste.com");
        tecnicoResponsa.setEspecialidade(EspecialidadeEnum.SOFTWARE);
        tecnicoResponsa = usuarioRepository.save(tecnicoResponsa);

        var c = new Chamado();
        c.setTitulo("TESTE TITULO");
        c.setDescricao("TESTE DESCRIÇÃO");
        c.setCategoria(CategoriaChamado.REDE);
        c.setPrioridade(PrioridadeChamado.ALTA);
        serviceChamado.criarChamado(c, tecnicoResponsa.getId());

        serviceChamado.atribuirTecnico(c.getId().toString(), tecnicoResponsa.getId().toString());

        var historico = serviceChamado.pegarTodoHistoricoPorId(c.getId());

        assertEquals("Chamado atribuído ao técnico Usuário de teste", historico.get(1).getDescricao());
    }

    @Test
    void deveRegistrarAlteracaoStatus() {
        var tecnicoResponsa = new Tecnico();
        tecnicoResponsa.setNome("Usuário de teste");
        tecnicoResponsa.setEmail("autor.chamado-" + UUID.randomUUID() + "@teste.com");
        tecnicoResponsa.setEspecialidade(EspecialidadeEnum.SOFTWARE);
        tecnicoResponsa = usuarioRepository.save(tecnicoResponsa);

        var c = new Chamado();
        c.setTitulo("TESTE TITULO");
        c.setDescricao("TESTE DESCRIÇÃO");
        c.setCategoria(CategoriaChamado.REDE);
        c.setPrioridade(PrioridadeChamado.ALTA);
        serviceChamado.criarChamado(c, tecnicoResponsa.getId());

        serviceChamado.alterarStatus(String.valueOf(c.getId()), StatusChamado.CANCELADO, String.valueOf(tecnicoResponsa.getId()));
        var historico = serviceChamado.pegarTodoHistoricoPorId(c.getId());

        assertEquals("Status alterado de " + StatusChamado.ABERTO
                + " para " + StatusChamado.CANCELADO + " por: " + tecnicoResponsa.getNome(), historico.get(1).getDescricao());

    }
    }
