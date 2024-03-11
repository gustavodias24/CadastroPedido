package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterCredito;
import benicio.solucoes.cadastropedido.adapter.AdapterPedidos;
import benicio.solucoes.cadastropedido.databinding.ActivityMenuCreditoBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.CreditoModel;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.util.PedidosUtil;

public class MenuCreditoActivity extends AppCompatActivity {

    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference().getRoot().child("usuarios");
    private DatabaseReference refCreditos = FirebaseDatabase.getInstance().getReference().getRoot().child("creditos");
    private ActivityMenuCreditoBinding mainBinding;
    private RecyclerView recyclerPedidos;
    private List<CreditoModel> lista = new ArrayList<>();
    private AdapterCredito adapterCredito;
    private Dialog loadingDialog;
    private String idUsuario;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMenuCreditoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        getSupportActionBar().setTitle("Tela principal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mainBinding.btnFazerPedido.setOnClickListener( view -> {
            finish();
            startActivity(new Intent(this, CriarInfoCreditoActivity.class));
        });

        configurarLoadingDialog();
        configurarIdVendedor();
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
        configurarListener("");
        adapterCredito = new AdapterCredito(lista, this);
        recyclerPedidos.setAdapter(adapterCredito);
    }

    private void configurarListener(String query) {
        loadingDialog.show();
        refCreditos.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.dismiss();
                if (snapshot.exists()) {
                    lista.clear();
                    for (DataSnapshot dado : snapshot.getChildren()) {
                        CreditoModel pedidoModel = dado.getValue(CreditoModel.class);

                        if (pedidoModel.getIdVendedor() != null && pedidoModel.getIdVendedor().equals(idUsuario)) {
                            if (query.isEmpty()) {
                                lista.add(pedidoModel);
                            }
//                            } else {
//                                assert pedidoModel != null;
//                                if (
//                                        pedidoModel.getLojaVendedor().toLowerCase().trim().contains(query) ||
//                                                pedidoModel.getData().toLowerCase().trim().contains(query) ||
//                                                pedidoModel.getIdAgente().toLowerCase().trim().contains(query) ||
//                                                pedidoModel.getNomeEstabelecimento().toLowerCase().trim().contains(query) ||
//                                                pedidoModel.getNomeComprador().toLowerCase().trim().contains(query) ||
//                                                pedidoModel.getEmail().toLowerCase().trim().contains(query) ||
//                                                pedidoModel.getTele().toLowerCase().trim().contains(query) ||
//                                                pedidoModel.getCnpj().toLowerCase().trim().contains(query) ||
//                                                pedidoModel.getObsEntrega().toLowerCase().trim().contains(query)
//                                ) {
//                                    listaPedidos.add(pedidoModel);
//                                }
//                            }
                        }
                    }

                    adapterCredito.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(MenuCreditoActivity.this, "Sem Conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configurarLoadingDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setView(LoadingLayoutBinding.inflate(getLayoutInflater()).getRoot());
        loadingDialog = b.create();
    }

    private void configurarIdVendedor() {

        loadingDialog.show();
        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.dismiss();

                for (DataSnapshot dado : snapshot.getChildren()) {
                    UserModel userModel = dado.getValue(UserModel.class);
                    if (userModel.getEmail().equals(auth.getCurrentUser().getEmail())) {
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


}
