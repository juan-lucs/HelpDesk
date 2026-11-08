package com.juanlucas.helpdesk.controller.DTO;

import com.juanlucas.helpdesk.Model.Entity.Usuario;

public record UsuarioDTO(String nome,
                         String email) {
    public Usuario mapearparaUsuario() {
        var u = new Usuario();
        u.setNome(this.nome);
        u.setEmail(this.email);
        return u;
    }
}
