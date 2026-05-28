const API_URL = "/api/cariocada";

async function carregarDados() {
    try {
        console.log("Buscando dados no servidor...");
        const response = await fetch(`${API_URL}/dados`);
        if (!response.ok) throw new Error("Erro na rede");
        const data = await response.json();
        
        // 1. LIMPA E RECONSTRÓI AS TABELAS
        const tbodyMeier = document.getElementById("tabelaMeier");
        const tbodyTijuca = document.getElementById("tabelaTijuca");
        
        tbodyMeier.innerHTML = "";
        tbodyTijuca.innerHTML = "";

        if (data.estoques && data.estoques.length > 0) {
            // Ordenação fixa para não pular itens
            data.estoques.sort((a, b) => a.produto.nome.localeCompare(b.produto.nome));

            data.estoques.forEach(est => {
                let tr = document.createElement("tr");
                tr.innerHTML = `<td>${est.produto.nome}</td><td><strong>${est.quantidade} un</strong></td>`;

                if (est.loja.nome.toLowerCase().includes("meier") || est.loja.nome.toLowerCase().includes("méier")) {
                    tbodyMeier.appendChild(tr);
                } else if (est.loja.nome.toLowerCase().includes("tijuca")) {
                    tbodyTijuca.appendChild(tr);
                }
            });
        }

        // 2. RECONSTRÓI OS SELECTS (Sempre que carregar, para garantir que não fiquem vazios)
        const selectLoja = document.getElementById("selectLoja");
        const selectProduto = document.getElementById("selectProduto");
        
        // Mantém seleção atual se existir
        const lojaSel = selectLoja.value;
        const prodSel = selectProduto.value;

        selectLoja.innerHTML = '<option value="">Selecione...</option>';
        selectProduto.innerHTML = '<option value="">Selecione...</option>';

        const lojas = [...new Set(data.estoques.map(e => e.loja.nome))];
        const produtos = [...new Set(data.estoques.map(e => e.produto.nome))];

        lojas.forEach(l => selectLoja.innerHTML += `<option value="${l}">${l}</option>`);
        produtos.sort().forEach(p => selectProduto.innerHTML += `<option value="${p}">${p}</option>`);

        if (lojaSel) selectLoja.value = lojaSel;
        if (prodSel) selectProduto.value = prodSel;

        // 3. ATUALIZA ALERTAS
        const lista = document.getElementById("listaAlertas");
        lista.innerHTML = "";
        if (data.alertas && data.alertas.length > 0) {
            data.alertas.forEach(alerta => lista.innerHTML += `<li>${alerta}</li>`);
        } else {
            lista.innerHTML = "<li>Nenhum alerta.</li>";
        }

    } catch (error) {
        console.error("Erro ao carregar:", error);
        document.getElementById("logVenda").innerText = "Erro ao conectar ao servidor.";
    }
}

async function vender() {
    const loja = document.getElementById("selectLoja").value;
    const produto = document.getElementById("selectProduto").value;
    const qtd = document.getElementById("inputQtd").value;

    if (!loja || !produto) {
        document.getElementById("logVenda").innerText = "Selecione loja e produto!";
        return;
    }

    try {
        const response = await fetch(`${API_URL}/venda?loja=${loja}&produto=${produto}&quantidade=${qtd}`, { method: 'POST' });
        const res = await response.json();
        document.getElementById("logVenda").innerText = res.status;
        await carregarDados(); // Atualiza tudo
    } catch (e) {
        document.getElementById("logVenda").innerText = "Erro na venda.";
    }
}

// Inicializa
carregarDados();