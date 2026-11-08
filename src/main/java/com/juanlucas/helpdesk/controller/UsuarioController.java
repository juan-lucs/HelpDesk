package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.UsuarioService;
import com.juanlucas.helpdesk.controller.DTO.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<Object> salvarUsuario(@RequestBody UsuarioDTO usuario){
        var usuarioEntidade = usuario.mapearparaUsuario();
        service.salvarUsuario(usuarioEntidade);

        // http://localhost:8080/usuarios/ID DO USUARIO QUE FOI CRIADO PELO BANCOOO
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(usuarioEntidade.getId()).toUri();

        return ResponseEntity.created(location).build(); // Uma resposta boa, dizendo que usuario foi criado
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
