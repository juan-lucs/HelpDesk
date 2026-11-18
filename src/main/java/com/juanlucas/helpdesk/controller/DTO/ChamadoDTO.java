package com.juanlucas.helpdesk.controller.DTO;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;

public record ChamadoDTO(String titulo, String descricao, CategoriaChamado categoria, PrioridadeChamado prioridade, String idCriador ){
    public Chamado mapearparaChamado() {
        var c = new Chamado();
        c.setTitulo(this.titulo);
        c.setDescricao(this.descricao);
        c.setCategoria(categoria);
        c.setPrioridade(prioridade);
        c.setUsuarioCriador(null);
        return c;
    }
}
