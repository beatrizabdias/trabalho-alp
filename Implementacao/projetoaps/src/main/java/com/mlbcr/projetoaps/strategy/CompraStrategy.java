package com.mlbcr.projetoaps.strategy;

import org.springframework.stereotype.Component;

import com.mlbcr.projetoaps.model.Estoque;

@Component
public class CompraStrategy implements ReposicaoStrategy {

    @Override
    public boolean podeAplicar(Estoque estoque) {
        return true;
    }
}