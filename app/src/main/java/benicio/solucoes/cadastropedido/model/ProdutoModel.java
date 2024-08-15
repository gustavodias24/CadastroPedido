package benicio.solucoes.cadastropedido.model;

import java.text.NumberFormat;
import java.util.Locale;

public class ProdutoModel {

    float estoque;
    String nome, sku;
    String preco;
    String fornecedor;

    @Override
    public String toString() {
        return nome + '\n' +
                "Estoque: " + getEstoque() + '\n' +
                "SKU: " + sku + '\n' +
                "Preço: R$" + getPrecoFormatado();
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public int getEstoque() {
        String estoqueString = String.valueOf(estoque).split("\\.")[0];
        return Integer.parseInt(estoqueString);
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

    public String getPrecoFormatado() {
        float valor = Float.parseFloat(preco);
        NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatador.format(valor);
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }
}
