package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.ComentarioService;
import com.juanlucas.helpdesk.controller.DTO.ComentarioDTO;
import com.juanlucas.helpdesk.controller.DTO.ComentarioRetornoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

// TODO: adequar rota e expor consultas; consultar comentários no contrato-api.md.
@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {
    private final ComentarioService service;

    @PostMapping()
    public ResponseEntity<Void> criarComentario(@RequestBody ComentarioDTO dto) {
        var comentarioEntidade = dto.mapearparaChamado();
        service.criarComentario(comentarioEntidade, dto.idChamado(), dto.idAutor());

        // http://localhost:8080/comentarios/ID DO comentarios QUE FOI CRIADO PELO BANCOOO
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(comentarioEntidade.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{idChamado}")
    public ResponseEntity<List<ComentarioRetornoDTO>> pegarTodos(@PathVariable UUID idChamado) {
        return ResponseEntity.ok(
                service.pegarTodos(idChamado).stream()
                .map(comentario -> new ComentarioRetornoDTO(comentario.getAutor().getNome(), comentario.getConteudo()))
                .toList());
    }
}
