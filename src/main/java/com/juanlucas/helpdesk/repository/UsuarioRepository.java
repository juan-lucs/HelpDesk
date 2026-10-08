package com.juanlucas.helpdesk.repository;

import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
boolean existsByEmail(String email);
}
