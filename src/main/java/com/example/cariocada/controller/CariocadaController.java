package com.example.cariocada.controller;

import com.example.cariocada.model.Blackboard;
import com.example.cariocada.service.EspecialistaReposicao;
import com.example.cariocada.service.LojaMeier;
import com.example.cariocada.service.LojaTijuca;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cariocada")
@CrossOrigin(origins = "*")
public class CariocadaController {

    // Instâncias únicas mantidas na memória do servidor
    private final Blackboard blackboard = new Blackboard();
    private final LojaTijuca lojaTijuca = new LojaTijuca();
    private final LojaMeier lojaMeier = new LojaMeier();
    private final EspecialistaReposicao reposicao = new EspecialistaReposicao();

    @GetMapping("/dados")
    public Map<String, Object> buscarDados() {
        // Executa as regras do especialista de reposição antes de devolver os dados
        reposicao.executar(blackboard);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("estoques", blackboard.getEstoqueLojas());
        resposta.put("alertas", blackboard.getAlertasEReposicoes());
        resposta.put("regraTijuca", lojaTijuca.descontoProprio());
        resposta.put("regraMeier", lojaMeier.prioridade());
        return resposta;
    }

    @PostMapping("/venda")
    public Map<String, Object> realizarVenda(@RequestParam String loja, @RequestParam String produto, @RequestParam int quantidade) {
        if (loja.equalsIgnoreCase("Tijuca")) {
            lojaTijuca.registrarVenda(blackboard, produto, quantidade);
        } else if (loja.equalsIgnoreCase("Meier")) {
            lojaMeier.registrarVenda(blackboard, produto, quantidade);
        }
        
        // Atualiza as regras de reposição após a venda alterar o Blackboard
        reposicao.executar(blackboard);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("status", "Venda de " + quantidade + " " + produto + "(s) na filial " + loja + " processada!");
        resposta.put("estoques", blackboard.getEstoqueLojas());
        return resposta;
    }
}