package com.juanlucas.helpdesk.Model.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juanlucas.helpdesk.Enum.EspecialidadeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@ToString
@Entity
// @Table(name = "tecnico")
public class Tecnico extends Usuario{
    @NotBlank(message = "A especialidade do Tecnico é obrigatória!")
    @Enumerated(EnumType.STRING)
    @Column()
    private EspecialidadeEnum especialidade;
    @JsonIgnore // TODO: migrar para DTO
    @OneToMany(mappedBy = "tecnicoResponsavel")
    private List<Chamado> chamadosEncarregados = new ArrayList<>();

}
