package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import benicio.solucoes.cadastropedido.databinding.ActivityCriarInfoCreditoBinding;
import benicio.solucoes.cadastropedido.model.CreditoModel;

public class CriarInfoCreditoActivity extends AppCompatActivity {

    private ActivityCriarInfoCreditoBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCriarInfoCreditoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        getSupportActionBar().setTitle("Pedido de Crédito");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mainBinding.btnProsseguir.setOnClickListener(view -> {
            Intent i = new Intent(this, EnviarEmailActivity.class);
            i.putExtra("credito", true);

            Gson gson = new Gson();

            if (
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
                String dadosCredito = gson.toJson(new CreditoModel(
                        mainBinding.edtNome.getText().toString(),
                        mainBinding.edtRazaoSocial.getText().toString(),
                        mainBinding.edtCNPJ.getText().toString(),
                        mainBinding.edtEstadual.getText().toString(),
                        mainBinding.edtEmail.getText().toString(),
                        mainBinding.edtTelefone.getText().toString(),
                        mainBinding.edtEndereO.getText().toString(),
                        mainBinding.edtPrazo.getText().toString(),
                        mainBinding.edtValor.getText().toString()
                ));

                i.putExtra("dados", dadosCredito);
                startActivity(i);
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
}