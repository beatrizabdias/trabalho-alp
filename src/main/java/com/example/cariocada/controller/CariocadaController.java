package com.example.cariocada.controller;

import com.example.cariocada.model.Blackboard;
import com.example.cariocada.service.EspecialistaReposicao;
import com.example.cariocada.service.LojaMeier;
import com.example.cariocada.service.LojaTijuca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cariocada")
@CrossOrigin(origins = "*")
public class CariocadaController {

    @Autowired
    private Blackboard blackboard;

    @Autowired
    private LojaTijuca lojaTijuca;

    @Autowired
    private LojaMeier lojaMeier;

    @Autowired
    private EspecialistaReposicao reposicao;

    @GetMapping("/dados")
    public Map<String, Object> buscarDados() {
        // Roda a análise lógica do especialista de reposição
        reposicao.analisarQuadroEstrategico();

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("estoques", blackboard.getEstoqueLojas());
        resposta.put("fornecedores", blackboard.getFornecedores());
        resposta.put("vendas", blackboard.getHistoricoVendas());
        resposta.put("compras", blackboard.getHistoricoComprasFornecedores());
        resposta.put("alertas", blackboard.getAlertasEReposicoes());
        resposta.put("regraTijuca", lojaTijuca.descontoProprio());
        resposta.put("regraMeier", lojaMeier.prioridade());
        return resposta;
    }

    @PostMapping("/venda")
    public Map<String, Object> realizarVenda(
            @RequestParam String loja, 
            @RequestParam String produto, 
            @RequestParam int quantidade) {
        
        String mensagemStatus = "";
        String lojaLower = loja.toLowerCase();

        // Validação flexível e blindada contra acentos ou strings compostas
        if (lojaLower.contains("tijuca")) {
            mensagemStatus = lojaTijuca.registrarVenda(produto, quantidade);
        } else if (lojaLower.contains("meier") || lojaLower.contains("méier")) {
            mensagemStatus = lojaMeier.registrarVenda(produto, quantidade);
        } else {
            mensagemStatus = "Erro: Loja informada (" + loja + ") é inválida no sistema.";
        }
        
        // Atualiza as análises críticas do Blackboard após a baixa no estoque
        reposicao.analisarQuadroEstrategico();

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("status", mensagemStatus);
        resposta.put("estoques", blackboard.getEstoqueLojas());
        resposta.put("alertas", blackboard.getAlertasEReposicoes());
        return resposta;
    }
}