package com.juanlucas.helpdesk.Model.Entity;

import com.juanlucas.helpdesk.Enum.Statuschamado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "historicochamados")
@Getter
@Setter
@ToString
public class Historico {
        @Id
        @GeneratedValue
        private UUID id;
        @Column( nullable = false)
        private LocalTime horaDaAlteracao;

        @ManyToOne
        private Usuario Autor;

        @Column( nullable = false, length = 1000)
        private String descricao;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Statuschamado Statusatual;
}
