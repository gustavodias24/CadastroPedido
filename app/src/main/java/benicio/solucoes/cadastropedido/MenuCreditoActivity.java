package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import benicio.solucoes.cadastropedido.databinding.ActivityMenuCreditoBinding;

public class MenuCreditoActivity extends AppCompatActivity {

    private ActivityMenuCreditoBinding mainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMenuCreditoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        getSupportActionBar().setTitle("Tela principal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mainBinding.btnFazerPedido.setOnClickListener( view -> startActivity(new Intent(this, CriarInfoCreditoActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}