package com.juanlucas.helpdesk.repository;

import com.juanlucas.helpdesk.Model.Entity.ComentariosChamado;
import com.juanlucas.helpdesk.Model.Entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TecnicoRepository extends JpaRepository<Tecnico, UUID> {
}
