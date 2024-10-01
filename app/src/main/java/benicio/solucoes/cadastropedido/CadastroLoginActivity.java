package benicio.solucoes.cadastropedido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import benicio.solucoes.cadastropedido.databinding.ActivityCadastroLoginBinding;
import benicio.solucoes.cadastropedido.databinding.ActivityEnviarEmailBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.ResponseModel;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.service.ApiServices;
import benicio.solucoes.cadastropedido.util.RetrofitApiApp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroLoginActivity extends AppCompatActivity {

    private ActivityCadastroLoginBinding mainBinding;
    private Dialog dialogCarregando;
    private SharedPreferences user_prefs;
    private SharedPreferences.Editor editor;
    private ApiServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCadastroLoginBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        user_prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        editor = user_prefs.edit();
        apiServices = RetrofitApiApp.criarService(
                RetrofitApiApp.criarRetrofit()
        );

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (!user_prefs.getString("email", "").isEmpty()) {
            finish();
            startActivity(new Intent(this, MenuPedidoOrCreditoActivity.class));
            Toast.makeText(this, "Bem-Vindo de Volta!", Toast.LENGTH_SHORT).show();
        }

        configurarDialogCarregando();

        mainBinding.btnFazerCadastro.setOnClickListener(view -> {
            String nome, email, senha;

            nome = mainBinding.edtNome.getText().toString();
            email = mainBinding.edtEmailCadastro.getText().toString().trim().toLowerCase();
            senha = mainBinding.edtSenhaCasdastro.getText().toString();

            UserModel novoUsuario = new UserModel(nome, email, senha);

            if (!email.isEmpty() && !senha.isEmpty()) {
                dialogCarregando.show();

                apiServices.registrarUsuario(novoUsuario).enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        ResponseModel responseModel = response.body();

                        if (responseModel.isSuccess()) {
                            finish();
                            Toast.makeText(CadastroLoginActivity.this, "Usuário criado com sucesso.", Toast.LENGTH_SHORT).show();
                            editor.putString("email", novoUsuario.getEmail()).apply();
                            startActivity(new Intent(CadastroLoginActivity.this, MenuPedidoOrCreditoActivity.class));
                        } else {
                            exibirError(responseModel.getMsg());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        exibirError("Erro de conexão, tente novamente.");
                    }
                });
            }
        });

        mainBinding.btnFazerLogin.setOnClickListener(view -> {
            String email, senha;

            email = mainBinding.edtEmailLogin.getText().toString().toLowerCase().trim();
            senha = mainBinding.edtSenhaLogin.getText().toString();
            UserModel logarUsuario = new UserModel("", email, senha);

            if (email.equals("adm") && senha.equals("adm@2332")) {
                finish();
                startActivity(new Intent(this, AdminActivity.class));
            } else {
                if (!email.isEmpty() && !senha.isEmpty()) {
                    dialogCarregando.show();

                    apiServices.logarUsuario(logarUsuario).enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                            ResponseModel responseModel = response.body();

                            if (responseModel.isSuccess()) {
                                finish();
                                startActivity(new Intent(CadastroLoginActivity.this, MenuPedidoOrCreditoActivity.class));
                                editor.putString("email", logarUsuario.getEmail()).apply();
                                Toast.makeText(CadastroLoginActivity.this, "Bem-vindo de volta!", Toast.LENGTH_SHORT).show();
                            } else {
                                exibirError(responseModel.getMsg());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                            exibirError("Problema de conexão da internet!");
                        }
                    });
                }
            }
        });

    }

    private void exibirError(String s) {
        dialogCarregando.dismiss();
        Toast.makeText(this, "error: " + s, Toast.LENGTH_SHORT).show();
    }

    private void configurarDialogCarregando() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingLayoutBinding dialogBinding = LoadingLayoutBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }
}