package com.juanlucas.helpdesk.controller.DTO;

import com.juanlucas.helpdesk.Enum.EspecialidadeEnum;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;

import java.util.UUID;

// Como o tipo aqui já é EspecialidadeEnum, o Spring tenta converter o valor recebido
                                                    // antes mesmo de chamar o Controller que usa esse DTO, e caso não exista no enum, ele devolve
                                                    // erro 400
public record TecnicoDTO(UUID id, String nome, String email, EspecialidadeEnum especialidade) {

    public Tecnico mapearparaTenico() {
        var t = new Tecnico();
        t.setNome(this.nome);
        t.setEmail(this.email);
        t.setEspecialidade(especialidade);
        return t;
    }
}
