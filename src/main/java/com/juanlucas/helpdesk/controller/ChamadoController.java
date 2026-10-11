package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.Service.ChamadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chamado")
@RequiredArgsConstructor
public class ChamadoController {
    private final ChamadoService service;


    @PostMapping("/chamado")
    public void criarChamado(@RequestBody Chamado c) {
        service.criarChamado(c);
    }
}
