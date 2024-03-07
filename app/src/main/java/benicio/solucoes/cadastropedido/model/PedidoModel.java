package benicio.solucoes.cadastropedido.model;

import java.util.ArrayList;
import java.util.List;

public class PedidoModel {


    int status = 0;
    String id, idVendedor;
    String lojaVendedor, data, idAgente, nomeEstabelecimento, nomeComprador, email, tele, cnpj, inscriEstadual,
            formaPagamento, enderecoCompleto, enderecoEntrega, obsEntrega;
    String cep = "";
    String totalCompra;
    List<ItemCompra> produtos = new ArrayList<>();

    public PedidoModel() {
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String toInformacao(Boolean isAdmin) {
        StringBuilder produtosBuilder = new StringBuilder();

        for (ItemCompra item : produtos) {
            produtosBuilder.append(item.toString()).append("\n\n");
        }
        String statusText = "";

        switch (this.getStatus()) {
            //Pendente = 0
            //Em Processamento = 1
            //Completo = 2
            //Pagamento Pendente = 3
            //Cancelado = 4
            //Fechado = 5

            case 0:
                statusText = "PENDENTE";
                break;
            case 1:
                statusText = "EM PROCESSAMENTO";
                break;
            case 2:
                statusText = "COMPLETO";
                break;
            case 3:
                statusText = "PAGAMENTO PENDENTE";
                break;
            case 4:
                statusText = "CANCELADO";
                break;
            case 5:
                statusText = "FECHADO";
                break;
        }
        String statusExibicao = "";
        if (isAdmin) {
            statusExibicao = "STATUS: " + statusText;
        } else {
            if (this.getStatus() == 4) {
                statusExibicao = "STATUS: " + statusText;
            }
        }

        return statusExibicao + '\n' +
                "Loja Vendedor: " + lojaVendedor + '\n' +
                "Data: " + data + '\n' +
                "Id Agente: " + idAgente + '\n' +
                "Nome Estabelecimento: " + nomeEstabelecimento + '\n' +
                "Nome Comprador: " + nomeComprador + '\n' +
                "E-mail: " + email + '\n' +
                "Telefone: " + tele + '\n' +
                "CNPJ: " + cnpj + '\n' +
                "Inscrição Estadual: " + inscriEstadual + '\n' +
                "Forma de Pagamento: " + formaPagamento + '\n' +
                "Endereço Completo: " + enderecoCompleto + '\n' +
                "CEP: " + cep + '\n' +
                "Endereço Entrega: " + enderecoEntrega + '\n' +
                "Observação Entrega: " + obsEntrega + '\n' +
                totalCompra + '\n' +
                "Produtos: " + "\n\n" + produtosBuilder.toString();
    }

    public PedidoModel(String lojaVendedor, String data, String idAgente, String nomeEstabelecimento, String nomeComprador, String email, String tele, String cnpj, String inscriEstadual, String formaPagamento, String enderecoCompleto, String enderecoEntrega, String obsEntrega, List<ItemCompra> produtos, String cep) {
        this.lojaVendedor = lojaVendedor;
        this.data = data;
        this.idAgente = idAgente;
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.nomeComprador = nomeComprador;
        this.email = email;
        this.tele = tele;
        this.cnpj = cnpj;
        this.inscriEstadual = inscriEstadual;
        this.formaPagamento = formaPagamento;
        this.enderecoCompleto = enderecoCompleto;
        this.enderecoEntrega = enderecoEntrega;
        this.obsEntrega = obsEntrega;
        this.produtos = produtos;
        this.cep = cep;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getLojaVendedor() {
        return lojaVendedor;
    }

    public void setLojaVendedor(String lojaVendedor) {
        this.lojaVendedor = lojaVendedor;
    }

    public String getData() {
        return data;
    }

    public String getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(String totalCompra) {
        this.totalCompra = totalCompra;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIdAgente() {
        return idAgente;
    }

    public void setIdAgente(String idAgente) {
        this.idAgente = idAgente;
    }

    public String getNomeEstabelecimento() {
        return nomeEstabelecimento;
    }

    public void setNomeEstabelecimento(String nomeEstabelecimento) {
        this.nomeEstabelecimento = nomeEstabelecimento;
    }

    public String getNomeComprador() {
        return nomeComprador;
    }

    public void setNomeComprador(String nomeComprador) {
        this.nomeComprador = nomeComprador;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTele() {
        return tele;
    }

    public void setTele(String tele) {
        this.tele = tele;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getInscriEstadual() {
        return inscriEstadual;
    }

    public void setInscriEstadual(String inscriEstadual) {
        this.inscriEstadual = inscriEstadual;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public String getObsEntrega() {
        return obsEntrega;
    }

    public void setObsEntrega(String obsEntrega) {
        this.obsEntrega = obsEntrega;
    }

    public List<ItemCompra> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ItemCompra> produtos) {
        this.produtos = produtos;
    }
}
