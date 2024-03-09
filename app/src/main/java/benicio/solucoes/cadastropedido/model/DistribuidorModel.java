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

    public String getAceitaVouche() {
        return aceitaVouche;
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
