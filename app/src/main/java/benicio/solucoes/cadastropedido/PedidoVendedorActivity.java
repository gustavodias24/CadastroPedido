package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterCredito;
import benicio.solucoes.cadastropedido.adapter.AdapterPedidos;
import benicio.solucoes.cadastropedido.databinding.ActivityAdminBinding;
import benicio.solucoes.cadastropedido.databinding.ActivityPedidoVendedorBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.CreditoModel;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ResponseModelListPedidoCredito;
import benicio.solucoes.cadastropedido.model.ResponseModelListPedidoProduto;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.service.ApiServices;
import benicio.solucoes.cadastropedido.util.PedidosUtil;
import benicio.solucoes.cadastropedido.util.RetrofitApiApp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoVendedorActivity extends AppCompatActivity {

    private  ApiServices apiServices;

    private  AdapterPedidos adapterPedidos;
    private  AdapterCredito adapterCredito;
    private  List<PedidoModel> listaPedidos = new ArrayList<>();
    private  List<CreditoModel> listaCreditos = new ArrayList<>();
    private  ActivityPedidoVendedorBinding mainBinding;
    private RecyclerView recyclerPedidos;
    private  Bundle b;
    private boolean isCredito = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityPedidoVendedorBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        apiServices = RetrofitApiApp.criarService(RetrofitApiApp.criarRetrofit());

        getSupportActionBar().setTitle("Pedidos Vendedor");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        b = getIntent().getExtras();

        isCredito = b.getBoolean("credito", false);

        configurarRecyclerPedidos();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarRecyclerPedidos() {
        recyclerPedidos = mainBinding.recyclerPedidos;
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerPedidos.setHasFixedSize(true);


        if (isCredito) {
            adapterCredito = new AdapterCredito(listaCreditos, this);
            recyclerPedidos.setAdapter(adapterCredito);
        } else {
            adapterPedidos = new AdapterPedidos(listaPedidos, this, true, mainBinding.carregandoLayout);
            recyclerPedidos.setAdapter(adapterPedidos);
        }

        configurarListener();

    }

    private void configurarListener() {
        mainBinding.carregandoLayout.setVisibility(View.VISIBLE);

        if (isCredito) {
            apiServices.getPedidosCredito(new UserModel(b.getString("email", ""))).enqueue(new Callback<ResponseModelListPedidoCredito>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<ResponseModelListPedidoCredito> call, Response<ResponseModelListPedidoCredito> response) {
                    mainBinding.carregandoLayout.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        listaCreditos.clear();
                        listaCreditos.addAll(response.body().getMsg());

                        adapterCredito.notifyDataSetChanged();

                    }
                }

                @Override
                public void onFailure(Call<ResponseModelListPedidoCredito> call, Throwable t) {
                    mainBinding.carregandoLayout.setVisibility(View.GONE);
                }
            });
        } else {

            apiServices.getPedidosProdutos(new UserModel(b.getString("email", ""))).enqueue(new Callback<ResponseModelListPedidoProduto>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<ResponseModelListPedidoProduto> call, Response<ResponseModelListPedidoProduto> response) {
                    mainBinding.carregandoLayout.setVisibility(View.GONE);

                    if  (response.isSuccessful()){
                        listaPedidos.clear();
                        listaPedidos.addAll(response.body().getMsg());
                    }

                    adapterPedidos.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<ResponseModelListPedidoProduto> call, Throwable t) {
                    mainBinding.carregandoLayout.setVisibility(View.GONE);

                }
            });
        }
    }
}