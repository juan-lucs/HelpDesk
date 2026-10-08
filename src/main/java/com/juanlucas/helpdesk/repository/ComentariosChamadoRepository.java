package com.juanlucas.helpdesk.repository;

import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComentariosChamadoRepository extends JpaRepository<ComentariosChamado, UUID> {

}
