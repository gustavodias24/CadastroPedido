package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterDist;
import benicio.solucoes.cadastropedido.databinding.ActivityFornecedoresBinding;
import benicio.solucoes.cadastropedido.databinding.ActivityMainBinding;
import benicio.solucoes.cadastropedido.model.DistribuidorModel;
import benicio.solucoes.cadastropedido.service.ProdutosServices;
import benicio.solucoes.cadastropedido.util.RetrofitUitl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FornecedoresActivity extends AppCompatActivity {

    private ActivityFornecedoresBinding mainBinding;
    List<DistribuidorModel> lista = new ArrayList<>();
    RecyclerView recyclerDist;
    AdapterDist adapterDist;
    Retrofit retrofit;
    ProdutosServices services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityFornecedoresBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Distribuidores");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        retrofit = RetrofitUitl.criarRetrofit();
        services = RetrofitUitl.criarService(retrofit);
        configurarRecuclerView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarRecuclerView() {
        recyclerDist = mainBinding.recyclerDist;
        recyclerDist.setLayoutManager(new LinearLayoutManager(this));
        recyclerDist.setHasFixedSize(true);
        recyclerDist.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterDist = new AdapterDist(lista, this);
        recyclerDist.setAdapter(adapterDist);

        services.pegarDistribuidores().enqueue(new Callback<List<DistribuidorModel>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<DistribuidorModel>> call, Response<List<DistribuidorModel>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    lista.addAll(response.body());
                    adapterDist.notifyDataSetChanged();
                } else {
                    finish();
                    startActivity(new Intent(FornecedoresActivity.this, FornecedoresActivity.class));
                }
            }

            @Override
            public void onFailure(Call<List<DistribuidorModel>> call, Throwable t) {
                finish();
                startActivity(new Intent(FornecedoresActivity.this, FornecedoresActivity.class));
            }
        });
    }
}