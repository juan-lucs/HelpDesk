package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public void salvar(Usuario u) {
        if (repository.existsByEmail(u.getEmail())) {
            EmailJaCadastradoException("Email já cadastrado em outro usuário!");
        }
    }

}
