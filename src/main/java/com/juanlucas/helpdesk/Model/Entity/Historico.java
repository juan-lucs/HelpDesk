package com.juanlucas.helpdesk.Model.Entity;

import com.juanlucas.helpdesk.Enum.StatusChamado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "historicochamados")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class) // vai analisar toda operação feita nessa entidade, buscando @CreateDate ou @LAstModifyDate
public class Historico {
        @Id
        @GeneratedValue
        private UUID id;

        @CreatedDate
        @Column( nullable = false)
        private LocalDateTime horaDaAlteracao;

        @ManyToOne
        private Usuario Autor;

        @Column( nullable = false, length = 1000)
        private String descricao;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private StatusChamado Statusatual;

//        @ManyToOne(optional = false)
//        // nullable = false: o histórico nunca existe sem um chamado, e o Hibernate
//        // já inclui a FK no INSERT (evita o UPDATE extra).
//        @JoinColumn(name = "chamado_id", nullable = false)
//        private Chamado chamadoDono;
}
