package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Exception.EmailJaCadastradoException;
import com.juanlucas.helpdesk.Service.UsuarioService;
import com.juanlucas.helpdesk.controller.DTO.ErroResposta;
import com.juanlucas.helpdesk.controller.DTO.TecnicoDTO;
import com.juanlucas.helpdesk.controller.DTO.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<?> salvarUsuario(@RequestBody UsuarioDTO usuario) {
        try {
            var usuarioEntidade = usuario.mapearparaUsuario();

            service.salvarUsuario(usuarioEntidade);

            // http://localhost:8080/usuario/ID DO USUARIO QUE FOI CRIADO PELO BANCOOO
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(usuarioEntidade.getId()).toUri();

            return ResponseEntity.created(location).build(); // Uma resposta boa, dizendo que usuario foi criado
        } catch (EmailJaCadastradoException e) {
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }

    @PostMapping("/tecnico")
    public ResponseEntity<?> salvarTecnico(@RequestBody TecnicoDTO tecnico) {
        try {
            var tecnicoEntidade = tecnico.mapearparaTenico();
            service.salvarTecnico(tecnicoEntidade);

            // http://localhost:8080/tecnico/ID DO USUARIO QUE FOI CRIADO PELO BANCOOO
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(tecnicoEntidade.getId()).toUri();
            return ResponseEntity.created(location).build();
        } catch (EmailJaCadastradoException e) {
            var erroDTO = ErroResposta.conflito(e.getMessage()); // E-mail duplicado é 409; validação de campos é 422 (contrato-api.md).
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
        }

    @GetMapping("{id}")
    public ResponseEntity<?> pegarPorIdUsuario(@PathVariable("id") String id) {
        var idUser = UUID.fromString(id);
            return service.pegarPorId(idUser)
                    .map(usuario -> new UsuarioDTO(usuario.getId(), usuario.getNome(), usuario.getEmail()))

                    // IMPORTANTE, ELE AINDA É UM OPTIONAL APÓS O PRIMEIRO .MAP, PORQUE O .MAP RETORNA UM OPTIONAL.
                    // A DIFERENÇA É QUE AGORA ELE É UM OPTIONAL<UsuarioDTO>, não um Optional<Usuario>

                    .map(dto -> ResponseEntity.ok(dto))
                    .orElseGet(() -> ResponseEntity.notFound().build()); // aqui o valor dentro do optional de DTO, de cima é devolvido, ou vira NOTFOUND()

            // nomes antes do -> são PARÂMETROS de uma expressão lambda.
            // Eles RECEBEM o valor que será usado no código depois da seta.
    }

    @GetMapping("/tecnico/{id}")
    public ResponseEntity<TecnicoDTO> pegarPorIdTecnico(@PathVariable("id") String id) {
        var idTec = UUID.fromString(id);

        return service.pegarTecnicoPorId(idTec)
                .map(tecnico -> new TecnicoDTO(tecnico.getId(), tecnico.getNome(), tecnico.getEmail(), tecnico.getEspecialidade()))

                // IMPORTANTE, ELE AINDA É UM OPTIONAL APÓS O PRIMEIRO .MAP, PORQUE O .MAP RETORNA UM OPTIONAL.
                // A DIFERENÇA É QUE AGORA ELE É UM OPTIONAL<UsuarioDTO>, não um Optional<Usuario>

                .map(dto -> ResponseEntity.ok(dto))
                .orElseGet(() -> ResponseEntity.notFound().build()); // aqui o valor dentro do optional de cima é pego, ou vira NOTFOUND()

        // nomes antes do -> são PARÂMETROS de uma expressão lambda.
        // Eles RECEBEM o valor que será usado no código depois da seta.
    }


}
