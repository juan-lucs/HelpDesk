package com.juanlucas.helpdesk.Enum;

public enum StatusChamado {
    ABERTO,
    EM_ATENDIMENTO
 ,
  AGUARDANDO_USUARIO
  ,
    FECHADO, // FECHADO seria quando o Tecnico fecha o chamado

    CANCELADO; // cancelado seria quando o USUÁRIO cancela o teste

    public boolean alteracao(StatusChamado statusNovo) {
        return switch (this) {
            case ABERTO ->
                    statusNovo == CANCELADO || statusNovo == EM_ATENDIMENTO || statusNovo == AGUARDANDO_USUARIO;

            case EM_ATENDIMENTO ->
                statusNovo == CANCELADO ||  statusNovo == AGUARDANDO_USUARIO; // O Tecnico pergunta para o usuário se o problema foi resolvido, antes de fechar o chamado

            case AGUARDANDO_USUARIO ->
                    statusNovo == CANCELADO  || statusNovo == EM_ATENDIMENTO || statusNovo == FECHADO;

            case FECHADO ->
                statusNovo == EM_ATENDIMENTO;

            case  CANCELADO ->
                false;
        };
    }
}
