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
import android.util.Log;
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

import org.apache.commons.logging.LogFactory;

import java.util.Objects;
import java.util.UUID;

import benicio.solucoes.cadastropedido.databinding.ActivityEnviarEmailBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.CreditoModel;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ResponseModel;
import benicio.solucoes.cadastropedido.model.ResponseModelPedidoCredito;
import benicio.solucoes.cadastropedido.model.ResponseModelPedidoProduto;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.service.ApiServices;
import benicio.solucoes.cadastropedido.util.MathUtils;
import benicio.solucoes.cadastropedido.util.RetrofitApiApp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnviarEmailActivity extends AppCompatActivity {

    private static final org.apache.commons.logging.Log log = LogFactory.getLog(EnviarEmailActivity.class);
    private ApiServices apiServices;

    private ActivityEnviarEmailBinding mainBinding;
    private Bundle b;
    private PedidoModel pedido;
    private CreditoModel credito;
    private Dialog loadingDialog;

    Dialog dialogJanela;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityEnviarEmailBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        apiServices = RetrofitApiApp.criarService(
                RetrofitApiApp.criarRetrofit()
        );

        Objects.requireNonNull(getSupportActionBar()).setTitle("Enviar E-mail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configurarLoadingDialog();

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

        b = getIntent().getExtras();
        assert b != null;
        if (b.getBoolean("credito", false)) {
            credito = new Gson().fromJson(b.getString("dados", ""), new TypeToken<CreditoModel>() {
            }.getType());
        } else {
            pedido = new Gson().fromJson(b.getString("dados", ""), new TypeToken<PedidoModel>() {
            }.getType());

        }

        salvarPedido();

    }

    private void irParaMenu() {
        finish();
        startActivity(new Intent(this, MenuPedidoOrCreditoActivity.class));
    }

    private void salvarPedido() {
        loadingDialog.show();

        if (b.getBoolean("credito", false)) {
            apiServices.salvarPedidoCredito(credito).enqueue(new Callback<ResponseModelPedidoCredito>() {
                @Override
                public void onResponse(@NonNull Call<ResponseModelPedidoCredito> call, @NonNull Response<ResponseModelPedidoCredito> response) {
                    loadingDialog.dismiss();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Toast.makeText(EnviarEmailActivity.this, "Pedido Salvo", Toast.LENGTH_SHORT).show();
                        mainBinding.bodyEmail.setText(response.body().getMsg().toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseModelPedidoCredito> call, @NonNull Throwable t) {
                    Toast.makeText(EnviarEmailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            });
        } else {
            apiServices.salvarPedidoProduto(pedido).enqueue(new Callback<ResponseModelPedidoProduto>() {
                @Override
                public void onResponse(@NonNull Call<ResponseModelPedidoProduto> call, @NonNull Response<ResponseModelPedidoProduto> response) {
                    loadingDialog.dismiss();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Toast.makeText(EnviarEmailActivity.this, "Pedido Salvo", Toast.LENGTH_SHORT).show();
                        mainBinding.bodyEmail.setText(response.body().getMsg().toInformacao(false));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseModelPedidoProduto> call, @NonNull Throwable t) {
                    Toast.makeText(EnviarEmailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
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
        janelaEmailSalvo.setPositiveButton("Ir para Menu", (d, i) -> irParaMenu());
        janelaEmailSalvo.setNegativeButton("Enivar Novamente", (d, i) -> {
            dialogJanela.dismiss();
            enviarEmail();
        });
        janelaEmailSalvo.setNeutralButton("Voltar", (d, i) -> dialogJanela.dismiss());
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