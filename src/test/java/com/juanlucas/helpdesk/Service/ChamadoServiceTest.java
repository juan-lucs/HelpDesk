package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Enum.Statuschamado;
import com.juanlucas.helpdesk.Exception.ChamadoFechadoouCanceladoException;
import com.juanlucas.helpdesk.Exception.ChamadocomTecnicojaResponsavelException;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository repositoryChamado;

    @Mock
    private UsuarioRepository repositoryTecnico;

    @InjectMocks
    private ChamadoService service;

    @Test
    void deveDefinirDataDeCriacaoAoCriarChamado() {
        Chamado chamado = new Chamado();

        service.criarChamado(chamado);

        assertNotNull(chamado.getDataCriacao());
        verify(repositoryChamado).save(chamado);
    }

    @Test
    void deveBuscarChamadoPorId() {
        UUID id = UUID.randomUUID();
        Chamado chamado = new Chamado();
        when(repositoryChamado.findById(id)).thenReturn(Optional.of(chamado));

        assertSame(chamado, service.pegarPorId(id).orElseThrow());
    }

    @Test
    void deveRetornarTodosOsChamados() {
        List<Chamado> chamados = List.of(new Chamado(), new Chamado());
        when(repositoryChamado.findAll()).thenReturn(chamados);

        assertEquals(chamados, service.pegarTodos());
    }

    @Test
    void deveAtribuirTecnicoEAlterarStatus() {
        UUID idChamado = UUID.randomUUID();
        UUID idTecnico = UUID.randomUUID();
        Chamado chamado = new Chamado();
        chamado.setStatus(Statuschamado.ABERTO);
        Tecnico tecnico = new Tecnico();
        when(repositoryChamado.findById(idChamado)).thenReturn(Optional.of(chamado));
        when(repositoryTecnico.findById(idTecnico)).thenReturn(Optional.of(tecnico));

        service.atribuirTecnico(idChamado.toString(), idTecnico.toString());

        assertSame(tecnico, chamado.getTecnicoResponsavel());
        assertEquals(Statuschamado.EM_ATENDIMENTO, chamado.getStatus());
        verify(repositoryChamado).save(chamado);
    }

    @Test
    void deveImpedirAtribuicaoQuandoChamadoJaTemTecnico() {
        UUID idChamado = UUID.randomUUID();
        UUID idTecnico = UUID.randomUUID();
        Chamado chamado = new Chamado();
        chamado.setStatus(Statuschamado.ABERTO);
        chamado.setTecnicoResponsavel(new Tecnico());
        when(repositoryChamado.findById(idChamado)).thenReturn(Optional.of(chamado));
        when(repositoryTecnico.findById(idTecnico)).thenReturn(Optional.of(new Tecnico()));

        assertThrows(ChamadocomTecnicojaResponsavelException.class,
                () -> service.atribuirTecnico(idChamado.toString(), idTecnico.toString()));
        verify(repositoryChamado, never()).save(any());
    }

    @Test
    void deveImpedirAtribuicaoParaChamadoFechado() {
        UUID idChamado = UUID.randomUUID();
        UUID idTecnico = UUID.randomUUID();
        Chamado chamado = new Chamado();
        chamado.setStatus(Statuschamado.FECHADO);
        when(repositoryChamado.findById(idChamado)).thenReturn(Optional.of(chamado));
        when(repositoryTecnico.findById(idTecnico)).thenReturn(Optional.of(new Tecnico()));

        assertThrows(ChamadoFechadoouCanceladoException.class,
                () -> service.atribuirTecnico(idChamado.toString(), idTecnico.toString()));
        verify(repositoryChamado, never()).save(any());
    }

    @Test
    void deveImpedirAtribuicaoQuandoUsuarioNaoETecnico() {
        UUID idChamado = UUID.randomUUID();
        UUID idUsuario = UUID.randomUUID();
        Chamado chamado = new Chamado();
        chamado.setStatus(Statuschamado.ABERTO);
        when(repositoryChamado.findById(idChamado)).thenReturn(Optional.of(chamado));
        when(repositoryTecnico.findById(idUsuario)).thenReturn(Optional.of(new Usuario()));

        assertThrows(RuntimeException.class,
                () -> service.atribuirTecnico(idChamado.toString(), idUsuario.toString()));
        verify(repositoryChamado, never()).save(any());
    }

    @Test
    void deveAlterarStatusQuandoTransicaoEValidaETemTecnico() {
        UUID idChamado = UUID.randomUUID();
        Chamado chamado = new Chamado();
        chamado.setStatus(Statuschamado.EM_ATENDIMENTO);
        chamado.setTecnicoResponsavel(new Tecnico());
        when(repositoryChamado.findById(idChamado)).thenReturn(Optional.of(chamado));

        service.alterarStatus(idChamado.toString(), Statuschamado.RESOLVIDO);

        assertEquals(Statuschamado.RESOLVIDO, chamado.getStatus());
        verify(repositoryChamado).save(chamado);
    }

    @Test
    void devePermitirCancelarChamadoSemTecnico() {
        UUID idChamado = UUID.randomUUID();
        Chamado chamado = new Chamado();
        chamado.setStatus(Statuschamado.ABERTO);
        when(repositoryChamado.findById(idChamado)).thenReturn(Optional.of(chamado));

        service.alterarStatus(idChamado.toString(), Statuschamado.CANCELADO);

        assertEquals(Statuschamado.CANCELADO, chamado.getStatus());
        verify(repositoryChamado).save(chamado);
    }

    @Test
    void deveImpedirAlteracaoDeStatusSemTecnicoExcetoCancelamento() {
        UUID idChamado = UUID.randomUUID();
        Chamado chamado = new Chamado();
        chamado.setStatus(Statuschamado.ABERTO);
        when(repositoryChamado.findById(idChamado)).thenReturn(Optional.of(chamado));

        assertThrows(RuntimeException.class,
                () -> service.alterarStatus(idChamado.toString(), Statuschamado.EM_ATENDIMENTO));
        verify(repositoryChamado, never()).save(any());
    }

    @Test
    void deveImpedirTransicaoDeStatusInvalida() {
        UUID idChamado = UUID.randomUUID();
        Chamado chamado = new Chamado();
        chamado.setStatus(Statuschamado.FECHADO);
        when(repositoryChamado.findById(idChamado)).thenReturn(Optional.of(chamado));

        assertThrows(ChamadoFechadoouCanceladoException.class,
                () -> service.alterarStatus(idChamado.toString(), Statuschamado.ABERTO));
        verify(repositoryChamado, never()).save(any());
    }
}
