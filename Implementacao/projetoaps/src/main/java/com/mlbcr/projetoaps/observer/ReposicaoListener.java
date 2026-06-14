package com.mlbcr.projetoaps.observer;

import org.springframework.stereotype.Component;

import com.mlbcr.projetoaps.model.Loja;
import com.mlbcr.projetoaps.model.Produto;
import com.mlbcr.projetoaps.service.ReposicaoService;

@Component
public class ReposicaoListener implements ReposicaoObserver {

    private final ReposicaoService reposicaoService;

    public ReposicaoListener(ReposicaoService reposicaoService) {
        this.reposicaoService = reposicaoService;
    }

    @Override
    public void atualizar(Produto produto, Loja loja) {

        reposicaoService.analisarReposicao(produto, loja);

    }
}