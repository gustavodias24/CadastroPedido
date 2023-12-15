package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.ArrayList;

import benicio.solucoes.cadastropedido.databinding.ActivityInfosBinding;
import benicio.solucoes.cadastropedido.model.PedidoModel;

public class InfosActivity extends AppCompatActivity {

    private ActivityInfosBinding mainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityInfosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Pedido");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mainBinding.btnProdutos.setOnClickListener(view -> {
            Intent i = new Intent(this, ProdutosActivity.class);

            Gson gson = new Gson();

            String dadosPedido = gson.toJson(
                    new PedidoModel(
                           mainBinding.edtLoja.getText().toString(),
                            mainBinding.edtData.getText().toString(),
                            mainBinding.edtAgente.getText().toString(),
                            mainBinding.edtEstabelecimento.getText().toString(),
                            mainBinding.edtComprador.getText().toString(),
                            mainBinding.edtEmail.getText().toString(),
                            mainBinding.edtTelefone.getText().toString(),
                            mainBinding.edtCnpj.getText().toString(),
                            mainBinding.edtEstadual.getText().toString(),
                            mainBinding.edtFormaPagamento.getText().toString(),
                            mainBinding.edtEndereco.getText().toString(),
                            mainBinding.edtEnderecoEntrega.getText().toString(),
                            mainBinding.edtObsEntrega.getText().toString(),
                            new ArrayList<>()
                    )
            );

            i.putExtra("dadosPedido", dadosPedido);

            startActivity(i);
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){finish();}
        return super.onOptionsItemSelected(item);
    }
}