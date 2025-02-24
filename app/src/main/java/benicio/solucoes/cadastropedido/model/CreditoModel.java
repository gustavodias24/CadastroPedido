package benicio.solucoes.cadastropedido.model;

public class CreditoModel {

    String _id, idVendedor;

    String idAgent = "";
    String status = "pendente", distribuidor;
    String nome = "", razaoSocial = "", cnpj = "", inscricaoEstadual = "", email = "", telefone = "", endereco = "", prazoSocilitado = "",
            valorSolicitado = "", data = "", emailVendedor = "";;

    String enviarEmAnexo =
            "Documentos Solicitados dos sócios e da empresa: Enviar no anexo deste email: \n" +
                    "RG\n" +
                    "CPF\n" +
                    "Contrato Social\n" +
                    "Cartão CNPJ";

    public String getIdVendedor() {
        return idVendedor;
    }

    @Override
    public String toString() {
        return "id: " + _id + "\n" +
                "data: " + data + "\n" +
                "Status: " + status.toUpperCase() + "\n" +
                "Distribuidor: " + distribuidor + "\n" +
                "Nome: " + nome + "\n" +
                "Razão Social: " + razaoSocial + "\n" +
                "CNPJ: " + cnpj + "\n" +
                "Inscrição Estadual: " + inscricaoEstadual + "\n" +
                "E-mail: " + email + "\n" +
                "Telefone: " + telefone + "\n" +
                "Endereço: " + endereco + "\n" +
                "Prado Solicitado: " + prazoSocilitado + "\n" +
                "Valor Solicitado: " + valorSolicitado + "\n" +
                "\n" + "\n" + enviarEmAnexo + "\n";
    }

    public String getEmailVendedor() {
        return emailVendedor;
    }

    public void setEmailVendedor(String emailVendedor) {
        this.emailVendedor = emailVendedor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDistribuidor() {
        return distribuidor;
    }

    public void setDistribuidor(String distribuidor) {
        this.distribuidor = distribuidor;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public CreditoModel(String nome, String razaoSocial, String cnpj, String inscricaoEstadual, String email, String telefone, String endereco, String prazoSocilitado, String valorSolicitado, String distribuidor, String status, String data, String emailVendedor) {
        this.nome = nome;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.inscricaoEstadual = inscricaoEstadual;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.prazoSocilitado = prazoSocilitado;
        this.valorSolicitado = valorSolicitado;
        this.distribuidor = distribuidor;
        this.status = status;
        this.data = data;
        this.emailVendedor = emailVendedor;
    }

    public String getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(String idAgent) {
        this.idAgent = idAgent;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getPrazoSocilitado() {
        return prazoSocilitado;
    }

    public void setPrazoSocilitado(String prazoSocilitado) {
        this.prazoSocilitado = prazoSocilitado;
    }

    public String getValorSolicitado() {
        return valorSolicitado;
    }

    public void setValorSolicitado(String valorSolicitado) {
        this.valorSolicitado = valorSolicitado;
    }

    public String getEnviarEmAnexo() {
        return enviarEmAnexo;
    }

    public void setEnviarEmAnexo(String enviarEmAnexo) {
        this.enviarEmAnexo = enviarEmAnexo;
    }

    public CreditoModel() {
    }
}
