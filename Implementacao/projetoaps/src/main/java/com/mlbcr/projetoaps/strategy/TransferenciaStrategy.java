package com.mlbcr.projetoaps.strategy;

import org.springframework.stereotype.Component;

import com.mlbcr.projetoaps.model.Estoque;

@Component
public class TransferenciaStrategy implements ReposicaoStrategy {

    private static final int ESTOQUE_TRANSFERENCIA = 25;

    @Override
    public boolean podeAplicar(Estoque estoque) {

        return estoque != null
                && estoque.getQuantidade() > ESTOQUE_TRANSFERENCIA;
    }
}