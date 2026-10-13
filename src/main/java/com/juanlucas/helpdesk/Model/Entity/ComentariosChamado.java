package com.juanlucas.helpdesk.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comentarios_chamado")
@Getter
@Setter
@ToString

public class ComentariosChamado {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne // o mesmo Usuario pode fazer vários comentários
    private Usuario Autor;

    @Column(length = 1000, nullable = false)
    private String conteudo;


    @Column(nullable = false)
    private LocalDateTime dataComentario;
}
