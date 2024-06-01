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
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
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
import benicio.solucoes.cadastropedido.databinding.ActivityAllPedidosBinding;
import benicio.solucoes.cadastropedido.databinding.ActivityPedidoVendedorBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.CreditoModel;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.util.CSVGenerator;
import benicio.solucoes.cadastropedido.util.PedidosUtil;

public class AllPedidosActivity extends AppCompatActivity {

    ActivityAllPedidosBinding mainBinding;
    private RecyclerView recyclerPedidos;
    //    public static Dialog loadingDialog;
    public static List<PedidoModel> listaPedidos = new ArrayList<>();
    public static List<CreditoModel> listaCreditos = new ArrayList<>();
    public static AdapterPedidos adapterPedidos;
    public static AdapterCredito adapterCredito;
    public static DatabaseReference refPedidos = FirebaseDatabase.getInstance().getReference().getRoot().child("pedidos");
    public static DatabaseReference refCreditos = FirebaseDatabase.getInstance().getReference().getRoot().child("creditos");

    public static boolean isCredito = false;
    public Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityAllPedidosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Últimos Pedidos");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            isCredito = bundle.getBoolean("credito", false);

            if (isCredito) {
                mainBinding.btnRelatorioCredito.setVisibility(View.VISIBLE);
                mainBinding.btnRelatorioPedidos.setVisibility(View.GONE);
            }
        }

//        configurarLoadingDialog();
        configurarRecyclerPedidos();

        mainBinding.btnRelatorioPedidos.setOnClickListener(v -> CSVGenerator.gerarPedidoADMCSV(this, listaPedidos));
        mainBinding.filtrarPeriodo.setOnClickListener(v -> configurarListener("", true));

        mainBinding.pesquisarProduto.setOnClickListener(view -> {
            String pesquisa = mainBinding.edtPesquisa.getText().toString();
            configurarListener(pesquisa.toLowerCase().trim(), false);
        });

        mainBinding.btnRelatorioCredito.setOnClickListener(v -> CSVGenerator.gerarCreditoADMCSV(
                this,
                listaCreditos
        ));


    }

//    private void configurarLoadingDialog() {
//        AlertDialog.Builder b = new AlertDialog.Builder(this);
//        b.setCancelable(false);
//        b.setView(LoadingLayoutBinding.inflate(getLayoutInflater()).getRoot());
//        loadingDialog = b.create();
//    }

    private void configurarRecyclerPedidos() {
        recyclerPedidos = mainBinding.recyclerPedidos;
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerPedidos.setHasFixedSize(true);

//        listaPedidos.addAll(PedidosUtil.returnPedidos(this));

        if (isCredito) {
            adapterCredito = new AdapterCredito(listaCreditos, this);
            recyclerPedidos.setAdapter(adapterCredito);
        } else {
            adapterPedidos = new AdapterPedidos(listaPedidos, this, true, mainBinding.carregandoLayout);
            recyclerPedidos.setAdapter(adapterPedidos);
        }

        configurarListener("", false);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void configurarListener(String query, boolean filterPeriodo) {
        mainBinding.carregandoLayout.setVisibility(View.VISIBLE);

        if (isCredito) {
            refCreditos.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mainBinding.carregandoLayout.setVisibility(View.GONE);
                    if (snapshot.exists()) {
                        listaCreditos.clear();
                        for (DataSnapshot dado : snapshot.getChildren()) {
                            CreditoModel creditoModel = dado.getValue(CreditoModel.class);

                            if (query.isEmpty()) {
//                                listaCreditos.add(creditoModel);
                                if (filterPeriodo) {
                                    if (PedidosUtil.verificarIntervalo(
                                            creditoModel.getData(),
                                            mainBinding.edtDataInicial.getText().toString(),
                                            mainBinding.edtDataFinal.getText().toString()
                                    )) {
                                        listaCreditos.add(creditoModel);
                                    }
                                } else {
                                    listaCreditos.add(creditoModel);
                                }
                            } else {
                                assert creditoModel != null;
                                try {
                                    if (
                                            creditoModel.getDistribuidor().toLowerCase().trim().contains(query) ||
                                                    creditoModel.getStatus().toLowerCase().trim().contains(query) ||
                                                    creditoModel.getValorSolicitado().toLowerCase().trim().contains(query) ||
                                                    creditoModel.getNome().toLowerCase().trim().contains(query) ||
                                                    creditoModel.getRazaoSocial().toLowerCase().trim().contains(query) ||
                                                    creditoModel.getEmail().toLowerCase().trim().contains(query) ||
                                                    creditoModel.getTelefone().toLowerCase().trim().contains(query) ||
                                                    creditoModel.getCnpj().toLowerCase().trim().contains(query) ||
                                                    creditoModel.getPrazoSocilitado().toLowerCase().trim().contains(query)
                                    ) {
                                        listaCreditos.add(creditoModel);
                                    }
                                } catch (Exception ignored) {
                                }

                            }
                        }

                        adapterCredito.notifyDataSetChanged();
                        mainBinding.totalizador.setText("Total: " + listaCreditos.size());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mainBinding.carregandoLayout.setVisibility(View.GONE);
                    Toast.makeText(AllPedidosActivity.this, "Sem Conexão", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            refPedidos.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mainBinding.carregandoLayout.setVisibility(View.GONE);
                    if (snapshot.exists()) {
                        listaPedidos.clear();
                        for (DataSnapshot dado : snapshot.getChildren()) {
                            PedidoModel pedidoModel = dado.getValue(PedidoModel.class);

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
                                                String.valueOf(pedidoModel.getStatus()).toLowerCase().trim().contains(query) ||
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

                        adapterPedidos.notifyDataSetChanged();
                        mainBinding.totalizador.setText("Total: " + listaPedidos.size());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mainBinding.carregandoLayout.setVisibility(View.GONE);
                    Toast.makeText(AllPedidosActivity.this, "Sem Conexão", Toast.LENGTH_LONG).show();
                }
            });
        }
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
}