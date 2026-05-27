package com.example.cariocada.service;

import java.util.Map;

import com.example.cariocada.model.Blackboard;

public class EspecialistaLoja extends Especialista {
    protected String filial;

    public EspecialistaLoja(String filial) {
        this.filial = filial;
    }

    public void registrarVenda(Blackboard blackboard, String produto, int quantidade) {
        Map<String, Integer> estoque = blackboard.getEstoqueLojas().get(filial);
        if (estoque != null && estoque.containsKey(produto)) {
            int qtdAtual = estoque.get(produto);
            if (qtdAtual >= quantidade) {
                estoque.put(produto, qtdAtual - quantidade);
            }
        }
    }

    public int verificarEstoque(Blackboard blackboard, String produto) {
        Map<String, Integer> estoque = blackboard.getEstoqueLojas().get(filial);
        return (estoque != null) ? estoque.getOrDefault(produto, 0) : 0;
    }

    @Override
    public void executar(Blackboard blackboard) {
        // Monitoramento genérico se necessário
    }
}