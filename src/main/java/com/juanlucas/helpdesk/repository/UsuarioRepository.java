package com.juanlucas.helpdesk.repository;

import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
boolean existsByEmail(String email);

@Query("SELECT u FROM Usuario u WHERE TYPE(u) = Usuario")
List<Usuario> buscarUsuarios();

@Query("SELECT u FROM Usuario u WHERE TYPE(u) = Tecnico")
List<Tecnico> buscarTecnicos();

}

