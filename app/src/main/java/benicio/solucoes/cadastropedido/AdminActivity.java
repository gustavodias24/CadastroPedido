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
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterPedidos;
import benicio.solucoes.cadastropedido.adapter.AdapterUsuarios;
import benicio.solucoes.cadastropedido.databinding.ActivityAdminBinding;
import benicio.solucoes.cadastropedido.databinding.ActivityMainBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.util.PedidosUtil;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding mainBinding;
    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference().getRoot().child("usuarios");

    private Dialog dialogCarregando;
    private RecyclerView recyclerUsuarios;
    private List<UserModel> usuarios = new ArrayList<>();
    private AdapterUsuarios adapterUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        configurarDialogCarregando();
        configurarRecyclerUsuarios();

        getSupportActionBar().setTitle("Admin Manager");

        mainBinding.btnUltimosPedidos.setOnClickListener(view ->
                startActivity(new Intent(this, AllPedidosActivity.class))
        );

        mainBinding.btnUltimosCredito.setOnClickListener(v -> {
            Intent i = new Intent(this, AllPedidosActivity.class);
            i.putExtra("credito", true);
            startActivity(i);
        });

    }

    private void configurarDialogCarregando() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingLayoutBinding dialogBinding = LoadingLayoutBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }

    private void configurarRecyclerUsuarios() {
        recyclerUsuarios = mainBinding.recyclerVendedores;
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsuarios.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerUsuarios.setHasFixedSize(true);
        configurarListener("");
        adapterUsuarios = new AdapterUsuarios(usuarios, this);
        recyclerUsuarios.setAdapter(adapterUsuarios);
    }

    private void configurarListener(String query) {
        dialogCarregando.show();
        refUsuarios.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialogCarregando.dismiss();
                if (snapshot.exists()) {
                    usuarios.clear();
                    for (DataSnapshot dado : snapshot.getChildren()) {
                        usuarios.add(dado.getValue(UserModel.class));
                    }

                    adapterUsuarios.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogCarregando.dismiss();
                Toast.makeText(AdminActivity.this, "Sem Conexão", Toast.LENGTH_LONG).show();
            }
        });
    }


}