package com.juanlucas.helpdesk.Enum;

public enum Statuschamado {
    ABERTO,
    EM_ATENDIMENTO
 ,
  AGUARDANDO_USUARIO
  ,
    RESOLVIDO
  ,
    FECHADO,

    CANCELADO;

    public boolean alteracao(Statuschamado statusNovo) {
        return switch (this) {
            case ABERTO ->
                    statusNovo == CANCELADO || statusNovo == EM_ATENDIMENTO || statusNovo == AGUARDANDO_USUARIO;

            case EM_ATENDIMENTO ->
                statusNovo == CANCELADO || statusNovo == RESOLVIDO || statusNovo == AGUARDANDO_USUARIO;

            case AGUARDANDO_USUARIO ->
                    statusNovo == CANCELADO || statusNovo == RESOLVIDO || statusNovo == EM_ATENDIMENTO;

            case RESOLVIDO ->
                statusNovo == EM_ATENDIMENTO;

            case FECHADO, CANCELADO ->
                false;
        };
    }
}
