package benicio.solucoes.cadastropedido.model;

public class ProdutoModel {

    float estoque;
    String nome, sku;
    String preco;
    String fornecedor;

    @Override
    public String toString() {
        return  nome + '\n' +
                "Estoque: " + estoque + '\n' +
                "SKU: " + sku + '\n' +
                "Preço: R$" + preco;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public float getEstoque() {
        return estoque;
    }

    public void setEstoque(float estoque) {
        this.estoque = estoque;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }
}
