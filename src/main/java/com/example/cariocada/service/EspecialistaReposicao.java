package com.example.cariocada.service;

import java.util.Map;

import com.example.cariocada.model.Blackboard;

public class EspecialistaReposicao extends Especialista {
    private final int estoqueMinimo = 10;

    @Override
    public void executar(Blackboard blackboard) {
        // Limpa as sugestões antigas antes de rodar a nova verificação
        blackboard.limparAlertas();
        Map<String, Map<String, Integer>> estoques = blackboard.getEstoqueLojas();

        // Varre os estoques compartilhados no Blackboard
        for (String lojaOrigem : estoques.keySet()) {
            Map<String, Integer> produtosOrigem = estoques.get(lojaOrigem);

            for (String produto : produtosOrigem.keySet()) {
                int qtdOrigem = produtosOrigem.get(produto);

                // Se o produto estiver abaixo do estoque mínimo
                if (qtdOrigem < estoqueMinimo) {
                    boolean resolvidoPorTransferencia = false;

                    // Tenta encontrar outra filial que possa suprir a necessidade
                    for (String lojaDestino : estoques.keySet()) {
                        if (!lojaOrigem.equals(lojaDestino)) {
                            int qtdDestino = estoques.get(lojaDestino).getOrDefault(produto, 0);
                            
                            // Se a outra filial tiver estoque folgado (ex: mais de 25), sugere transferência
                            if (qtdDestino > 25) {
                                sugerirTransferencia(blackboard, lojaDestino, lojaOrigem, produto);
                                resolvidoPorTransferencia = true;
                                break;
                            }
                        }
                    }

                    // Se nenhuma outra filial puder ajudar, gera uma ordem de compra externa
                    if (!resolvidoPorTransferencia) {
                        gerarOrdemCompra(blackboard, lojaOrigem, produto);
                    }
                }
            }
        }
    }

    public void sugerirTransferencia(Blackboard bb, String de, String para, String produto) {
        bb.adicionarAlerta("SUGESTÃO: Transferir " + produto + " da filial " + de + " para a filial " + para + ".");
    }

    public void gerarOrdemCompra(Blackboard bb, String loja, String produto) {
        bb.adicionarAlerta("COMPRA: Emitir ordem de compra urgente de " + produto + " para a filial " + loja + ".");
    }
}