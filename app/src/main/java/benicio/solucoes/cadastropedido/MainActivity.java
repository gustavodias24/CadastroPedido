package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterPedidos;
import benicio.solucoes.cadastropedido.databinding.ActivityMainBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.ClienteModel;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ProdutoModel;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.service.ProdutosServices;
import benicio.solucoes.cadastropedido.util.ClientesUtil;
import benicio.solucoes.cadastropedido.util.PedidosUtil;
import benicio.solucoes.cadastropedido.util.ProdutosUtils;
import benicio.solucoes.cadastropedido.util.RetrofitUitl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AdapterPedidos adapterPedidos;
    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference().getRoot().child("usuarios");
    private DatabaseReference refPedidos = FirebaseDatabase.getInstance().getReference().getRoot().child("pedidos");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private RecyclerView recyclerPedidos;
    List<PedidoModel> listaPedidos = new ArrayList<>();

    private ActivityMainBinding mainBinding;
    private ProdutosServices produtosServices;
    private Dialog loadingDialog;
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

        produtosServices = RetrofitUitl.criarService(RetrofitUitl.criarRetrofit());
        configurarLoadingDialog();
        configurarIdVendedor();
        configurarRecyclerPedidos();
        configurarPrefs();

        mainBinding.btnVerProdutos.setOnClickListener(view -> startActivity(new Intent(this, VisualizarProdutosActivity.class)));
        mainBinding.btnFazerPedido.setOnClickListener(view -> startActivity(new Intent(this , InfosActivity.class)));

        mainBinding.pesquisarProduto.setOnClickListener(view -> {
            String pesquisa = mainBinding.edtPesquisa.getText().toString();
            configurarListener(pesquisa.toLowerCase().trim());
        });

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
                        mainBinding.ultimoUpdate.setText("Última Atualização: " + formatado);
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

        mainBinding.btnAtualizarBaseClientes.setOnClickListener( view ->{
            loadingDialog.show();

            produtosServices.atualizarBaseCliente().enqueue(new Callback<List<ClienteModel>>() {
                @Override
                public void onResponse(Call<List<ClienteModel>> call, Response<List<ClienteModel>> response) {
                    if ( response.isSuccessful()){
                        ClientesUtil.saveClientes(getApplicationContext(), response.body());
                        Toast.makeText(MainActivity.this, "Base  de clientes atualizada!", Toast.LENGTH_SHORT).show();
                        LocalDateTime agora = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formatado = agora.format(formatter);
                        mainBinding.ultimoUpdateCliente.setText("Última Atualização: " + formatado);
                        editor.putString("dataCliente", formatado).apply();
                    }else{
                        Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }

                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure(Call<List<ClienteModel>> call, Throwable t) {
                    loadingDialog.dismiss();
                    Log.d("mayara", "onFailure: " + t.getMessage());
                    Toast.makeText(MainActivity.this, "Problema de conexão!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void configurarIdVendedor(){

        loadingDialog.show();
        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.dismiss();

                for ( DataSnapshot dado : snapshot.getChildren()){
                    UserModel userModel = dado.getValue(UserModel.class);
                    if ( userModel.getEmail().equals(auth.getCurrentUser().getEmail())){
                        idUsuario = userModel.getId();
                        break;
                    }
                }

                configurarListener("");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == R.id.sair){
            finish();
            startActivity(new Intent(this, CadastroLoginActivity.class));
            auth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarListener(String query){
        loadingDialog.show();
        refPedidos.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.dismiss();
                if ( snapshot.exists() ){
                    listaPedidos.clear();
                    for ( DataSnapshot dado : snapshot.getChildren()){
                        PedidoModel pedidoModel = dado.getValue(PedidoModel.class);

                        if ( pedidoModel.getIdVendedor() != null && pedidoModel.getIdVendedor().equals(idUsuario)){
                            if ( query.isEmpty() ){
                                listaPedidos.add(pedidoModel);
                            }else{
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
                                ){
                                    listaPedidos.add(pedidoModel);
                                }
                            }
                        }
                    }

                    adapterPedidos.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(MainActivity.this, "Sem Conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configurarRecyclerPedidos() {
        recyclerPedidos = mainBinding.recyclerPedidos;
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerPedidos.setHasFixedSize(true);
        listaPedidos.addAll(PedidosUtil.returnPedidos(this));
        configurarListener("");
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

    private void configurarLoadingDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setView(LoadingLayoutBinding.inflate(getLayoutInflater()).getRoot());
        loadingDialog = b.create();
    }
}

