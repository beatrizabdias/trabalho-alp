package com.example.cariocada.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Blackboard {
    private final Map<String, Map<String, Integer>> estoqueLojas = new ConcurrentHashMap<>();
    private final List<String> alertasEReposicoes = new ArrayList<>();

    public Blackboard() {
        Map<String, Integer> estoqueTijuca = new ConcurrentHashMap<>();
        estoqueTijuca.put("Arroz", 50);
        estoqueTijuca.put("Feijao", 8); 
        estoqueLojas.put("Tijuca", estoqueTijuca);

        Map<String, Integer> estoqueMeier = new ConcurrentHashMap<>();
        estoqueMeier.put("Arroz", 12);
        estoqueMeier.put("Feijao", 40);
        estoqueLojas.put("Meier", estoqueMeier);
    }

    public Map<String, Map<String, Integer>> getEstoqueLojas() {
        return estoqueLojas;
    }

    public List<String> getAlertasEReposicoes() {
        return alertasEReposicoes;
    }

    public void adicionarAlerta(String alerta) {
        this.alertasEReposicoes.add(alerta);
    }

    public void limparAlertas() {
        this.alertasEReposicoes.clear();
    }
}