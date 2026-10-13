package com.juanlucas.helpdesk.Model.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@ToString(exclude = {"chamados"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public class Usuario {


    @Id
    @GeneratedValue
    private UUID id;

    @Column( nullable = false, length = 100)
    private String nome;

    @Column(unique = true, nullable = false, length = 150)
    private String email;
    @JsonIgnore // TODO: migrar para DTO
    @OneToMany( fetch = FetchType.LAZY, mappedBy = "usuarioCriador")
    private List<Chamado> chamados = new ArrayList<>();

}
