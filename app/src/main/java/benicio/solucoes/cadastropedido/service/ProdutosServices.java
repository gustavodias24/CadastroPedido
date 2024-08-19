package benicio.solucoes.cadastropedido.service;

import java.util.List;

import benicio.solucoes.cadastropedido.model.ClienteModel;
import benicio.solucoes.cadastropedido.model.DistribuidorModel;
import benicio.solucoes.cadastropedido.model.ProdutoModel;
import benicio.solucoes.cadastropedido.model.ProdutosResponseModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProdutosServices {
    @GET("produtos")
    Call<ProdutosResponseModel> retornarProdutos(@Query("offset") int offset, @Query("limit") int limit);

    @GET("clientes")
    Call<List<ClienteModel>> atualizarBaseCliente();

    @GET("distribuidores")
    Call<List<DistribuidorModel>> pegarDistribuidores();
}
