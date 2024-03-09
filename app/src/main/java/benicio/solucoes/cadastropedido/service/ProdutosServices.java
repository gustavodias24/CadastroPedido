package benicio.solucoes.cadastropedido.service;

import java.util.List;

import benicio.solucoes.cadastropedido.model.ClienteModel;
import benicio.solucoes.cadastropedido.model.DistribuidorModel;
import benicio.solucoes.cadastropedido.model.ProdutoModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ProdutosServices {
    @GET("produtos")
    Call<List<ProdutoModel>> atualizarBase();

    @GET("clientes")
    Call<List<ClienteModel>> atualizarBaseCliente();

    @GET("distribuidores")
    Call<List<DistribuidorModel>> pegarDistribuidores();
}
