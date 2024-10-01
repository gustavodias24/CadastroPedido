package benicio.solucoes.cadastropedido.model;

public class UserModel {
    public UserModel() {
    }

    String nome, email, senha;
    String idAgente;

    @Override
    public String toString() {
        return
                "Nome: " + '\n' + nome + '\n' +
                        "E-mail: " + '\n' + email + '\n' +
                        "Senha: " + '\n' + senha;
    }

    public UserModel(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public String getIdAgente() {
        return idAgente;
    }

    public void setIdAgente(String idAgente) {
        this.idAgente = idAgente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
