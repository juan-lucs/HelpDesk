package com.juanlucas.helpdesk.controller.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Model.Entity.Chamado;

import java.util.UUID;

public record ChamadoDTO(String titulo, String descricao, CategoriaChamado categoria,
                         PrioridadeChamado prioridade, UUID usuarioCriadorId) {
    public Chamado mapearparaChamado() {
        var c = new Chamado();
        c.setTitulo(this.titulo);
        c.setDescricao(this.descricao);
        c.setCategoria(categoria);
        c.setPrioridade(prioridade);
        return c;
    }
}
