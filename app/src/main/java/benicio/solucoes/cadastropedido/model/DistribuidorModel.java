package benicio.solucoes.cadastropedido.model;

public class DistribuidorModel {
    public DistribuidorModel() {
    }

    String aceitaVouche;
    String empresa;
    String frete;
    String meioPagamento;
    String prazo;
    String valorMinimo;
    String cidade;
    String zonaEntrega;
    String produtos;
    String obs;
    String horarioCorte;

    public String getAceitaVouche() {
        return aceitaVouche;
    }

    @Override
    public String toString() {
        StringBuilder infos = new StringBuilder();
        infos.append("<b>EMPRESA:</b>").append("<br>").append(empresa).append("<br>");
        infos.append("<b>CIDADE:</b>").append("<br>").append(cidade).append("<br>");
        infos.append("<b>ZONA DE ENTREGA:</b>").append("<br>").append(zonaEntrega).append("<br>");
        infos.append("<b>PRODUTOS:</b>").append("<br>").append(produtos).append("<br>");
        infos.append("<b>VALOR MÍNIMO:</b>").append("<br>").append(valorMinimo).append("<br>");
        infos.append("<b>FRETE:</b>").append("<br>").append(frete).append("<br>");
        infos.append("<b>MEIO  DE PAGAMENTO:</b>").append("<br>").append(meioPagamento).append("<br>");
        infos.append("<b>PRAZO:</b>").append("<br>").append(prazo).append("<br>");
        infos.append("<b>ACEITA VOUCHER?:</b>").append("<br>").append(aceitaVouche).append("<br>");
        infos.append("<b>HORÁRIO DE CORTE:</b>").append("<br>").append(horarioCorte).append("<br>");
        infos.append("<b>OBS:</b>").append("<br>").append(obs).append("<br>");


        return infos.toString();
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setAceitaVouche(String aceitaVouche) {
        this.aceitaVouche = aceitaVouche;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getFrete() {
        return frete;
    }

    public void setFrete(String frete) {
        this.frete = frete;
    }

    public String getMeioPagamento() {
        return meioPagamento;
    }

    public void setMeioPagamento(String meioPagamento) {
        this.meioPagamento = meioPagamento;
    }

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }

    public String getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(String valorMinimo) {
        this.valorMinimo = valorMinimo;
    }
}
