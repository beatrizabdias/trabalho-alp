package com.mlbcr.projetoaps.strategy;

import com.mlbcr.projetoaps.model.Estoque;

public interface ReposicaoStrategy {

    boolean podeAplicar(Estoque estoque);

}