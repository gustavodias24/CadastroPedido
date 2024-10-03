package benicio.solucoes.cadastropedido.model;

import java.util.List;

public class ResponseModelListUsers {
    List<UserModel> msg;

    public ResponseModelListUsers() {
    }

    public List<UserModel> getMsg() {
        return msg;
    }

    public void setMsg(List<UserModel> msg) {
        this.msg = msg;
    }
}
