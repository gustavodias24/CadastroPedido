package benicio.solucoes.cadastropedido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import benicio.solucoes.cadastropedido.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        getSupportActionBar().setTitle("Admin Manager");

        mainBinding.btnUltimosPedidos.setOnClickListener(view ->
                startActivity(new Intent(this, AllPedidosActivity.class))
        );

        mainBinding.btnUltimosCredito.setOnClickListener(v -> {
            Intent i = new Intent(this, AllPedidosActivity.class);
            i.putExtra("credito", true);
            startActivity(i);
        });

        mainBinding.btnVendedores.setOnClickListener(v -> startActivity(new Intent(this, VendedoresActivity.class)));

    }


}