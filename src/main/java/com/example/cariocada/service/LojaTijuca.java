package com.example.cariocada.service;

import org.springframework.stereotype.Service;

@Service
public class LojaTijuca extends EspecialistaLoja {
    
    public LojaTijuca() {
        super("Tijuca");
    }

    public String descontoProprio() {
        return "Loja Tijuca: 10% de desconto em produtos da cesta básica!";
    }
}