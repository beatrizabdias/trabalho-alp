package com.example.cariocada.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cariocada.model.Blackboard;
import com.example.cariocada.model.Compra;
import com.example.cariocada.model.Estoque;
import com.example.cariocada.model.Fornecedor;

@Service
public class EspecialistaReposicao {

    @Autowired
    private Blackboard blackboard;

    private static final int LIMITE_CRITICO = 10;

    public void analisarQuadroEstrategico() {
        System.out.println("[ESPECIALISTA REPOSIÇÃO] Analisando dados do Neon...");
        blackboard.limparAlertas();

        List<Estoque> todosEstoques = blackboard.getEstoqueLojas();
        List<Fornecedor> fornecedores = blackboard.getFornecedores();
        
        // Lista temporária para evitar modificar o estado do banco no meio da leitura do laço
        List<Compra> comprasParaSalvar = new ArrayList<>();

        if (todosEstoques == null) return;

        for (Estoque est : todosEstoques) {
            if (est != null && est.getLoja() != null && est.getProduto() != null) {
                
                if (est.getQuantidade() < LIMITE_CRITICO) {
                    String nomeLoja = est.getLoja().getNome();
                    String nomeProduto = est.getProduto().getNome();
                    int qtdAtual = est.getQuantidade();

                    // Tenta achar uma solução de transferência entre as lojas primeiro
                    boolean resolvidoPorTransferencia = tentarSugerirTransferencia(nomeLoja, nomeProduto, qtdAtual, todosEstoques);

                    // Se nenhuma outra loja puder ajudar, emite Ordem de Compra para o fornecedor
                    if (!resolvidoPorTransferencia) {
                        String nomeFornecedor = "Fornecedor Geral";
                        
                        // Busca o fornecedor correto associado a esse produto no banco
                        if (fornecedores != null) {
                            for (Fornecedor f : fornecedores) {
                                if (f.getNome() != null && (f.getNome().equalsIgnoreCase(nomeProduto) || 
                                   (f.getContato() != null && f.getContato().toLowerCase().contains(nomeProduto.toLowerCase())))) {
                                    nomeFornecedor = f.getNome() + " (" + f.getContato() + ")";
                                    break;
                                }
                            }
                        }

                        String alertaCompra = "⚠️ CRÍTICO: " + nomeLoja + " tem apenas " + qtdAtual + " un de " + nomeProduto + 
                                             ". Solução: Emitir Ordem de Compra para " + nomeFornecedor;
                        
                        blackboard.adicionarAlerta(alertaCompra);

                        // Cria o objeto, mas guarda na lista temporária para salvar depois
                        Compra novaCompra = new Compra();
                        novaCompra.setProduto(nomeProduto);
                        novaCompra.setFornecedor(nomeFornecedor);
                        novaCompra.setDetalhePedido("Pedido automático de reposição de 50 un para a loja " + nomeLoja);
                        comprasParaSalvar.add(novaCompra);
                    }
                }
            }
        }

        // SALVAMENTO SEGURO: Agora que o laço terminou, salvamos as ordens de compra de uma vez só
        for (Compra compra : comprasParaSalvar) {
            try {
                blackboard.salvarCompra(compra);
            } catch (Exception e) {
                System.out.println("[ERRO REPOSIÇÃO] Falha ao gravar ordem de compra: " + e.getMessage());
            }
        }
    }

    private boolean tentarSugerirTransferencia(String lojaNecessitada, String produto, int qtdAtual, List<Estoque> estoques) {
        if (estoques == null) return false;
        
        for (Estoque est : estoques) {
            if (est != null && est.getProduto() != null && est.getLoja() != null) {
                // Se for o mesmo produto, mas em OUTRA loja
                if (est.getProduto().getNome().equalsIgnoreCase(produto) && 
                    !est.getLoja().getNome().equalsIgnoreCase(lojaNecessitada)) {
                    
                    // Se a outra loja tiver estoque folgado (mais de 25 un), sugere transferir
                    if (est.getQuantidade() > 25) {
                        String alertaTrf = "💡 SUGESTÃO TRF: Mover 15 un de " + produto + " da loja " + est.getLoja().getNome() + 
                                           " (Estoque: " + est.getQuantidade() + ") para a loja " + lojaNecessitada + ".";
                        blackboard.adicionarAlerta(alertaTrf);
                        return true; 
                    }
                }
            }
        }
        return false; 
    }
}