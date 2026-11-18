package com.juanlucas.helpdesk.repository;

import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import com.juanlucas.helpdesk.Model.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
boolean existsByEmail(String email);

@Query("SELECT t FROM Usuario t WHERE t.id = :id AND TYPE(t) = Tecnico")
Optional<Tecnico> findTecnicoByid(@Param("id") UUID id);

@Query("SELECT u FROM Usuario u WHERE TYPE(u) = Tecnico")
List<Tecnico> buscarTecnicos();

}

