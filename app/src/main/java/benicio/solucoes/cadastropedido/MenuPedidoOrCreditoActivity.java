package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import benicio.solucoes.cadastropedido.databinding.ActivityMainBinding;
import benicio.solucoes.cadastropedido.databinding.ActivityMenuPedidoOrCreditoBinding;

public class MenuPedidoOrCreditoActivity extends AppCompatActivity {

    ActivityMenuPedidoOrCreditoBinding mainBinding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMenuPedidoOrCreditoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Menu");

        mainBinding.pedido.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        mainBinding.fornecedor.setOnClickListener(view -> startActivity(new Intent(this, FornecedoresActivity.class)));
        mainBinding.credito.setOnClickListener(view -> startActivity(new Intent(this, MenuCreditoActivity.class)));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sair, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == R.id.sair){
            finish();
            auth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}