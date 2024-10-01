package benicio.solucoes.cadastropedido.util;

import benicio.solucoes.cadastropedido.service.ApiServices;
import benicio.solucoes.cadastropedido.service.ProdutosServices;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiApp {

    public static Retrofit criarRetrofit() {
        return new Retrofit.Builder().baseUrl("http://191.252.178.129:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static ApiServices criarService(Retrofit retrofit) {
        return retrofit.create(ApiServices.class);
    }
}
