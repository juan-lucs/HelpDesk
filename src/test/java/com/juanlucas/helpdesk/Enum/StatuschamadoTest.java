package com.juanlucas.helpdesk.Enum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatuschamadoTest {

    @Test
    void devePermitirTransicoesValidas() {
        assertTrue(StatusChamado.ABERTO.alteracao(StatusChamado.CANCELADO));
        assertTrue(StatusChamado.ABERTO.alteracao(StatusChamado.EM_ATENDIMENTO));
        assertTrue(StatusChamado.ABERTO.alteracao(StatusChamado.AGUARDANDO_USUARIO));
        assertTrue(StatusChamado.EM_ATENDIMENTO.alteracao(StatusChamado.CANCELADO ));
        assertTrue(StatusChamado.EM_ATENDIMENTO.alteracao(StatusChamado.AGUARDANDO_USUARIO));
        assertTrue(StatusChamado.AGUARDANDO_USUARIO.alteracao(StatusChamado.EM_ATENDIMENTO));
        assertTrue(StatusChamado.AGUARDANDO_USUARIO.alteracao(StatusChamado.CANCELADO));
        assertTrue(StatusChamado.FECHADO.alteracao(StatusChamado.EM_ATENDIMENTO));
    }

    @Test
    void deveBloquearTransicoesInvalidas() {
        assertFalse(StatusChamado.FECHADO.alteracao(StatusChamado.ABERTO));
        assertFalse(StatusChamado.CANCELADO.alteracao(StatusChamado.EM_ATENDIMENTO));
        assertFalse(StatusChamado.CANCELADO.alteracao(StatusChamado.ABERTO));
        assertFalse(StatusChamado.CANCELADO.alteracao(StatusChamado.CANCELADO));
        assertFalse(StatusChamado.CANCELADO.alteracao(StatusChamado.FECHADO));
        assertFalse(StatusChamado.ABERTO.alteracao(StatusChamado.FECHADO));
        assertFalse(StatusChamado.EM_ATENDIMENTO.alteracao(StatusChamado.FECHADO));
    }
}
