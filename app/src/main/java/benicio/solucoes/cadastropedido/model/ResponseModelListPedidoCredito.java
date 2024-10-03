package benicio.solucoes.cadastropedido.model;

import java.util.List;

public class ResponseModelListPedidoCredito {
    List<CreditoModel> msg;

    public ResponseModelListPedidoCredito() {
    }

    public List<CreditoModel> getMsg() {
        return msg;
    }

    public void setMsg(List<CreditoModel> msg) {
        this.msg = msg;
    }
}
