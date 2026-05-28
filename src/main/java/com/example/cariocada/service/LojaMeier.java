package com.example.cariocada.service;

import org.springframework.stereotype.Service;

@Service
public class LojaMeier extends EspecialistaLoja {
    
    public LojaMeier() {
        super("Meier");
    }

    public String prioridade() {
        return "Loja Méier: Prioridade alta de reabastecimento nas sextas-feiras.";
    }
}