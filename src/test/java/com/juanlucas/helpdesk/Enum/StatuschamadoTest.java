package com.juanlucas.helpdesk.Enum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatuschamadoTest {

    @Test
    void devePermitirTransicoesValidas() {
        assertTrue(Statuschamado.ABERTO.alteracao(Statuschamado.CANCELADO));
        assertTrue(Statuschamado.ABERTO.alteracao(Statuschamado.EM_ATENDIMENTO));
        assertTrue(Statuschamado.ABERTO.alteracao(Statuschamado.AGUARDANDO_USUARIO));
        assertTrue(Statuschamado.EM_ATENDIMENTO.alteracao(Statuschamado.RESOLVIDO));
        assertTrue(Statuschamado.AGUARDANDO_USUARIO.alteracao(Statuschamado.EM_ATENDIMENTO));
        assertTrue(Statuschamado.RESOLVIDO.alteracao(Statuschamado.EM_ATENDIMENTO));
    }

    @Test
    void deveBloquearTransicoesInvalidas() {
        assertFalse(Statuschamado.FECHADO.alteracao(Statuschamado.ABERTO));
        assertFalse(Statuschamado.CANCELADO.alteracao(Statuschamado.EM_ATENDIMENTO));
        assertFalse(Statuschamado.ABERTO.alteracao(Statuschamado.FECHADO));
        assertFalse(Statuschamado.EM_ATENDIMENTO.alteracao(Statuschamado.FECHADO));
    }
}
