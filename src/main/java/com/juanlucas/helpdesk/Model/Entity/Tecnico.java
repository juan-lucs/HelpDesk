package com.juanlucas.helpdesk.Model.Entity;

import com.juanlucas.helpdesk.Enum.EspecialidadeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;
@Getter
@Setter
@ToString
@Entity
// @Table(name = "tecnico")
public class Tecnico extends Usuario{

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EspecialidadeEnum especialidade;
 }
