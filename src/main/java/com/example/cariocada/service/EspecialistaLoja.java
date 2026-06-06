package com.example.cariocada.service;

import com.example.cariocada.model.*;
import com.example.cariocada.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EspecialistaLoja extends Especialista {

    @Autowired
    protected Blackboard blackboard;

    @Autowired
    protected EstoqueRepository estoqueRepository;

    @Autowired
    protected VendaRepository vendaRepository;

    private String nomeLoja;

    public EspecialistaLoja() { this.nomeLoja = "Geral"; }
    public EspecialistaLoja(String nomeLoja) { this.nomeLoja = nomeLoja; }

    @Override
    public void analisarQuadroEstrategico() {
        System.out.println("[ESPECIALISTA LOJA] Monitorando quadro da loja: " + nomeLoja);
    }

    // --- MÉTODO CORRIGIDO: Agora atende à chamada processarVenda ---
    public String processarVenda(String nomeProduto, int quantidade) {
        return registrarVenda(nomeProduto, quantidade); // Redireciona para sua lógica existente
    }

    // --- MÉTODO NOVO: Necessário para o Estoquista ---
    public String registrarEntrada(String nomeProduto, int quantidade) {
        Estoque estoqueAlvo = encontrarEstoque(nomeProduto);

        if (estoqueAlvo == null) {
            return "Erro: Produto '" + nomeProduto + "' não encontrado nesta loja.";
        }

        estoqueAlvo.setQuantidade(estoqueAlvo.getQuantidade() + quantidade);
        estoqueRepository.saveAndFlush(estoqueAlvo);
        return "Entrada de " + quantidade + " un de " + nomeProduto + " registrada com sucesso.";
    }

    // --- LÓGICA DE VENDEDOR (Sua lógica original preservada) ---
    public String registrarVenda(String nomeProduto, Integer quantidadeVendida) {
        Estoque estoqueAlvo = encontrarEstoque(nomeProduto);

        if (estoqueAlvo == null) {
            return "Erro: Produto '" + nomeProduto + "' não encontrado na loja " + this.nomeLoja;
        }

        if (estoqueAlvo.getQuantidade() >= quantidadeVendida) {
            estoqueAlvo.setQuantidade(estoqueAlvo.getQuantidade() - quantidadeVendida);
            estoqueRepository.saveAndFlush(estoqueAlvo);

            Venda novaVenda = new Venda();
            novaVenda.setLoja(this.nomeLoja);
            novaVenda.setProduto(nomeProduto);
            novaVenda.setQuantidade(quantidadeVendida);
            blackboard.salvarVenda(novaVenda);

            return "Venda de " + quantidadeVendida + " un de " + nomeProduto + " processada.";
        } else {
            return "Erro: Estoque insuficiente.";
        }
    }

    // Método auxiliar para buscar estoque
    private Estoque encontrarEstoque(String nomeProduto) {
        List<Estoque> todosEstoques = blackboard.getEstoqueLojas();
        for (Estoque est : todosEstoques) {
            if (est.getProduto() != null && est.getProduto().getNome().equalsIgnoreCase(nomeProduto)) {
                return est;
            }
        }
        return null;
    } 
}