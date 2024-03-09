package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.databinding.ActivityInfosBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.ClienteModel;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ProdutoModel;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.util.ClientesUtil;

public class InfosActivity extends AppCompatActivity {

    private UserModel usuarioAtual;
    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference().getRoot().child("usuarios");
    private Dialog loadingDialog;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ActivityInfosBinding mainBinding;
    private List<ClienteModel> clientes = new ArrayList<>();
    private List<String> nomesEstabelecimentos = new ArrayList<>();
    private List<String> nomesClientes = new ArrayList<>();
    private List<String> emails = new ArrayList<>();
    private List<String> cnpjs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityInfosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Pedido");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configurarLoadingDialog();
        encontrarUsuarioAtual();

        clientes.addAll(ClientesUtil.returnClientes(this));

        for (ClienteModel clienteModel : clientes) {
            nomesEstabelecimentos.add(clienteModel.getNomeEstabelecimento());
            nomesClientes.add(clienteModel.getNomeCliente() + " " + clienteModel.getSobreNome());
            emails.add(clienteModel.getEmail());
            cnpjs.add(clienteModel.getCnpj());
        }

        String[] sugestoesNomesEstabelecimentos = nomesEstabelecimentos.toArray(new String[0]);
        String[] sugestoesNomesClientes = nomesClientes.toArray(new String[0]);
        String[] sugestoesEmails = emails.toArray(new String[0]);
        String[] sugestoesCnpjs = cnpjs.toArray(new String[0]);

        ArrayAdapter<String> adapterNomesEstabelecimentos = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sugestoesNomesEstabelecimentos);
        ArrayAdapter<String> adapterNomesClientes = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sugestoesNomesClientes);
        ArrayAdapter<String> adapterNomesEmails = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sugestoesEmails);
        ArrayAdapter<String> adapterNomesCnpjs = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sugestoesCnpjs);

        mainBinding.edtEstabelecimento.setAdapter(adapterNomesEstabelecimentos);
        mainBinding.edtComprador.setAdapter(adapterNomesClientes);
        mainBinding.edtEmail.setAdapter(adapterNomesEmails);
        mainBinding.edtCnpj.setAdapter(adapterNomesCnpjs);

        mainBinding.edtEstabelecimento.setOnClickListener(view -> mainBinding.edtEstabelecimento.showDropDown());
        mainBinding.edtComprador.setOnClickListener(view -> mainBinding.edtComprador.showDropDown());
        mainBinding.edtEmail.setOnClickListener(view -> mainBinding.edtEmail.showDropDown());
        mainBinding.edtCnpj.setOnClickListener(view -> mainBinding.edtCnpj.showDropDown());

        mainBinding.edtEstabelecimento.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFrase = (String) parent.getItemAtPosition(position);

            mainBinding.edtEstabelecimento.setText(selectedFrase);

            for (ClienteModel clienteModel : clientes) {
                if (clienteModel.getNomeEstabelecimento().equals(selectedFrase)) {
                    mainBinding.edtCnpj.setText(clienteModel.getCnpj());
                    mainBinding.edtEmail.setText(clienteModel.getEmail());
                    mainBinding.edtComprador.setText(clienteModel.getNomeCliente() + " " + clienteModel.getSobreNome());
                    mainBinding.edtEstabelecimento.setText(clienteModel.getNomeEstabelecimento());
                    mainBinding.edtEndereco.setText(clienteModel.getEndereco());
                    mainBinding.edtTelefone.setText(clienteModel.getTelefone());
                    break;
                }
            }
            Toast.makeText(getApplicationContext(), selectedFrase + " selecionado!", Toast.LENGTH_SHORT).show();
        });

        mainBinding.edtComprador.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFrase = (String) parent.getItemAtPosition(position);

            mainBinding.edtComprador.setText(selectedFrase);

            for (ClienteModel clienteModel : clientes) {
                if ((clienteModel.getNomeCliente() + " " + clienteModel.getSobreNome()).equals(selectedFrase)) {
                    mainBinding.edtCnpj.setText(clienteModel.getCnpj());
                    mainBinding.edtEmail.setText(clienteModel.getEmail());
                    mainBinding.edtComprador.setText(clienteModel.getNomeCliente() + " " + clienteModel.getSobreNome());
                    mainBinding.edtEstabelecimento.setText(clienteModel.getNomeEstabelecimento());
                    mainBinding.edtEndereco.setText(clienteModel.getEndereco());
                    mainBinding.edtTelefone.setText(clienteModel.getTelefone());
                    break;
                }
            }
            Toast.makeText(getApplicationContext(), selectedFrase + " selecionado!", Toast.LENGTH_SHORT).show();
        });

        mainBinding.edtEmail.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFrase = (String) parent.getItemAtPosition(position);

            mainBinding.edtEmail.setText(selectedFrase);

            for (ClienteModel clienteModel : clientes) {
                if (clienteModel.getEmail().equals(selectedFrase)) {
                    mainBinding.edtCnpj.setText(clienteModel.getCnpj());
                    mainBinding.edtEmail.setText(clienteModel.getEmail());
                    mainBinding.edtComprador.setText(clienteModel.getNomeCliente() + " " + clienteModel.getSobreNome());
                    mainBinding.edtEstabelecimento.setText(clienteModel.getNomeEstabelecimento());
                    mainBinding.edtEndereco.setText(clienteModel.getEndereco());
                    mainBinding.edtTelefone.setText(clienteModel.getTelefone());
                    break;
                }
            }
            Toast.makeText(getApplicationContext(), selectedFrase + " selecionado!", Toast.LENGTH_SHORT).show();
        });

        mainBinding.edtCnpj.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFrase = (String) parent.getItemAtPosition(position);

            mainBinding.edtCnpj.setText(selectedFrase);

            for (ClienteModel clienteModel : clientes) {
                if (clienteModel.getCnpj().equals(selectedFrase)) {
                    mainBinding.edtCnpj.setText(clienteModel.getCnpj());
                    mainBinding.edtEmail.setText(clienteModel.getEmail());
                    mainBinding.edtComprador.setText(clienteModel.getNomeCliente() + " " + clienteModel.getSobreNome());
                    mainBinding.edtEstabelecimento.setText(clienteModel.getNomeEstabelecimento());
                    mainBinding.edtEndereco.setText(clienteModel.getEndereco());
                    mainBinding.edtTelefone.setText(clienteModel.getTelefone());
                    break;
                }
            }
            Toast.makeText(getApplicationContext(), selectedFrase + " selecionado!", Toast.LENGTH_SHORT).show();
        });


        mainBinding.btnProdutos.setOnClickListener(view -> {
            Intent i = new Intent(this, ProdutosActivity.class);

            Gson gson = new Gson();

            if (
                    mainBinding.edtLoja.getText().toString().isEmpty() ||
                            mainBinding.edtData.getText().toString().isEmpty() ||
                            mainBinding.edtEstabelecimento.getText().toString().isEmpty() ||
                            mainBinding.edtComprador.getText().toString().isEmpty() ||
                            mainBinding.edtEmail.getText().toString().isEmpty() ||
                            mainBinding.edtTelefone.getText().toString().isEmpty() ||
                            mainBinding.edtCnpj.getText().toString().isEmpty() ||
                            mainBinding.edtFormaPagamento.getText().toString().isEmpty() ||
                            mainBinding.edtEndereco.getText().toString().isEmpty() ||
                            mainBinding.edtEnderecoEntrega.getText().toString().isEmpty() ||
                            mainBinding.edtObsEntrega.getText().toString().isEmpty() ||
                            mainBinding.edtCep.getText().toString().isEmpty()
            ) {
                Toast.makeText(this, "Preencha Todos os Dados Obrigatórios!", Toast.LENGTH_LONG).show();
            } else {
                String idAgente = mainBinding.edtAgente.getText().toString();

                String dadosPedido = gson.toJson(
                        new PedidoModel(
                                mainBinding.edtLoja.getText().toString(),
                                mainBinding.edtData.getText().toString(),
                                idAgente,
                                mainBinding.edtEstabelecimento.getText().toString(),
                                mainBinding.edtComprador.getText().toString(),
                                mainBinding.edtEmail.getText().toString(),
                                mainBinding.edtTelefone.getText().toString(),
                                mainBinding.edtCnpj.getText().toString(),
                                mainBinding.edtEstadual.getText().toString(),
                                mainBinding.edtFormaPagamento.getText().toString(),
                                mainBinding.edtEndereco.getText().toString(),
                                mainBinding.edtEnderecoEntrega.getText().toString(),
                                mainBinding.edtObsEntrega.getText().toString(),
                                new ArrayList<>(),
                                mainBinding.edtCep.getText().toString()
                        )
                );

                i.putExtra("dadosPedido", dadosPedido);

                usuarioAtual.setIdAgente(idAgente);
                loadingDialog.show();
                refUsuarios.child(usuarioAtual.getId()).setValue(usuarioAtual).addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        startActivity(i);
                    } else {
                        Toast.makeText(this, "Tente novamente...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void encontrarUsuarioAtual() {
        try {
            loadingDialog.show();
            refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    loadingDialog.dismiss();

                    for (DataSnapshot dado : snapshot.getChildren()) {
                        if (dado.getValue(UserModel.class).getEmail().equals(user.getEmail())) {
                            usuarioAtual = dado.getValue(UserModel.class);
                            mainBinding.edtAgente.setText(usuarioAtual.getIdAgente());
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loadingDialog.dismiss();
                }
            });
        } catch (Exception ignored) {
        }

    }


    @Override

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarLoadingDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setView(LoadingLayoutBinding.inflate(getLayoutInflater()).getRoot());
        loadingDialog = b.create();
    }
}