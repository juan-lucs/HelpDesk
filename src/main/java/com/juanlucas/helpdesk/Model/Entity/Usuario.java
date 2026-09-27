package com.juanlucas.helpdesk.Model.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;
@Getter
@Setter
@ToString
public class Usuario {

    private UUID id;
    private String nome;
    private String email;
}
