package com.juanlucas.helpdesk.Service;

import com.juanlucas.helpdesk.Model.Entity.Chamado;
import com.juanlucas.helpdesk.Model.Entity.Historico;
import com.juanlucas.helpdesk.repository.ChamadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChamadoService {
    private final ChamadoRepository repository;

    public void criarChamado(Chamado c) { // Chamado vem com titulo, descricao, categoria, prioridade e Status já definidos
        c.setDataCriacao(LocalDateTime.now());
        repository.save(c);
    }
}
