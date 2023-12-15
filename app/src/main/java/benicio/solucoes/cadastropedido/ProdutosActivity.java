package benicio.solucoes.cadastropedido;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.adapter.AdapterProduto;
import benicio.solucoes.cadastropedido.databinding.ActivityProdutosBinding;
import benicio.solucoes.cadastropedido.model.ItemCompra;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ProdutoModel;
import benicio.solucoes.cadastropedido.util.MathUtils;
import benicio.solucoes.cadastropedido.util.ProdutosUtils;

public class ProdutosActivity extends AppCompatActivity {
    private RecyclerView r;
    private ActivityProdutosBinding mainBinding;
    private Bundle b;
    private PedidoModel pedidoModel;

    private List<ProdutoModel> listaProdutos;

    private List<ItemCompra> listaCompra = new ArrayList<>();
    private AdapterProduto adapterProdutos;
    private List<String> listaNomeProdutos = new ArrayList<>();

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityProdutosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Produtos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configurarRecycler();

        b = getIntent().getExtras();
        Gson gson = new Gson();
        Type type = new TypeToken<PedidoModel>(){}.getType();
        pedidoModel = gson.fromJson(b.getString("dadosPedido", ""), type);

        listaProdutos = ProdutosUtils.returnProdutos(this);
        for ( ProdutoModel produtoModel : listaProdutos){
            listaNomeProdutos.add(produtoModel.getNome());
        }

        String[] sugestoes = listaNomeProdutos.toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sugestoes);

        mainBinding.edtNomeProduto.setAdapter(adapter);

        mainBinding.edtNomeProduto.setOnClickListener(view -> mainBinding.edtNomeProduto.showDropDown());

        mainBinding.edtNomeProduto.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFrase = (String) parent.getItemAtPosition(position);
            mainBinding.edtNomeProduto.setText(selectedFrase);

            for ( ProdutoModel produtoModel : listaProdutos){
                if ( produtoModel.getNome().equals(selectedFrase)){
                    mainBinding.edtSKU.setText(produtoModel.getSku());
                    mainBinding.edtValor.setText( "R$ " + produtoModel.getPreco().replace(".", ","));
                    mainBinding.textEstoque.setText("Esse produto tem "+produtoModel.getEstoque()+" no estoque.");
                    mainBinding.textEstoque.setVisibility(View.VISIBLE);
                    break;
                }
            }
            Toast.makeText(getApplicationContext(),  selectedFrase + " selecionado!", Toast.LENGTH_SHORT).show();
        });

        mainBinding.btnCalcular.setOnClickListener( view -> {
            Double valorTotalPorItem =
                    MathUtils.converterParaDouble(
                            mainBinding.edtValor.getText().toString()
                    ) *
                    MathUtils.converterParaDouble(
                            mainBinding.edtQTD.getText().toString()
                    );

            mainBinding.edtValorTotal.setText(
                    MathUtils.formatarMoeda(valorTotalPorItem)
            );


            Double descontoPorcentagem = (Double.parseDouble(
                    mainBinding.edtDesconto.getText().toString().isEmpty() ? "0" :
                            mainBinding.edtDesconto.getText().toString()
            )) / 100;

            Double desconto = valorTotalPorItem * descontoPorcentagem;


            mainBinding.edtValorTotalDesconto.setText(
                    MathUtils.formatarMoeda(
                            valorTotalPorItem - desconto
                    )
            );

        });
        mainBinding.btnAddProdutos.setOnClickListener( view -> {
            listaCompra.add(
                    new ItemCompra(
                            mainBinding.edtNomeProduto.getText().toString(),
                            mainBinding.edtSKU.getText().toString(),
                            mainBinding.edtValor.getText().toString(),
                            mainBinding.edtQTD.getText().toString(),
                            mainBinding.edtValorTotal.getText().toString(),
                            mainBinding.edtDesconto.getText().toString(),
                            mainBinding.edtValorTotalDesconto.getText().toString()
                    )
            );

            adapterProdutos.notifyDataSetChanged();

            Double valorAtual =
                    MathUtils.converterParaDouble(
                            mainBinding.textValorTotal.getText().toString().split(" ")[2]
                    );

            valorAtual += MathUtils.converterParaDouble(mainBinding.edtValorTotalDesconto.getText().toString());

            mainBinding.textValorTotal.setText("Total Compra: "+ MathUtils.formatarMoeda(valorAtual));

            mainBinding.edtNomeProduto.setText("");
            mainBinding.edtSKU.setText("");
            mainBinding.edtValor.setText("");
            mainBinding.edtQTD.setText("");
            mainBinding.edtValorTotal.setText("");
            mainBinding.edtDesconto.setText("");
            mainBinding.edtValorTotalDesconto.setText("");



        });
        mainBinding.btnEnviarEmail.setOnClickListener( view -> {
            List<ItemCompra> listaAntigaDeProdutos = pedidoModel.getProdutos();
            pedidoModel.setTotalCompra(mainBinding.textValorTotal.getText().toString());
            listaAntigaDeProdutos.addAll(listaCompra);
            pedidoModel.setProdutos(listaAntigaDeProdutos);
            Intent i = new Intent(this, EnviarEmailActivity.class);
            i.putExtra("dados", new Gson().toJson(pedidoModel));
            startActivity(i);
            finish();
        });
    }

    private void configurarRecycler() {
        r = mainBinding.recyclerProdutos;
        r.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        r.setHasFixedSize(true);
        r.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterProdutos = new AdapterProduto(listaCompra, this, mainBinding.textValorTotal);
        r.setAdapter(adapterProdutos);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){finish();}
        return super.onOptionsItemSelected(item);
    }
}