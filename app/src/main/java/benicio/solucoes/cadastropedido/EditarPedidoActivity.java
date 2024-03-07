package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import benicio.solucoes.cadastropedido.databinding.ActivityEditarPedidoBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.PedidoModel;

public class EditarPedidoActivity extends AppCompatActivity {

    private ActivityEditarPedidoBinding mainBinding;
    private DatabaseReference refPedidos = FirebaseDatabase.getInstance().getReference().getRoot().child("pedidos");
    private Dialog loadingDialog;
    private Bundle b;
    private PedidoModel pedido;
    private String idPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityEditarPedidoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        getSupportActionBar().setTitle("Editar Pedido");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configurarLoadingDialog();
        b = getIntent().getExtras();

        idPedido = b.getString("idPedido", "");
        loadingDialog.show();
        refPedidos.child(idPedido).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.dismiss();
                if (snapshot.exists()) {
                    pedido = snapshot.getValue(PedidoModel.class);
                    configurarEdits();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finish();
                Toast.makeText(EditarPedidoActivity.this, "Tente novamente...", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });

        mainBinding.btnProdutos.setOnClickListener(view -> {
            loadingDialog.show();
            pedido.setLojaVendedor(mainBinding.edtLoja.getText().toString());
            pedido.setData(mainBinding.edtData.getText().toString());
            pedido.setIdAgente(mainBinding.edtAgente.getText().toString());
            pedido.setNomeEstabelecimento(mainBinding.edtEstabelecimento.getText().toString());
            pedido.setNomeComprador(mainBinding.edtComprador.getText().toString());
            pedido.setEmail(mainBinding.edtEmail.getText().toString());
            pedido.setTele(mainBinding.edtTelefone.getText().toString());
            pedido.setCnpj(mainBinding.edtCnpj.getText().toString());
            pedido.setInscriEstadual(mainBinding.edtEstadual.getText().toString());
            pedido.setFormaPagamento(mainBinding.edtFormaPagamento.getText().toString());
            pedido.setEnderecoCompleto(mainBinding.edtEndereco.getText().toString());
            pedido.setEnderecoEntrega(mainBinding.edtEnderecoEntrega.getText().toString());
            pedido.setObsEntrega(mainBinding.edtObsEntrega.getText().toString());
            pedido.setCep(mainBinding.edtCep.getText().toString());

            refPedidos.child(idPedido).setValue(pedido).addOnCompleteListener(task -> {
                loadingDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Pedido Atualizado com Sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Tente Novamente...", Toast.LENGTH_SHORT).show();
                }
            });
        });
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

    private void configurarEdits() {
        mainBinding.edtLoja.setText(pedido.getLojaVendedor());
        mainBinding.edtData.setText(pedido.getData());
        mainBinding.edtAgente.setText(pedido.getIdAgente());
        mainBinding.edtEstabelecimento.setText(pedido.getNomeEstabelecimento());
        mainBinding.edtComprador.setText(pedido.getNomeComprador());
        mainBinding.edtEmail.setText(pedido.getEmail());
        mainBinding.edtTelefone.setText(pedido.getTele());
        mainBinding.edtEstadual.setText(pedido.getInscriEstadual());
        mainBinding.edtFormaPagamento.setText(pedido.getFormaPagamento());
        mainBinding.edtEndereco.setText(pedido.getEnderecoCompleto());
        mainBinding.edtCep.setText(pedido.getCep());
        mainBinding.edtEnderecoEntrega.setText(pedido.getEnderecoEntrega());
        mainBinding.edtObsEntrega.setText(pedido.getObsEntrega());
        mainBinding.edtCnpj.setText(pedido.getCnpj());
    }
}