package benicio.solucoes.cadastropedido.util;

import com.google.gson.GsonBuilder;

import benicio.solucoes.cadastropedido.service.ProdutosServices;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUitl {

    public static Retrofit criarRetrofit() {
//        return new Retrofit.Builder().baseUrl("https://red-cloud-update-produtos.vercel.app/")
//                .addConverterFactory(GsonConverterFactory.create()).build();

        return new Retrofit.Builder().baseUrl("http://195.200.6.225:50345/")
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static ProdutosServices criarService(Retrofit retrofit) {
        return retrofit.create(ProdutosServices.class);
    }
}
