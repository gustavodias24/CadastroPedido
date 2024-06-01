package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import benicio.solucoes.cadastropedido.databinding.ActivityCriarInfoCreditoBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.CreditoModel;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.util.MathUtils;

public class CriarInfoCreditoActivity extends AppCompatActivity {
    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference().getRoot().child("usuarios");
    //    private Dialog loadingDialog;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String idAgent = "";
    private ActivityCriarInfoCreditoBinding mainBinding;
    private DatabaseReference refCreditos = FirebaseDatabase.getInstance().getReference().getRoot().child("creditos");

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCriarInfoCreditoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        encontrarUsuarioAtual();
        configurarLoadingDialog();

        getSupportActionBar().setTitle("Pedido de Crédito");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mainBinding.btnProsseguir.setOnClickListener(view -> {
            Intent i = new Intent(this, EnviarEmailActivity.class);
            i.putExtra("credito", true);

            Gson gson = new Gson();

            if (
                    mainBinding.edtDistribuidor.getText().toString().isEmpty() ||
                            mainBinding.edtNome.getText().toString().isEmpty() ||
                            mainBinding.edtRazaoSocial.getText().toString().isEmpty() ||
                            mainBinding.edtEmail.getText().toString().isEmpty() ||
                            mainBinding.edtCNPJ.getText().toString().isEmpty() ||
                            mainBinding.edtTelefone.getText().toString().isEmpty() ||
                            mainBinding.edtEndereO.getText().toString().isEmpty() ||
                            mainBinding.edtPrazo.getText().toString().isEmpty() ||
                            mainBinding.edtValor.getText().toString().isEmpty()
            ) {
                Toast.makeText(this, "Preencha Todos os Dados Obrigatórios!", Toast.LENGTH_LONG).show();
            } else {
                @SuppressLint("SimpleDateFormat") CreditoModel creditoModel = new CreditoModel(
                        mainBinding.edtNome.getText().toString(),
                        mainBinding.edtRazaoSocial.getText().toString(),
                        mainBinding.edtCNPJ.getText().toString(),
                        mainBinding.edtEstadual.getText().toString(),
                        mainBinding.edtEmail.getText().toString().toLowerCase().trim(),
                        mainBinding.edtTelefone.getText().toString(),
                        mainBinding.edtEndereO.getText().toString(),
                        mainBinding.edtPrazo.getText().toString(),
                        mainBinding.edtValor.getText().toString(),
                        mainBinding.edtDistribuidor.getText().toString(),
                        "pendente",
                        new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
                        idAgent
                );

                loadingDialog.show();
                refCreditos.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 1;
                        loadingDialog.dismiss();
                        if (snapshot.exists()) {
                            for (DataSnapshot dado : snapshot.getChildren()) {
                                count++;
                            }
                        }
                        creditoModel.setId(MathUtils.formatarNumero(count));
                        String dadosCredito = gson.toJson(creditoModel);

                        i.putExtra("dados", dadosCredito);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismiss();
                        Toast.makeText(CriarInfoCreditoActivity.this, "Tente Novamente...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void encontrarUsuarioAtual() {
        try {
            refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dado : snapshot.getChildren()) {
                        if (dado.getValue(UserModel.class).getEmail().trim().toLowerCase().equals(user.getEmail().trim().toLowerCase())) {
                            idAgent = dado.getValue(UserModel.class).getIdAgente();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception ignored) {
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            startActivity(new Intent(this, MenuCreditoActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarLoadingDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setView(LoadingLayoutBinding.inflate(getLayoutInflater()).getRoot());
        loadingDialog = b.create();
    }
}