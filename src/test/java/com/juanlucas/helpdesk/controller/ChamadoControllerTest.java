package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Enum.Statuschamado;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Service.ChamadoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChamadoControllerTest {

    @Mock
    private ChamadoService service;

    @InjectMocks
    private ChamadoController controller;

    @Test
    void deveEncaminharCriacaoDeChamadoAoService() {
        Chamado chamado = new Chamado();

        controller.criarChamado(chamado);

        verify(service).criarChamado(chamado);
    }

    @Test
    void deveRetornarChamadoQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        Chamado chamado = new Chamado();
        when(service.pegarPorId(id)).thenReturn(Optional.of(chamado));

        var resposta = controller.pegarPorId(id.toString());

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertSame(chamado, resposta.getBody());
    }

    @Test
    void deveRetornarNaoEncontradoQuandoChamadoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(service.pegarPorId(id)).thenReturn(Optional.empty());

        var resposta = controller.pegarPorId(id.toString());

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
        assertNull(resposta.getBody());
    }

    @Test
    void deveRetornarListaDeChamados() {
        List<Chamado> chamados = List.of(new Chamado());
        when(service.pegarTodos()).thenReturn(chamados);

        assertEquals(chamados, controller.pegarTodos());
    }

    @Test
    void deveEncaminharAtribuicaoDeTecnicoAoService() {
        String idChamado = UUID.randomUUID().toString();
        String idTecnico = UUID.randomUUID().toString();

        controller.atribuirTecnico(idChamado, idTecnico);

        verify(service).atribuirTecnico(idChamado, idTecnico);
    }

    @Test
    void deveConverterStatusEEncaminharAlteracaoAoService() {
        String idChamado = UUID.randomUUID().toString();

        controller.alterarStatus(idChamado, "RESOLVIDO");

        verify(service).alterarStatus(idChamado, Statuschamado.RESOLVIDO);
    }
}
