package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Service.ChamadoService;
import com.juanlucas.helpdesk.controller.DTO.ChamadoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chamado")
@RequiredArgsConstructor
public class ChamadoController {
    private final ChamadoService service;


    @PostMapping("/chamado")
    public ResponseEntity<Void> criarChamado(@RequestBody ChamadoDTO c) {
        var chamadoEntidade = c.mapearparaChamado();
        service.criarChamado(chamadoEntidade, UUID.fromString(c.idCriador()));

        // http://localhost:8080/chamado/ID DO chamado QUE FOI CRIADO PELO BANCOOO
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(chamadoEntidade.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadoDTO> obterDetalhes(@PathVariable String id) {
        var idChamado = UUID.fromString(id);
        return service.pegarPorId(UUID.fromString(id))
                .map(chamado -> new ChamadoDTO(chamado.getTitulo(), chamado.getDescricao(), chamado.getCategoria(), chamado.getPrioridade(), chamado.getUsuarioCriador().getId().toString()))
                .map(dto -> ResponseEntity.ok(dto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Chamado> filtrar(@RequestParam(required = false) String titulo,
                                 @RequestParam(required = false) CategoriaChamado categoria,
                                 @RequestParam(required = false) PrioridadeChamado prioridade,
                                 @RequestParam(required = false) LocalDate dataCriacao) {
        return service.filtrar(titulo, categoria, prioridade, dataCriacao);
    }

    @PatchMapping("/atribuirtecnicochamado")
    public ResponseEntity<Void> atribuirTecnico(@RequestParam String idChamado, String idTecnico) {
        service.atribuirTecnico(idChamado, idTecnico);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/alterarStatus/{id}")
    public ResponseEntity<Void> alterarStatus(@PathVariable("id") String idChamado, String statusNovo, String idAutorDaAlteracao) {
        service.alterarStatus(idChamado, StatusChamado.valueOf(statusNovo),idAutorDaAlteracao);
        return ResponseEntity.noContent().build();
    }
}
