package benicio.solucoes.cadastropedido.model;

import java.util.ArrayList;
import java.util.List;

public class ResponseModelListPedidoProduto {
    List<PedidoModel> msg = new ArrayList<>();

    public ResponseModelListPedidoProduto() {
    }

    public ResponseModelListPedidoProduto(List<PedidoModel> msg) {
        this.msg = msg;
    }

    public List<PedidoModel> getMsg() {
        return msg;
    }

    public void setMsg(List<PedidoModel> msg) {
        this.msg = msg;
    }
}
