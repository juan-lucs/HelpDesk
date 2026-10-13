package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Enum.Statuschamado;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Service.ChamadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chamado")
@RequiredArgsConstructor
public class ChamadoController {
    private final ChamadoService service;


    @PostMapping("/chamado")
    public void criarChamado(@RequestBody Chamado c) {
        service.criarChamado(c);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chamado> pegarPorId(@PathVariable String id) {
        return service.pegarPorId(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Chamado> pegarTodos() {
        return service.pegarTodos();
    }

    @PatchMapping("/atribuirtecnicochamado")
    public void atribuirTecnico(@RequestParam String idChamado, String idTecnico) {
        service.atribuirTecnico(idChamado, idTecnico);
    }

    @PatchMapping("/alterarStatus/{id}")
    public void alterarStatus(@PathVariable("id") String idChamado, String statusNovo) {
        service.alterarStatus(idChamado, Statuschamado.valueOf(statusNovo));
    }
}
