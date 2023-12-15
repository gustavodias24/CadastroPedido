package benicio.solucoes.cadastropedido.service;

import java.util.List;

import benicio.solucoes.cadastropedido.model.ProdutoModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ProdutosServices {
    @GET("produtos")
    Call<List<ProdutoModel>> atualizarBase();
}
