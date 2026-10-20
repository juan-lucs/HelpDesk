package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {
    private final ComentarioService service;

    @PostMapping
    public void criarComentario(@RequestBody ComentariosChamado com, Chamado chamado, Usuario autor) {
        service.criarComentario(com, chamado, autor);
    }
}
