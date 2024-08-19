package benicio.solucoes.cadastropedido.model;

import java.util.List;

public class ProdutosResponseModel {
    int total;
    List<ProdutoModel> produtos;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ProdutoModel> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ProdutoModel> produtos) {
        this.produtos = produtos;
    }
}
