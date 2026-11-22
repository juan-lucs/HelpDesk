package com.juanlucas.helpdesk.repository;

import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ComentariosChamadoRepository extends JpaRepository<ComentariosChamado, UUID> {
    @Query("SELECT c FROM ComentariosChamado c WHERE c.chamadoDono.id = :idChamado")
    List<ComentariosChamado> findAllById(UUID idChamado);
}
