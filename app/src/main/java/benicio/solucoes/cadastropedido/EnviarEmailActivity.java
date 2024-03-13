package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ShareCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.UUID;

import benicio.solucoes.cadastropedido.databinding.ActivityEnviarEmailBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.CreditoModel;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.UserModel;

public class EnviarEmailActivity extends AppCompatActivity {


    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference().getRoot().child("usuarios");
    private DatabaseReference refCreditos = FirebaseDatabase.getInstance().getReference().getRoot().child("creditos");
    private DatabaseReference refPedidos = FirebaseDatabase.getInstance().getReference().getRoot().child("pedidos");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ActivityEnviarEmailBinding mainBinding;
    private Bundle b;
    private PedidoModel pedido;
    private CreditoModel credito;
    private String idVendedor;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityEnviarEmailBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Enviar E-mail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configurarLoadingDialog();
        configurarIdVendedor();

        String[] sugestoesEmails = {"pedidos@redcloudtechnology.com", "credito@redcloudtechnology.com"};
        ArrayAdapter<String> adapterEmails = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sugestoesEmails);
        mainBinding.edtEmailEnvio.setAdapter(adapterEmails);
        mainBinding.edtEmailEnvio.setOnClickListener(view -> mainBinding.edtEmailEnvio.showDropDown());
        mainBinding.edtEmailEnvio.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFrase = (String) parent.getItemAtPosition(position);
            mainBinding.edtEmailEnvio.setText(selectedFrase);
            Toast.makeText(getApplicationContext(), selectedFrase + " selecionado!", Toast.LENGTH_SHORT).show();
        });


        b = getIntent().getExtras();

        assert b != null;
        if (b.getBoolean("credito", false)) {
            credito = new Gson().fromJson(b.getString("dados", ""), new TypeToken<CreditoModel>() {
            }.getType());

        } else {
            pedido = new Gson().fromJson(b.getString("dados", ""), new TypeToken<PedidoModel>() {
            }.getType());
        }

        mainBinding.btnEnviarEmail.setOnClickListener(view -> enviarEmail());

        mainBinding.btnFinalizar.setOnClickListener(view -> {

            assert b != null;
            if (b.getBoolean("credito", false)) {
                credito.setIdVendedor(idVendedor);
                loadingDialog.show();
                refCreditos.child(credito.getId()).setValue(credito).addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Crédito Solicitado", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(this, MenuCreditoActivity.class));
                    }
                });
            } else {
                pedido.setId(UUID.randomUUID().toString());
                pedido.setIdVendedor(idVendedor);
                loadingDialog.show();
                refPedidos.child(pedido.getId()).setValue(pedido).addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Pedido Criado!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(this, MainActivity.class));
                    }
                });
            }

        });

        assert b != null;
        if (b.getBoolean("credito", false)) {
            mainBinding.bodyEmail.setText(
                    credito.toString()
            );
        } else {
            mainBinding.bodyEmail.setText(
                    pedido.toInformacao(false)
            );
        }


        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        mainBinding.bodyEmail.setOnClickListener(view -> {
            ClipData clipData = ClipData.newPlainText("venda", mainBinding.bodyEmail.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "Copiado!", Toast.LENGTH_SHORT).show();
        });
    }

    private void configurarLoadingDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setView(LoadingLayoutBinding.inflate(getLayoutInflater()).getRoot());
        loadingDialog = b.create();
    }

    private void configurarIdVendedor() {

        loadingDialog.show();
        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.dismiss();

                for (DataSnapshot dado : snapshot.getChildren()) {
                    UserModel userModel = dado.getValue(UserModel.class);
                    if (userModel.getEmail().equals(auth.getCurrentUser().getEmail())) {
                        idVendedor = userModel.getId();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
            }
        });
    }

    private void enviarEmail() {
        String email = mainBinding.edtEmailEnvio.getText().toString();
        String assunto = mainBinding.edtTituloEmail.getText().toString();
        String corpo = mainBinding.bodyEmail.getText().toString();

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
        if (item.getItemId() == android.R.id.home) {
            finish();
            if (b.getBoolean("credito", false)) {
                startActivity(new Intent(this, MenuCreditoActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}