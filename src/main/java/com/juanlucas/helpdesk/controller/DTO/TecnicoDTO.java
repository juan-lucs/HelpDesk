package com.juanlucas.helpdesk.controller.DTO;

import com.juanlucas.helpdesk.Enum.EspecialidadeEnum;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;

public record TecnicoDTO(String nome, String email, EspecialidadeEnum especialidade) {

    public Tecnico mapearparaTenico() {
        var t = new Tecnico();
        t.setNome(this.nome);
        t.setEmail(this.email);
        return t;
    }
}
