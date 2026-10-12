package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Exception.EmailJaCadastradoException;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import com.juanlucas.helpdesk.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public Usuario salvarUsuario(Usuario usuario) throws EmailJaCadastradoException {
        verificarEmail(usuario.getEmail());

        return repository.save(usuario);
    }

    public Tecnico salvarTecnico(Tecnico tecnico) throws EmailJaCadastradoException {
        verificarEmail(tecnico.getEmail());

        if (tecnico.getEspecialidade() == null ||
                tecnico.getEspecialidade().toString().isBlank()) {
            throw new IllegalArgumentException(
                    "A especialidade é obrigatória"
            );
        }

        return repository.save(tecnico);
    }

    private void verificarEmail(String email) throws EmailJaCadastradoException {
        if (repository.existsByEmail(email)) {
            throw new EmailJaCadastradoException(
                    "Email já cadastrado"
            );
        }
    }

    public Optional<Usuario> pegarPorId(UUID idUser) {
        return repository.findById(idUser);
    }

    public List<Usuario> pegarTodosUsuarios(){
        return repository.buscarUsuarios();
    }

    public List<Tecnico> pegarTodosTecnicos(){
        return repository.buscarTecnicos();
    }
}