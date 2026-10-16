package com.juanlucas.helpdesk.Enum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatuschamadoTest {

    @Test
    void devePermitirTransicoesImplementadasNoEnum() {
        assertTrue(Statuschamado.ABERTO.alteracao(Statuschamado.CANCELADO));
        assertTrue(Statuschamado.ABERTO.alteracao(Statuschamado.EM_ATENDIMENTO));
        assertTrue(Statuschamado.ABERTO.alteracao(Statuschamado.AGUARDANDO_USUARIO));

        assertTrue(Statuschamado.EM_ATENDIMENTO.alteracao(Statuschamado.AGUARDANDO_USUARIO));
        assertTrue(Statuschamado.EM_ATENDIMENTO.alteracao(Statuschamado.CANCELADO));

        assertTrue(Statuschamado.AGUARDANDO_USUARIO.alteracao(Statuschamado.EM_ATENDIMENTO));
        assertTrue(Statuschamado.AGUARDANDO_USUARIO.alteracao(Statuschamado.CANCELADO));

        // Comportamento atualmente definido no enum.
        assertTrue(Statuschamado.FECHADO.alteracao(Statuschamado.EM_ATENDIMENTO));
    }

    @Test
    void deveBloquearTransicoesNaoImplementadasNoEnum() {
        assertFalse(Statuschamado.CANCELADO.alteracao(Statuschamado.EM_ATENDIMENTO));
        assertFalse(Statuschamado.CANCELADO.alteracao(Statuschamado.ABERTO));
        assertFalse(Statuschamado.ABERTO.alteracao(Statuschamado.FECHADO));
        assertFalse(Statuschamado.EM_ATENDIMENTO.alteracao(Statuschamado.ABERTO));
        assertFalse(Statuschamado.AGUARDANDO_USUARIO.alteracao(Statuschamado.ABERTO));
    }
}
