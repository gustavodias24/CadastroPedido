package benicio.solucoes.cadastropedido.service;

import benicio.solucoes.cadastropedido.model.CreditoModel;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ResponseModel;
import benicio.solucoes.cadastropedido.model.ResponseModelListPedidoCredito;
import benicio.solucoes.cadastropedido.model.ResponseModelListPedidoProduto;
import benicio.solucoes.cadastropedido.model.ResponseModelListUsers;
import benicio.solucoes.cadastropedido.model.ResponseModelPedidoCredito;
import benicio.solucoes.cadastropedido.model.ResponseModelPedidoProduto;
import benicio.solucoes.cadastropedido.model.ResponseModelUser;
import benicio.solucoes.cadastropedido.model.UserModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiServices {

    @POST("registro")
    Call<ResponseModel> registrarUsuario(@Body UserModel userModel);

    @POST("login")
    Call<ResponseModel> logarUsuario(@Body UserModel userModel);

    @GET("last_update")
    Call<ResponseModel> getLastUpdateDataHora();

    @POST("pedidos_produtos")
    Call<ResponseModelListPedidoProduto> getPedidosProdutos(@Body UserModel userModel);

    @POST("pedidos_creditos")
    Call<ResponseModelListPedidoCredito> getPedidosCredito(@Body UserModel userModel);

    @POST("get_info_user")
    Call<ResponseModelUser> getUser(@Body UserModel userModel);

    @POST("salvar_pedido")
    Call<ResponseModelPedidoProduto> salvarPedidoProduto(@Body PedidoModel pedido);

    @PUT("atualizar_pedido_produto")
    Call<ResponseModel> atualizarPedidoProduto(@Body PedidoModel pedido);

    @POST("salvar_pedido")
    Call<ResponseModelPedidoCredito> salvarPedidoCredito(@Body CreditoModel pedido);


    @GET("usuarios")
    Call<ResponseModelListUsers> getUsuarios();

    @DELETE("usuarios")
    Call<ResponseModel> deleteUsuario(@Body UserModel userModel);

}
