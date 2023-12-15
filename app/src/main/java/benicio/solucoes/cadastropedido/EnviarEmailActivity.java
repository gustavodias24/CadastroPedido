package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ShareCompat;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import benicio.solucoes.cadastropedido.databinding.ActivityEnviarEmailBinding;
import benicio.solucoes.cadastropedido.databinding.ActivityInfosBinding;
import benicio.solucoes.cadastropedido.model.PedidoModel;

public class EnviarEmailActivity extends AppCompatActivity {

    private ActivityEnviarEmailBinding mainBinding;
    private Bundle b;
    private PedidoModel pedido;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityEnviarEmailBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Enviar E-mail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        b = getIntent().getExtras();
        pedido = new Gson().fromJson(b.getString("dados", ""), new TypeToken<PedidoModel>(){}.getType());

        mainBinding.btnEnviarEmail.setOnClickListener(view-> enviarEmail());

        mainBinding.btnFinalizar.setOnClickListener( view -> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });

        mainBinding.bodyEmail.setText(
                pedido.toString()
        );

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        mainBinding.bodyEmail.setOnClickListener( view -> {
            ClipData clipData =  ClipData.newPlainText("venda", mainBinding.bodyEmail.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "Copiado!", Toast.LENGTH_SHORT).show();
        });
    }

    private void enviarEmail() {
        String email = mainBinding.edtEmailEnvio.getText().toString();
        String assunto = mainBinding.edtTituloEmail.getText().toString();
        String corpo = pedido.toString();
        
        String[] emailList = {email};
        final Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("message/rfc822")
                .setEmailTo(emailList)
                .setSubject(assunto)
                .setText(corpo)
                .setChooserTitle("Enviar venda.")
                .createChooserIntent();

        startActivity(intent);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){finish();}
        return super.onOptionsItemSelected(item);
    }
}