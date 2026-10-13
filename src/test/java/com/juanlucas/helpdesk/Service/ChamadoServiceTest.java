package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.Statuschamado;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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
        tecnico.setEspecialidade(com.juanlucas.helpdesk.Enum.EspecialidadeEnum.values()[0]);
        return usuarioRepository.save(tecnico);
    }

    private Chamado criarChamado(Usuario usuario, Statuschamado status) {
        Chamado chamado = new Chamado();
        chamado.setTitulo("Chamado de teste");
        chamado.setDescricao("Descrição do chamado de teste");
        chamado.setCategoria(CategoriaChamado.SOFTWARE);
        chamado.setPrioridade(PrioridadeChamado.MEDIA);
        chamado.setStatus(status);
        chamado.setUsuarioCriador(usuario);
        return chamadoRepository.save(chamado);
    }

    @Test
    void deveDefinirDataDeCriacaoAoCriarChamado() {
        Usuario usuario = criarUsuario();
        Chamado chamado = new Chamado();
        chamado.setTitulo("Novo chamado");
        chamado.setDescricao("Descrição");
        chamado.setCategoria(CategoriaChamado.SOFTWARE);
        chamado.setPrioridade(PrioridadeChamado.MEDIA);
        chamado.setStatus(Statuschamado.ABERTO);
        chamado.setUsuarioCriador(usuario);

        service.criarChamado(chamado);

        assertNotNull(chamado.getDataCriacao());
        chamadoRepository.delete(chamado);
        usuarioRepository.delete(usuario);
    }

    @Test
    void deveAtribuirTecnicoEAlterarStatus() {
        Usuario usuario = criarUsuario();
        Tecnico tecnico = criarTecnico();
        Chamado chamado = criarChamado(usuario, Statuschamado.ABERTO);

        service.atribuirTecnico(chamado.getId().toString(), tecnico.getId().toString());

        Chamado atualizado = chamadoRepository.findById(chamado.getId()).orElseThrow();
        assertEquals(Statuschamado.EM_ATENDIMENTO, atualizado.getStatus());
        assertEquals(tecnico.getId(), atualizado.getTecnicoResponsavel().getId());

        chamadoRepository.delete(chamado);
        usuarioRepository.delete(tecnico);
        usuarioRepository.delete(usuario);
    }

    @Test
    void deveImpedirAtribuicaoQuandoChamadoJaTemTecnico() {
        Usuario usuario = criarUsuario();
        Tecnico tecnico = criarTecnico();
        Chamado chamado = criarChamado(usuario, Statuschamado.ABERTO);
        chamado.setTecnicoResponsavel(tecnico);
        chamadoRepository.save(chamado);

        assertThrows(ChamadocomTecnicojaResponsavelException.class,
                () -> service.atribuirTecnico(chamado.getId().toString(), tecnico.getId().toString()));

        chamadoRepository.delete(chamado);
        usuarioRepository.delete(tecnico);
        usuarioRepository.delete(usuario);
    }

    @Test
    void deveImpedirAtribuicaoParaChamadoFechado() {
        Usuario usuario = criarUsuario();
        Tecnico tecnico = criarTecnico();
        Chamado chamado = criarChamado(usuario, Statuschamado.FECHADO);

        assertThrows(ChamadoFechadoouCanceladoException.class,
                () -> service.atribuirTecnico(chamado.getId().toString(), tecnico.getId().toString()));

        chamadoRepository.delete(chamado);
        usuarioRepository.delete(tecnico);
        usuarioRepository.delete(usuario);
    }

    @Test
    void devePermitirCancelarChamadoSemTecnico() {
        Usuario usuario = criarUsuario();
        Chamado chamado = criarChamado(usuario, Statuschamado.ABERTO);

        service.alterarStatus(chamado.getId().toString(), Statuschamado.CANCELADO);

        assertEquals(Statuschamado.CANCELADO,
                chamadoRepository.findById(chamado.getId()).orElseThrow().getStatus());

        chamadoRepository.delete(chamado);
        usuarioRepository.delete(usuario);
    }
}
