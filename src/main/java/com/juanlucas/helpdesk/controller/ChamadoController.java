package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Exception.UsuarioNaoEncontradoException;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Service.ChamadoService;
import com.juanlucas.helpdesk.controller.DTO.ChamadoDTO;
import com.juanlucas.helpdesk.controller.DTO.ErroCampo;
import com.juanlucas.helpdesk.controller.DTO.ErroResposta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO: adequar rotas e DTOs de entrada/resposta; consultar chamados no contrato-api.md.
@RestController
@RequestMapping("/chamado")
@RequiredArgsConstructor
public class ChamadoController {
    private final ChamadoService service;


    @PostMapping("/chamado")
    public ResponseEntity<?> criarChamado(@RequestBody ChamadoDTO c) {
        var chamadoEntidade = c.mapearparaChamado();
        try {
            service.criarChamado(chamadoEntidade, c.usuarioCriadorId());
        } catch (UsuarioNaoEncontradoException e) {
            var erro = new ErroResposta(HttpStatus.NOT_FOUND.value(), e.getMessage(), List.of());
            return ResponseEntity.status(erro.status()).body(erro);
        }

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/chamado/{id}").buildAndExpand(chamadoEntidade.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadoDTO> obterDetalhes(@PathVariable String id) {
        var idChamado = UUID.fromString(id);
        return service.pegarPorId(UUID.fromString(id))
                .map(chamado -> new ChamadoDTO(chamado.getTitulo(), chamado.getDescricao(), chamado.getCategoria(), chamado.getPrioridade(), chamado.getUsuarioCriador().getId()))
                .map(dto -> ResponseEntity.ok(dto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<ChamadoDTO>> filtrar(@RequestParam(required = false) String titulo,
                                 @RequestParam(required = false) CategoriaChamado categoria,
                                 @RequestParam(required = false) PrioridadeChamado prioridade,
                                 @RequestParam(required = false) LocalDate dataCriacao,
                                 @RequestParam(required = false) UUID idCriador,
                                 @RequestParam(required = false) UUID idTecnico) {
        List<ChamadoDTO> chamados = service.filtrar(titulo, categoria, prioridade, dataCriacao, idCriador, idTecnico).stream()
        .map(chamado -> new ChamadoDTO(chamado.getTitulo(), chamado.getDescricao(), chamado.getCategoria(), chamado.getPrioridade(), chamado.getUsuarioCriador().getId()))
        .toList();
        return ResponseEntity.ok(chamados);
    }

    @PatchMapping("/atribuirTecnicoChamado")
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
