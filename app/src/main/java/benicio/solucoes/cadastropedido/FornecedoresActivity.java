package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterDist;
import benicio.solucoes.cadastropedido.databinding.ActivityFornecedoresBinding;
import benicio.solucoes.cadastropedido.databinding.ActivityMainBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.DistribuidorModel;
import benicio.solucoes.cadastropedido.service.ProdutosServices;
import benicio.solucoes.cadastropedido.util.RetrofitUitl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FornecedoresActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Dialog loadingDialog;
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

        configurarLoadingDialog();

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
        if (item.getItemId() == R.id.sair) {
            finish();
            startActivity(new Intent(this, CadastroLoginActivity.class));
            auth.signOut();
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

        configurarListener("");
    }

    private void configurarLoadingDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setView(LoadingLayoutBinding.inflate(getLayoutInflater()).getRoot());
        loadingDialog = b.create();
    }

    private void configurarListener(String query) {

        lista.clear();
        loadingDialog.show();
        services.pegarDistribuidores().enqueue(new Callback<List<DistribuidorModel>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<DistribuidorModel>> call, Response<List<DistribuidorModel>> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (query.isEmpty()) {
                        lista.addAll(response.body());
                    } else {
                        for (DistribuidorModel dist : response.body()) {
                            if (dist.getEmpresa().trim().toLowerCase().contains(query.trim().toLowerCase())) {
                                lista.add(dist);
                            }
                        }
                    }
                    adapterDist.notifyDataSetChanged();
                } else {
                    finish();
                    startActivity(new Intent(FornecedoresActivity.this, FornecedoresActivity.class));
                }
            }

            @Override
            public void onFailure(Call<List<DistribuidorModel>> call, Throwable t) {
                loadingDialog.dismiss();
                finish();
                startActivity(new Intent(FornecedoresActivity.this, FornecedoresActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                configurarListener(newText.toLowerCase().trim());
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


}