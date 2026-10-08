package com.juanlucas.helpdesk.Model.Entity;

import com.juanlucas.helpdesk.Enum.CategoriaChamado;
import com.juanlucas.helpdesk.Enum.PrioridadeChamado;
import com.juanlucas.helpdesk.Enum.Statuschamado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chamado")
@Getter
@Setter

// Exclui as listas do toString() para evitaR
// query extra (o toString força o carregamento das listas)
@ToString(exclude = {"historico", "comentarios"})
public class Chamado {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 250, nullable = false)
    private String titulo;

    @Column(length = 1000, nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING) // salva o NOME do enum ("ALTA"), não o número. Mais seguro se a ordem mudar.
    @Column(nullable = false)
    private CategoriaChamado categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeChamado prioridade;

    // UM chamado tem MUITOS históricos.
    // cascade = ALL: salvar/atualizar/deletar o chamado faz o mesmo nos históricos.
    // orphanRemoval = true: remover um histórico da lista também o apaga do banco.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)

    // Cria a coluna "chamado_id" na tabela de HISTORICO (ESTRAngeira para este chamado).
    // Sem isso, o JPA criaria uma tabela com o id de chamado e historico
    // nullable = false: o histórico nunca existe sem um chamado, e o Hibernate
    // já inclui a FK no INSERT (evita o UPDATE extra).
    @JoinColumn(name = "chamado_id", nullable = false)
    // Inicializada para evitar NullPointerException em chamados novos.
    private List<Historico> historico = new ArrayList<>();

    // Mesma lógica do histórico: os comentários dependem do chamado,
    // então nascem, mudam e morrem junto com ele.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "chamado_id", nullable = false)
    private List<ComentariosChamado> comentarios = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statuschamado Status = Statuschamado.ABERTO;
}