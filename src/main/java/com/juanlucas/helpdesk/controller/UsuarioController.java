package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService service;

    @PostMapping
    public Usuario salvarUsuario(@RequestBody Usuario usuario) {
        return service.salvarUsuario(usuario);
    }

    @PostMapping("/tecnico")
    public Tecnico salvarTecnico(@RequestBody Tecnico tecnico) {
        return service.salvarTecnico(tecnico);
    }

    @GetMapping("{id}")
    public ResponseEntity<Usuario> pegarPorId(@PathVariable("id") String id) {
        var idUser = UUID.fromString(id);

        return service.pegarPorId(idUser).map(autor -> ResponseEntity.ok(autor)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/usuarios")
    public List<Usuario> pegarTodosUsuarios() {
        return service.pegarTodosUsuarios();
    }

    @GetMapping("/tecnicos")
    public List<Tecnico> pegarTodosTecnicos() {
        return service.pegarTodosTecnicos();
    }


}
