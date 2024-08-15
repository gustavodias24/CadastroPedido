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
import benicio.solucoes.cadastropedido.util.MathUtils;

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

    Dialog dialogJanela;


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

        mainBinding.btnEnviarEmail.setOnClickListener(view -> enviarEmail());

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        mainBinding.bodyEmail.setOnClickListener(view -> {
            ClipData clipData = ClipData.newPlainText("venda", mainBinding.bodyEmail.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "Copiado!", Toast.LENGTH_SHORT).show();
        });


    }

    private void salvarPedido(){
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
            pedido.setIdVendedor(idVendedor);

            loadingDialog.show();

            refPedidos.child(pedido.getId()).setValue(pedido).addOnCompleteListener(task -> {
                loadingDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(EnviarEmailActivity.this, "Pedido Criado!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(EnviarEmailActivity.this, MainActivity.class));
                }
            });


        }
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
                    if (userModel.getEmail().toLowerCase().trim().equals(auth.getCurrentUser().getEmail().toLowerCase().trim())) {
                        idVendedor = userModel.getId();
                        b = getIntent().getExtras();
                        assert b != null;
                        if (b.getBoolean("credito", false)) {
                            gerarIdCredito();
                        } else {
                            gerarIdPedido();
                        }
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

    private void gerarIdPedido() {

        pedido = new Gson().fromJson(b.getString("dados", ""), new TypeToken<PedidoModel>() {
        }.getType());


        loadingDialog.show();
        refPedidos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dado : snapshot.getChildren()) {
                    count++;
                }

                loadingDialog.dismiss();
                pedido.setId(MathUtils.formatarNumero(count));
                mainBinding.bodyEmail.setText(
                        pedido.toInformacao(false)
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
            }
        });
    }

    private void gerarIdCredito() {
        credito = new Gson().fromJson(b.getString("dados", ""), new TypeToken<CreditoModel>() {
        }.getType());

        mainBinding.bodyEmail.setText(
                credito.toString()
        );

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

        AlertDialog.Builder janelaEmailSalvo = new AlertDialog.Builder(EnviarEmailActivity.this);
        janelaEmailSalvo.setTitle("Atenção");
        janelaEmailSalvo.setMessage("Escolha uma opção:");
        janelaEmailSalvo.setCancelable(false);
        janelaEmailSalvo.setPositiveButton("Salvar", (d, i) -> salvarPedido());
        janelaEmailSalvo.setNegativeButton("Enivar Novamente", (d,i) -> {
            dialogJanela.dismiss();
            enviarEmail();
        });
        janelaEmailSalvo.setNeutralButton("Voltar", (d,i) -> dialogJanela.dismiss());
        dialogJanela = janelaEmailSalvo.create();
        dialogJanela.show();
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