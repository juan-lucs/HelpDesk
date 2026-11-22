package com.juanlucas.helpdesk.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comentarios_chamado")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class ComentariosChamado {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne // o mesmo Usuario pode fazer vários comentários
    private Usuario Autor;

    @Column(length = 1000, nullable = false)
    private String conteudo;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime dataComentario;

    @ManyToOne
    @JoinColumn(name = "chamado_id", nullable = false)
    private Chamado chamadoDono;
}
