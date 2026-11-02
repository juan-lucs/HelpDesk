package com.juanlucas.helpdesk.repository;

import com.juanlucas.helpdesk.Enum.StatusChamado;
import com.juanlucas.helpdesk.Model.Entity.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ChamadoRepository extends JpaRepository<Chamado, UUID> {
    @Query("SELECT c.Status FROM Chamado c WHERE c.id = :id")
    StatusChamado verificarStatusChamado(@Param("id") UUID id);
}
