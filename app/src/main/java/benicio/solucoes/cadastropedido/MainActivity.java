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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterPedidos;
import benicio.solucoes.cadastropedido.databinding.ActivityMainBinding;
import benicio.solucoes.cadastropedido.dblocal.ProdutosDAO;
import benicio.solucoes.cadastropedido.model.ClienteModel;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ProdutoModel;
import benicio.solucoes.cadastropedido.model.ProdutosResponseModel;
import benicio.solucoes.cadastropedido.model.ResponseModel;
import benicio.solucoes.cadastropedido.model.ResponseModelListPedidoProduto;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.service.ApiServices;
import benicio.solucoes.cadastropedido.service.ProdutosServices;
import benicio.solucoes.cadastropedido.util.CSVGenerator;
import benicio.solucoes.cadastropedido.util.ClientesUtil;
import benicio.solucoes.cadastropedido.util.PedidosUtil;
import benicio.solucoes.cadastropedido.util.RetrofitApiApp;
import benicio.solucoes.cadastropedido.util.RetrofitUitl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiServices apiServices;

    // offset e limit para produtos
    int offset = 0;
    int limit = 1000;
    boolean continuarBuscandoProdutos = true;
    boolean fazendoBuscaProduto = false;

    private AdapterPedidos adapterPedidos;
    private String ultimoUpdateDatabase = "";
    private RecyclerView recyclerPedidos;
    List<PedidoModel> listaPedidos = new ArrayList<>();

    private ActivityMainBinding mainBinding;
    private ProdutosServices produtosServices;
    private SharedPreferences updatePrefs;
    private SharedPreferences.Editor editor;

    private SharedPreferences user_prefs;
    private SharedPreferences.Editor editor_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        apiServices = RetrofitApiApp.criarService(
                RetrofitApiApp.criarRetrofit()
        );

        getSupportActionBar().setTitle("Tela principal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configurarPrefs();

        new Thread() {
            @Override
            public void run() {
                verificarUpdates();
                super.run();
            }
        }.start();


        produtosServices = RetrofitUitl.criarService(RetrofitUitl.criarRetrofit());

        mainBinding.btnVerProdutos.setOnClickListener(view -> startActivity(new Intent(this, VisualizarProdutosActivity.class)));
        mainBinding.btnFazerPedido.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(this, InfosActivity.class));
        });

        mainBinding.pesquisarProduto.setOnClickListener(view -> {
            String pesquisa = mainBinding.edtPesquisa.getText().toString();
            configurarListener(pesquisa.toLowerCase().trim(), false);
        });

        mainBinding.filtrarPeriodo.setOnClickListener(v -> configurarListener("", true));

        mainBinding.btnAtualizarBase.setOnClickListener(view -> atualizarBaseProdutos());

        mainBinding.btnAtualizarBaseClientes.setOnClickListener(view -> atualizarBaseClientes());

        mainBinding.btnGerarRelatorio.setOnClickListener(v -> CSVGenerator.gerarPedidoCSV(
                this,
                listaPedidos
        ));

        configurarRecyclerPedidos();
    }

    private void atualizarBaseClientes() {

        mainBinding.progressBarClientes.setVisibility(View.VISIBLE);

        new Thread(() -> {
            produtosServices.atualizarBaseCliente().enqueue(new Callback<List<ClienteModel>>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<List<ClienteModel>> call, Response<List<ClienteModel>> response) {

                    runOnUiThread(() -> mainBinding.progressBarClientes.setVisibility(View.GONE));
                    if (response.isSuccessful()) {
                        ClientesUtil.saveClientes(getApplicationContext(), response.body());

                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Base  de clientes atualizada!", Toast.LENGTH_SHORT).show();
                            LocalDateTime agora = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String formatado = agora.format(formatter);
                            mainBinding.ultimoUpdateCliente.setText("Última Atualização: " + formatado);
                            editor.putString("dataCliente", formatado).apply();
                        });

                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                "Status: " + response.code()
                                        + '\n' +
                                        response.message(), Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onFailure(Call<List<ClienteModel>> call, Throwable t) {
                    runOnUiThread(() -> {
                        mainBinding.progressBarClientes.setVisibility(View.GONE);
                        Log.d("mayara", "onFailure: " + t.getMessage());
                        Toast.makeText(MainActivity.this, "Problema de conexão!", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }).start();

    }

    private void verificarUpdates() {

        apiServices.getLastUpdateDataHora().enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ultimoUpdateDatabase = response.body().getMsg();

                    if (!ultimoUpdateDatabase.equals(updatePrefs.getString("data", ""))) {
                        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                        b.setTitle("Aviso!");
                        b.setCancelable(false);
                        b.setMessage("Clique em OK para atualizar a base");
                        b.setPositiveButton("ok", (dialogInterface, i) -> atualizarBaseProdutos());
                        b.create().show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {

            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void atualizarBaseProdutos() {

        Toast.makeText(this, "Atualizando Aguarde!", Toast.LENGTH_SHORT).show();
        mainBinding.progressBarProdutos.setVisibility(View.VISIBLE);

        ProdutosDAO produtosDAO = new ProdutosDAO(MainActivity.this);
        produtosDAO.limparProdutos();

        new Thread(() -> {
            while (continuarBuscandoProdutos) {
                if (!fazendoBuscaProduto) {
                    fazendoBuscaProduto = true;
                    produtosServices.retornarProdutos(offset, limit).enqueue(new Callback<ProdutosResponseModel>() {
                        @Override
                        public void onResponse(Call<ProdutosResponseModel> call, Response<ProdutosResponseModel> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                List<ProdutoModel> produtos = response.body().getProdutos();
                                for (ProdutoModel produtoModel : produtos) {
                                    produtosDAO.inserirProduto(produtoModel);
                                }
                                offset += 1000;
                                fazendoBuscaProduto = false;

                                if (produtos.isEmpty()) {
                                    continuarBuscandoProdutos = false;
                                    runOnUiThread(() -> {
                                        mainBinding.progressBarProdutos.setVisibility(View.GONE);
                                        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                                        b.setTitle("Atenção!");
                                        b.setMessage("Base de produtos atualizada!");
                                        b.setPositiveButton("OK", null);
                                        b.create().show();
                                        mainBinding.ultimoUpdate.setText("Última Atualização: " + ultimoUpdateDatabase);
                                        editor.putString("data", ultimoUpdateDatabase).apply();
                                    });
                                }
                            } else {
                                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                        "Status: " + response.code()
                                                + '\n' +
                                                response.message(), Toast.LENGTH_SHORT).show());
                            }
                        }

                        @Override
                        public void onFailure(Call<ProdutosResponseModel> call, Throwable t) {
                            runOnUiThread(() -> {
                                mainBinding.progressBarProdutos.setVisibility(View.GONE);
                                Log.d("mayara", "onFailure: " + t.getMessage());
                                Toast.makeText(MainActivity.this, "Problema de conexão!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
            }
        }).start();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sair) {
            finish();
            startActivity(new Intent(this, CadastroLoginActivity.class));
            editor_user.remove("email").apply();
        } else if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MenuPedidoOrCreditoActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarListener(String query, Boolean filterPeriodo) {

        mainBinding.textCarregando.setVisibility(View.VISIBLE);
        mainBinding.recyclerPedidos.setVisibility(View.INVISIBLE);

        apiServices.getPedidosProdutos(new UserModel(user_prefs.getString("email", ""))).enqueue(new Callback<ResponseModelListPedidoProduto>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ResponseModelListPedidoProduto> call, @NonNull Response<ResponseModelListPedidoProduto> response) {
                if (response.isSuccessful()) {
                    listaPedidos.clear();
                    assert response.body() != null;

                    if (!filterPeriodo && query.isEmpty()) {
                        listaPedidos.addAll(response.body().getMsg());
                    } else {
                        for (PedidoModel pedidoModel : response.body().getMsg()) {
                            if (query.isEmpty()) {
                                if (filterPeriodo) {
                                    if (PedidosUtil.verificarIntervalo(
                                            pedidoModel.getData(),
                                            mainBinding.edtDataInicial.getText().toString(),
                                            mainBinding.edtDataFinal.getText().toString()
                                    )) {
                                        listaPedidos.add(pedidoModel);
                                    }
                                } else {
                                    listaPedidos.add(pedidoModel);
                                }
                            } else {
                                if (
                                        pedidoModel.getLojaVendedor().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getData().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getIdAgente().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getNomeEstabelecimento().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getNomeComprador().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getEmail().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getTele().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getCnpj().toLowerCase().trim().contains(query) ||
                                                pedidoModel.getObsEntrega().toLowerCase().trim().contains(query)
                                ) {
                                    listaPedidos.add(pedidoModel);
                                }
                            }
                        }
                    }


                    Collections.reverse(listaPedidos);
                    adapterPedidos.notifyDataSetChanged();
                    mainBinding.textCarregando.setVisibility(View.GONE);
                    mainBinding.recyclerPedidos.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModelListPedidoProduto> call, @NonNull Throwable t) {
                configurarListener("", false);
                Log.d("mayara", "onFailure: " + t.getMessage());
            }
        });
    }

    private void configurarRecyclerPedidos() {
        recyclerPedidos = mainBinding.recyclerPedidos;
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerPedidos.setHasFixedSize(true);
        configurarListener("", false);
        adapterPedidos = new AdapterPedidos(listaPedidos, this);
        recyclerPedidos.setAdapter(adapterPedidos);
    }

    @SuppressLint("SetTextI18n")
    private void configurarPrefs() {
        updatePrefs = getSharedPreferences("updates_prefs", MODE_PRIVATE);
        editor = updatePrefs.edit();
        user_prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        editor_user = user_prefs.edit();
        mainBinding.ultimoUpdate.setText("Última Atualização: " + updatePrefs.getString("data", ""));
        mainBinding.ultimoUpdateCliente.setText("Última Atualização: " + updatePrefs.getString("dataCliente", ""));
    }
}

