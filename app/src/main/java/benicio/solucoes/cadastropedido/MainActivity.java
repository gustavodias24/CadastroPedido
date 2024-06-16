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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterPedidos;
import benicio.solucoes.cadastropedido.databinding.ActivityMainBinding;
import benicio.solucoes.cadastropedido.dblocal.ProdutosDAO;
import benicio.solucoes.cadastropedido.model.ClienteModel;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ProdutoModel;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.service.ProdutosServices;
import benicio.solucoes.cadastropedido.util.CSVGenerator;
import benicio.solucoes.cadastropedido.util.ClientesUtil;
import benicio.solucoes.cadastropedido.util.PedidosUtil;
import benicio.solucoes.cadastropedido.util.RetrofitUitl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AdapterPedidos adapterPedidos;
    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference().getRoot().child("usuarios");
    private DatabaseReference refUpdate = FirebaseDatabase.getInstance().getReference().getRoot().child("info");
    private String ultimoUpdateDatabase = "";
    private DatabaseReference refPedidos = FirebaseDatabase.getInstance().getReference().getRoot().child("pedidos");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private RecyclerView recyclerPedidos;
    List<PedidoModel> listaPedidos = new ArrayList<>();

    private ActivityMainBinding mainBinding;
    private ProdutosServices produtosServices;
    //    private Dialog loadingDialog;
    private SharedPreferences updatePrefs;
    private SharedPreferences.Editor editor;

    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Tela principal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        configurarLoadingDialog();
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
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onFailure(Call<List<ClienteModel>> call, Throwable t) {
                    runOnUiThread(() -> {
                        mainBinding.progressBarClientes.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Problema de conexão!", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }).start();

    }

    private void verificarUpdates() {
        refUpdate.child("dataHora").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ultimoUpdateDatabase = task.getResult().getValue(String.class);

                if (!ultimoUpdateDatabase.equals(updatePrefs.getString("data", ""))) {
                    AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                    b.setTitle("Aviso!");
                    b.setCancelable(false);
                    b.setMessage("Clique em OK para atualizar a base");
//                    b.setMessage("Sua base de dados está desatualizada! o último update foi " + ultimoUpdateDatabase + ", clique em OK para atualizar!");
                    b.setPositiveButton("ok", (dialogInterface, i) -> atualizarBaseProdutos());
                    b.create().show();
                }

                configurarIdVendedor();

            }
        });
    }

    private void atualizarBaseProdutos() {

        Toast.makeText(this, "Atualizando Aguarde!", Toast.LENGTH_SHORT).show();
        mainBinding.progressBarProdutos.setVisibility(View.VISIBLE);

        new Thread(() -> {
            produtosServices.atualizarBase().enqueue(new Callback<List<ProdutoModel>>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<List<ProdutoModel>> call, Response<List<ProdutoModel>> response) {

                    runOnUiThread(() -> mainBinding.progressBarProdutos.setVisibility(View.GONE));
                    if (response.isSuccessful()) {

                        ProdutosDAO produtosDAO = new ProdutosDAO(MainActivity.this);
                        produtosDAO.limparProdutos();

                        assert response.body() != null;
                        for (ProdutoModel produtoModel : response.body()) {
                            produtosDAO.inserirProduto(produtoModel);
                        }
                        // ProdutosUtils.saveProdutos(getApplicationContext(), response.body());

                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Base atualizada!", Toast.LENGTH_SHORT).show();
                            mainBinding.ultimoUpdate.setText("Última Atualização: " + ultimoUpdateDatabase);
                            editor.putString("data", ultimoUpdateDatabase).apply();
                        });

                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show());
                    }

                }

                @Override
                public void onFailure(Call<List<ProdutoModel>> call, Throwable t) {
                    runOnUiThread(() -> {
                        mainBinding.progressBarProdutos.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Problema de conexão!", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }).start();

    }

    private void configurarIdVendedor() {
//        runOnUiThread(() -> loadingDialog.show());

        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dado : snapshot.getChildren()) {
                    UserModel userModel = dado.getValue(UserModel.class);

                    if (userModel.getEmail().trim().toLowerCase().equals(auth.getCurrentUser().getEmail().trim().toLowerCase())) {

                        idUsuario = userModel.getId();
                        configurarRecyclerPedidos();
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

//                loadingDialog.dismiss();
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
            auth.signOut();
        } else if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MenuPedidoOrCreditoActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarListener(String query, Boolean filterPeriodo) {

        mainBinding.textCarregando.setVisibility(View.VISIBLE);
        mainBinding.recyclerPedidos.setVisibility(View.INVISIBLE);
        refPedidos.orderByChild("idVendedor").equalTo(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listaPedidos.clear();
                    for (DataSnapshot dado : snapshot.getChildren()) {
                        PedidoModel pedidoModel = dado.getValue(PedidoModel.class);

                        if (pedidoModel.getIdVendedor() != null && pedidoModel.getIdVendedor().equals(idUsuario)) {
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
                                assert pedidoModel != null;
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

                    runOnUiThread(() -> {
                        adapterPedidos.notifyDataSetChanged();
                        mainBinding.textCarregando.setVisibility(View.GONE);
                        mainBinding.recyclerPedidos.setVisibility(View.VISIBLE);
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> {
                    mainBinding.textCarregando.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Sem Conexão", Toast.LENGTH_LONG).show();
                });
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

        mainBinding.ultimoUpdate.setText("Última Atualização: " + updatePrefs.getString("data", ""));
        mainBinding.ultimoUpdateCliente.setText("Última Atualização: " + updatePrefs.getString("dataCliente", ""));
    }

//    private void configurarLoadingDialog() {
//        AlertDialog.Builder b = new AlertDialog.Builder(this);
//        b.setCancelable(false);
//        b.setView(LoadingLayoutBinding.inflate(getLayoutInflater()).getRoot());
//        loadingDialog = b.create();
//    }

}

