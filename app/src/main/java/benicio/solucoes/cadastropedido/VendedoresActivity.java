package benicio.solucoes.cadastropedido;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterUsuarios;
import benicio.solucoes.cadastropedido.databinding.ActivityAdminBinding;
import benicio.solucoes.cadastropedido.databinding.ActivityVendedoresBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.ResponseModelListUsers;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.service.ApiServices;
import benicio.solucoes.cadastropedido.util.RetrofitApiApp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendedoresActivity extends AppCompatActivity {
    private ApiServices apiServices;


    private ActivityVendedoresBinding mainBinding;
    private Dialog dialogCarregando;

    private RecyclerView recyclerUsuarios;
    private List<UserModel> usuarios = new ArrayList<>();
    private AdapterUsuarios adapterUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityVendedoresBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        apiServices = RetrofitApiApp.criarService(RetrofitApiApp.criarRetrofit());

        try {
            getSupportActionBar().setTitle("Vendedores");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
        }


        configurarDialogCarregando();
        configurarRecyclerUsuarios();


    }

    private void configurarDialogCarregando() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingLayoutBinding dialogBinding = LoadingLayoutBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }

    private void configurarListener(String query) {
        dialogCarregando.show();

        apiServices.getUsuarios().enqueue(new Callback<ResponseModelListUsers>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseModelListUsers> call, Response<ResponseModelListUsers> response) {
                dialogCarregando.dismiss();
                usuarios.clear();
                usuarios.addAll(response.body().getMsg());
                adapterUsuarios.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseModelListUsers> call, Throwable t) {
                dialogCarregando.dismiss();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
}