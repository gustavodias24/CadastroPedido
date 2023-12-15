package benicio.solucoes.cadastropedido.model;

public class ItemCompra {

    String nomeProduto, sku,
    valor,quantidade, valorTotalPorItem,
    valorTotalFinal;
    String desconto = "0";

    @Override
    public String toString() {
        return
                "Nome Produto: " + nomeProduto + '\n' +
                "SKU: " + sku + '\n' +
                "Valor: " + valor + '\n' +
                "Quantidade: " + quantidade + '\n' +
                "Valor Total por Item: " + valorTotalPorItem + '\n' +
                "Desconto: " + (desconto.isEmpty() ? "0" : desconto) +'%'+ '\n' +
                "Valor Total Final: " + valorTotalFinal ;
    }

    public ItemCompra(String nomeProduto, String sku, String valor, String quantidade, String valorTotalPorItem, String desconto, String valorTotalFinal) {
        this.nomeProduto = nomeProduto;
        this.sku = sku;
        this.valor = valor;
        this.quantidade = quantidade;
        this.valorTotalPorItem = valorTotalPorItem;
        this.desconto = desconto;
        this.valorTotalFinal = valorTotalFinal;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getValorTotalPorItem() {
        return valorTotalPorItem;
    }

    public void setValorTotalPorItem(String valorTotalPorItem) {
        this.valorTotalPorItem = valorTotalPorItem;
    }

    public String getDesconto() {
        return desconto;
    }

    public void setDesconto(String desconto) {
        this.desconto = desconto;
    }

    public String getValorTotalFinal() {
        return valorTotalFinal;
    }

    public void setValorTotalFinal(String valorTotalFinal) {
        this.valorTotalFinal = valorTotalFinal;
    }
}
