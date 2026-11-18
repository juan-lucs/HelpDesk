package com.juanlucas.helpdesk.controller.DTO;

import com.juanlucas.helpdesk.Model.Entity.Usuario;

import java.util.UUID;

public record UsuarioDTO(
        UUID id,
        String nome,
        String email) {
    public Usuario mapearparaUsuario() {
        var u = new Usuario();
        u.setNome(this.nome);
        u.setEmail(this.email);
        return u;
    }
}
