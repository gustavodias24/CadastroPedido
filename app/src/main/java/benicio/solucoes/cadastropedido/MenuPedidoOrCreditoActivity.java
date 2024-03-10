package benicio.solucoes.cadastropedido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

import benicio.solucoes.cadastropedido.databinding.ActivityMainBinding;
import benicio.solucoes.cadastropedido.databinding.ActivityMenuPedidoOrCreditoBinding;

public class MenuPedidoOrCreditoActivity extends AppCompatActivity {

    ActivityMenuPedidoOrCreditoBinding mainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMenuPedidoOrCreditoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Menu");

        mainBinding.pedido.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        mainBinding.fornecedor.setOnClickListener(view -> startActivity(new Intent(this, FornecedoresActivity.class)));

    }
}