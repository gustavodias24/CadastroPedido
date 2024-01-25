package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterPedidos;
import benicio.solucoes.cadastropedido.databinding.ActivityMainBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ProdutoModel;
import benicio.solucoes.cadastropedido.service.ProdutosServices;
import benicio.solucoes.cadastropedido.util.PedidosUtil;
import benicio.solucoes.cadastropedido.util.ProdutosUtils;
import benicio.solucoes.cadastropedido.util.RetrofitUitl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerPedidos;
    List<PedidoModel> listaPedidos = new ArrayList<>();

    private ActivityMainBinding mainBinding;
    private ProdutosServices produtosServices;
    private Dialog loadingDialog;
    private SharedPreferences updatePrefs;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Tela principal");

        produtosServices = RetrofitUitl.criarService(RetrofitUitl.criarRetrofit());
        configurarRecyclerPedidos();
        configurarLoadingDialog();
        configurarPrefs();

        mainBinding.btnVerProdutos.setOnClickListener(view -> startActivity(new Intent(this, VisualizarProdutosActivity.class)));
        mainBinding.btnFazerPedido.setOnClickListener(view -> startActivity(new Intent(this , InfosActivity.class)));

        mainBinding.btnAtualizarBase.setOnClickListener( view -> {
            loadingDialog.show();

            produtosServices.atualizarBase().enqueue(new Callback<List<ProdutoModel>>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<List<ProdutoModel>> call, Response<List<ProdutoModel>> response) {
                    if ( response.isSuccessful()){
                        ProdutosUtils.saveProdutos(getApplicationContext(), response.body());
                        Toast.makeText(MainActivity.this, "Base atualizada!", Toast.LENGTH_SHORT).show();
                        LocalDateTime agora = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formatado = agora.format(formatter);
                        mainBinding.ultimoUpdate.setText("Última atualização: " + formatado);
                        editor.putString("data", formatado).apply();
                    }else{
                        Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }

                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure(Call<List<ProdutoModel>> call, Throwable t) {
                    loadingDialog.dismiss();
                    Log.d("mayara", "onFailure: " + t.getMessage());
                    Toast.makeText(MainActivity.this, "Problema de conexão!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void configurarRecyclerPedidos() {
        recyclerPedidos = mainBinding.recyclerPedidos;
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerPedidos.setHasFixedSize(true);
        listaPedidos.addAll(PedidosUtil.returnPedidos(this));
        if ( listaPedidos.size() > 0){ mainBinding.pedidosText.setVisibility(View.VISIBLE);}
        recyclerPedidos.setAdapter(new AdapterPedidos(listaPedidos, this));
    }

    @SuppressLint("SetTextI18n")
    private void configurarPrefs() {
        updatePrefs = getSharedPreferences("updates_prefs", MODE_PRIVATE);
        editor = updatePrefs.edit();

        mainBinding.ultimoUpdate.setText("Última atualização: " + updatePrefs.getString("data", ""));
    }

    private void configurarLoadingDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setView(LoadingLayoutBinding.inflate(getLayoutInflater()).getRoot());
        loadingDialog = b.create();
    }
}

