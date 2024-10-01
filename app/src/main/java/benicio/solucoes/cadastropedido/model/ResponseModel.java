package benicio.solucoes.cadastropedido.model;

public class ResponseModel {
    String msg;
    boolean success;

    public ResponseModel() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
