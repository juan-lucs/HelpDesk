package com.juanlucas.helpdesk.controller;

import com.juanlucas.helpdesk.Service.UsuarioService;
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
    public ResponseEntity<Object> salvarUsuario(@RequestBody UsuarioDTO usuario){
        var usuarioEntidade = usuario.mapearparaUsuario();
        service.salvarUsuario(usuarioEntidade);

        // http://localhost:8080/usuario/ID DO USUARIO QUE FOI CRIADO PELO BANCOOO
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(usuarioEntidade.getId()).toUri();

        return ResponseEntity.created(location).build(); // Uma resposta boa, dizendo que usuario foi criado
    }

    @PostMapping("/tecnico")
    public ResponseEntity<Void> salvarTecnico(@RequestBody TecnicoDTO tecnico) {
        var tecnicoEntidade = tecnico.mapearparaTenico();
        service.salvarTecnico(tecnicoEntidade);

        // http://localhost:8080/tecnico/ID DO USUARIO QUE FOI CRIADO PELO BANCOOO
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(tecnicoEntidade.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<UsuarioDTO> pegarUsuarioPorId(@PathVariable("id") String id) {
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
    public ResponseEntity<TecnicoDTO> pegarPorId(@PathVariable("id") String id) {
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
