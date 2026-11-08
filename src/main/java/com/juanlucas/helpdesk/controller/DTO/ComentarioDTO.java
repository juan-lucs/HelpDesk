package com.juanlucas.helpdesk.controller.DTO;

import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;

public record ComentarioDTO(String idAutor, String conteudo) {
    public ComentariosChamado mapearparaChamado() {
        var c = new ComentariosChamado();
        c.setConteudo(conteudo);
        return c;
    }
}
