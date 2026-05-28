package com.example.cariocada.service;

import com.example.cariocada.model.Blackboard;
import com.example.cariocada.model.Estoque;
import com.example.cariocada.model.Venda;
import com.example.cariocada.repository.EstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EspecialistaLoja extends Especialista {

    @Autowired
    protected Blackboard blackboard;

    @Autowired
    protected EstoqueRepository estoqueRepository;

    private String nomeLoja;

    public EspecialistaLoja() {
        this.nomeLoja = "Geral";
    }

    public EspecialistaLoja(String nomeLoja) {
        this.nomeLoja = nomeLoja;
    }

    @Override
    public void analisarQuadroEstrategico() {
        System.out.println("[ESPECIALISTA LOJA] Monitorando quadro da loja: " + nomeLoja);
    }

    public String registrarVenda(String nomeProduto, Integer quantidadeVendida) {
        // Busca a lista mais recente do Blackboard (vinda direto do findAll)
        List<Estoque> todosEstoques = blackboard.getEstoqueLojas();
        Estoque estoqueAlvo = null;

        String nomeLojaService = this.nomeLoja.toLowerCase();

        for (Estoque est : todosEstoques) {
            if (est != null && est.getLoja() != null && est.getProduto() != null) {
                String nomeLojaBanco = est.getLoja().getNome().toLowerCase()
                                         .replace("é", "e")
                                         .replace("ê", "e");
                
                if (nomeLojaBanco.contains(nomeLojaService) && 
                    est.getProduto().getNome().equalsIgnoreCase(nomeProduto)) {
                    estoqueAlvo = est;
                    break;
                }
            }
        }

        if (estoqueAlvo == null) {
            return "Erro: Produto '" + nomeProduto + "' nao encontrado na loja " + this.nomeLoja;
        }

        // VERIFICAÇÃO DE QUANTIDADE
        if (estoqueAlvo.getQuantidade() >= quantidadeVendida) {
            
            // BLINDAGEM DO OBJETO: Mantém as referências originais do banco Neon para não setar null
            Estoque estoqueParaAtualizar = estoqueRepository.findById(estoqueAlvo.getId()).orElse(estoqueAlvo);
            
            // Altera APENAS a quantidade, preservando os relacionamentos com as tabelas Loja e Produto
            estoqueParaAtualizar.setQuantidade(estoqueParaAtualizar.getQuantidade() - quantidadeVendida);
            
            // Salva de forma segura no banco de dados
            estoqueRepository.saveAndFlush(estoqueParaAtualizar);

            // Registra a venda no histórico
            Venda novaVenda = new Venda();
            novaVenda.setLoja(this.nomeLoja);
            novaVenda.setProduto(nomeProduto);
            novaVenda.setQuantidade(quantidadeVendida);
            blackboard.salvarVenda(novaVenda);

            return "Venda de " + quantidadeVendida + " un de " + nomeProduto + " processada na loja " + this.nomeLoja;
        } else {
            return "Erro: Estoque insuficiente na loja " + this.nomeLoja + ". Disponivel: " + estoqueAlvo.getQuantidade();
        }
    }

    public String getNomeLoja() {
        return nomeLoja;
    }
}