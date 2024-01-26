package benicio.solucoes.cadastropedido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
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
import benicio.solucoes.cadastropedido.model.UserModel;

public class CadastroLoginActivity extends AppCompatActivity {

    private ActivityCadastroLoginBinding mainBinding;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference().getRoot().child("usuarios");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Dialog dialogCarregando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCadastroLoginBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        if ( user != null){
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "Bem-Vindo de Volta!", Toast.LENGTH_SHORT).show();
        }

        configurarDialogCarregando();

        mainBinding.btnFazerCadastro.setOnClickListener( view -> {
            String nome,email,senha,id;

            id = UUID.randomUUID().toString();
            nome = mainBinding.edtNome.getText().toString();
            email = mainBinding.edtEmailCadastro.getText().toString();
            senha = mainBinding.edtSenhaCasdastro.getText().toString();

            UserModel novoUsuario = new UserModel(
                    id, nome, email, senha
            );

            if ( !email.isEmpty() && !senha.isEmpty()){
                dialogCarregando.show();

                auth.createUserWithEmailAndPassword(novoUsuario.getEmail(), novoUsuario.getSenha()).addOnCompleteListener(criarTask -> {
                    if( criarTask.isSuccessful()){
                        refUsuarios.child(novoUsuario.getId()).setValue(novoUsuario).addOnCompleteListener(taskUsuario -> {
                            if (taskUsuario.isSuccessful()) {
                                auth.signInWithEmailAndPassword(novoUsuario.getEmail(), novoUsuario.getSenha()).addOnCompleteListener(logarTask -> {
                                    if (logarTask.isSuccessful()) {
                                        finish();
                                        Toast.makeText(this, "Usuário criado com sucesso.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, MainActivity.class));
                                    } else {
                                        exibirError("Conta criada, faça login agora!");
                                    }
                                });
                            } else {
                                exibirError("Erro de conexão, tente novamente.");
                            }
                        });
                    }else {
                        try {
                            throw criarTask.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            exibirError("Senha fraca!");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            exibirError("Email inválido!");
                        } catch (FirebaseAuthUserCollisionException e) {
                            exibirError("Já existe um usuário com o mesmo email!");
                        } catch (Exception e) {
                            exibirError(e.getMessage());
                        }
                    }
                });
            }
        });

        mainBinding.btnFazerLogin.setOnClickListener( view -> {
            String email,senha;

            email = mainBinding.edtEmailLogin.getText().toString();
            senha = mainBinding.edtSenhaLogin.getText().toString();

            if ( email.equals("adm") && senha.equals("adm@2332")){
                finish();
                startActivity(new Intent(this, AdminActivity.class));
            }else{
                if( !email.isEmpty() && !senha.isEmpty()){
                    dialogCarregando.show();
                    auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener( loginTask -> {
                        if ( loginTask.isSuccessful() ){
                            finish();
                            startActivity(new Intent(this, MainActivity.class));
                            Toast.makeText(this, "Bem-vindo de volta!", Toast.LENGTH_SHORT).show();
                        }else{
                            try {
                                throw loginTask.getException();
                            }
                            catch (FirebaseNetworkException e){
                                exibirError("Problema de conexão da internet!");
                            }
                            catch (FirebaseAuthInvalidUserException e){
                                exibirError("Usuário não cadastrado!");
                            }
                            catch (FirebaseAuthInvalidCredentialsException e){
                                exibirError("Credenciais inválidas!");
                            }
                            catch (Exception e){
                                exibirError(e.getMessage());
                            }
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