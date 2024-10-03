package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterCredito;
import benicio.solucoes.cadastropedido.adapter.AdapterPedidos;
import benicio.solucoes.cadastropedido.databinding.ActivityMenuCreditoBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.CreditoModel;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ResponseModelListPedidoCredito;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.service.ApiServices;
import benicio.solucoes.cadastropedido.util.CSVGenerator;
import benicio.solucoes.cadastropedido.util.PedidosUtil;
import benicio.solucoes.cadastropedido.util.RetrofitApiApp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuCreditoActivity extends AppCompatActivity {

    private ApiServices apiServices;
    private SharedPreferences user_prefs;
    private SharedPreferences.Editor editor;


    private ActivityMenuCreditoBinding mainBinding;
    private RecyclerView recyclerPedidos;
    private List<CreditoModel> lista = new ArrayList<>();
    private AdapterCredito adapterCredito;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMenuCreditoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        apiServices = RetrofitApiApp.criarService(
                RetrofitApiApp.criarRetrofit()
        );
        user_prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        editor = user_prefs.edit();

        getSupportActionBar().setTitle("Tela principal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mainBinding.btnFazerPedido.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(this, CriarInfoCreditoActivity.class));
        });

        configurarLoadingDialog();
        configurarRecyclerPedidos();

        mainBinding.pesquisarProduto.setOnClickListener(view -> {
            String query = mainBinding.edtPesquisa.getText().toString();
            configurarListener(query, false);
        });

        mainBinding.filtrarPeriodo.setOnClickListener(v -> configurarListener("", true));

        mainBinding.btnGerarRelatorio.setOnClickListener(v -> CSVGenerator.gerarCreditoADMCSV(this, lista));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MenuPedidoOrCreditoActivity.class));
            finish();
        } else if (item.getItemId() == R.id.sair) {
            finish();
            editor.remove("email").apply();
        }

        return super.onOptionsItemSelected(item);
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
                configurarListener(newText.toLowerCase().trim(), false);
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    private void configurarRecyclerPedidos() {
        recyclerPedidos = mainBinding.recyclerPedidos;
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerPedidos.setHasFixedSize(true);
        configurarListener("", false);
        adapterCredito = new AdapterCredito(lista, this);
        recyclerPedidos.setAdapter(adapterCredito);
    }

    private void configurarListener(String query, boolean filterPeriodo) {
        loadingDialog.show();
        apiServices.getPedidosCredito(new UserModel(user_prefs.getString("email", ""))).enqueue(new Callback<ResponseModelListPedidoCredito>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseModelListPedidoCredito> call, Response<ResponseModelListPedidoCredito> response) {
                loadingDialog.dismiss();
                lista.clear();

                if (response.isSuccessful()) {
                    if (query.isEmpty() && !filterPeriodo) {
                        assert response.body() != null;
                        lista.addAll(response.body().getMsg());
                    } else {
                        assert response.body() != null;
                        for (CreditoModel pedidoModel : response.body().getMsg()) {
                            if (query.isEmpty()) {
                                if (filterPeriodo) {
                                    if (PedidosUtil.verificarIntervalo(
                                            pedidoModel.getData(),
                                            mainBinding.edtDataInicial.getText().toString(),
                                            mainBinding.edtDataFinal.getText().toString()
                                    )) {
                                        lista.add(pedidoModel);
                                    }
                                } else {
                                    lista.add(pedidoModel);
                                }
                            } else {
                                assert pedidoModel != null;
                                if (
                                        pedidoModel.getId().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getEmail().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getDistribuidor().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getRazaoSocial().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getStatus().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getPrazoSocilitado().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getValorSolicitado().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getCnpj().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getEndereco().toLowerCase().trim().contains(query)
                                ) {
                                    lista.add(pedidoModel);
                                }
                            }
                        }
                    }

                }

                Collections.reverse(lista);
                adapterCredito.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<ResponseModelListPedidoCredito> call, Throwable t) {
                loadingDialog.dismiss();
                configurarListener("", false);
            }
        });
    }

    private void configurarLoadingDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setView(LoadingLayoutBinding.inflate(getLayoutInflater()).getRoot());
        loadingDialog = b.create();
    }

}
