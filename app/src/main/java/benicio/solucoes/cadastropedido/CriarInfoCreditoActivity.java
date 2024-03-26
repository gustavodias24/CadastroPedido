package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;

import benicio.solucoes.cadastropedido.databinding.ActivityCriarInfoCreditoBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.CreditoModel;
import benicio.solucoes.cadastropedido.util.MathUtils;

public class CriarInfoCreditoActivity extends AppCompatActivity {

    private ActivityCriarInfoCreditoBinding mainBinding;
    private DatabaseReference refCreditos = FirebaseDatabase.getInstance().getReference().getRoot().child("creditos");

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCriarInfoCreditoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

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
                CreditoModel creditoModel = new CreditoModel(
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
                        "pendente"
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