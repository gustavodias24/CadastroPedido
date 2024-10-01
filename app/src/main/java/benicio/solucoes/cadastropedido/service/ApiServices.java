package benicio.solucoes.cadastropedido.service;

import benicio.solucoes.cadastropedido.model.ResponseModel;
import benicio.solucoes.cadastropedido.model.UserModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServices {

    @POST("registro")
    Call<ResponseModel> registrarUsuario(@Body UserModel userModel);

    @POST("login")
    Call<ResponseModel> logarUsuario(@Body UserModel userModel);

}
