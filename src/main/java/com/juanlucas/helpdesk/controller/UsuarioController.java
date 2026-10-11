package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService service;

    @PostMapping("/usuarios")
    public Usuario salvarUsuario(@RequestBody Usuario usuario) {
        return service.salvarUsuario(usuario);
    }

    @PostMapping("/tecnicos")
    public Tecnico salvarTecnico(@RequestBody Tecnico tecnico) {
        return service.salvarTecnico(tecnico);
    }
}
