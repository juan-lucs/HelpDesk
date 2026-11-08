package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.ComentarioService;
import com.juanlucas.helpdesk.controller.DTO.ComentarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {
    private final ComentarioService service;

    @PostMapping("/{id}")
    public ResponseEntity<Void> criarComentario(@PathVariable("id") String idChamado, @RequestBody ComentarioDTO dto) {
        var comentarioEntidade = dto.mapearparaChamado();
        service.criarComentario(comentarioEntidade, idChamado, dto.idAutor());

        // http://localhost:8080/comentarios/ID DO comentarios QUE FOI CRIADO PELO BANCOOO
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(comentarioEntidade.getId()).toUri();
        return ResponseEntity.created(location).build();
    }
}
