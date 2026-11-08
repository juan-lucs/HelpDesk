package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.EspecialidadeEnum;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Exception.ChamadoFechadoouCanceladoException;
import com.juanlucas.helpdesk.Exception.ChamadocomTecnicojaResponsavelException;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ChamadoServiceTest {

    @Autowired
    private ChamadoService service;

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário teste");
        usuario.setEmail("usuario-" + UUID.randomUUID() + "@example.com");
        return usuarioRepository.save(usuario);
    }

    private Tecnico criarTecnico() {
        Tecnico tecnico = new Tecnico();
        tecnico.setNome("Técnico teste");
        tecnico.setEmail("tecnico-" + UUID.randomUUID() + "@example.com");
        tecnico.setEspecialidade(EspecialidadeEnum.values()[0]);
        return usuarioRepository.save(tecnico);
    }

    private Chamado criarChamado() {
        Chamado chamado = new Chamado();
        chamado.setTitulo("Chamado de teste");
        chamado.setDescricao("Descrição do chamado de teste");
        chamado.setCategoria(CategoriaChamado.SOFTWARE);
        chamado.setPrioridade(PrioridadeChamado.MEDIA);
        chamado.setDataCriacao(LocalDateTime.now());
        return chamado;
    }

    @Test
    void deveDefinirDataDeCriacaoAoCriarChamado() {
        Usuario usuario = criarUsuario();
        Chamado chamado = new Chamado();
        chamado.setTitulo("Novo chamado");
        chamado.setDescricao("Descrição");
        chamado.setCategoria(CategoriaChamado.SOFTWARE);
        chamado.setPrioridade(PrioridadeChamado.MEDIA);

        service.criarChamado(chamado, usuario.getId());

        assertNotNull(chamado.getDataCriacao());
    }

    @Test
    void deveAtribuirTecnicoEAlterarStatus() {
        Usuario usuario = criarUsuario();
        Tecnico tecnico = criarTecnico();
        Chamado chamado = criarChamado();
        service.criarChamado(chamado, usuario.getId());
        service.atribuirTecnico(chamado.getId().toString(), tecnico.getId().toString());

        Chamado atualizado = chamadoRepository.findById(chamado.getId()).orElseThrow();
        assertEquals(StatusChamado.EM_ATENDIMENTO, atualizado.getStatus());
        assertEquals(tecnico.getId(), atualizado.getTecnicoResponsavel().getId());
    }

    @Test
    void deveImpedirAtribuicaoQuandoChamadoJaTemTecnico() {
        Usuario usuario = criarUsuario();
        Tecnico tecnico = criarTecnico();
        Chamado chamado = criarChamado();
        chamado.setTecnicoResponsavel(tecnico);
        service.criarChamado(chamado, usuario.getId());

        assertThrows(ChamadocomTecnicojaResponsavelException.class,
                () -> service.atribuirTecnico(chamado.getId().toString(), tecnico.getId().toString()));

    }

    @Test
    void deveImpedirAtribuicaoParaChamadoFechado() {
        Usuario usuario = criarUsuario();
        Tecnico tecnico = criarTecnico();
        Chamado chamado = criarChamado();
        chamado.setStatus(StatusChamado.FECHADO);
        service.criarChamado(chamado, usuario.getId());
        assertThrows(ChamadoFechadoouCanceladoException.class,
                () -> service.atribuirTecnico(chamado.getId().toString(), tecnico.getId().toString()));
    }

    @Test
    void devePermitirCancelarChamadoSemTecnico() {
        Usuario usuario = criarUsuario();
        Chamado chamado = criarChamado();
        service.criarChamado(chamado, usuario.getId());
        service.alterarStatus(chamado.getId().toString(), StatusChamado.CANCELADO, String.valueOf(usuario.getId()));

        assertEquals(StatusChamado.CANCELADO,
                chamadoRepository.findById(chamado.getId()).orElseThrow().getStatus());
    }

    }
