package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import benicio.solucoes.cadastropedido.adapter.AdapterViewProduto;
import benicio.solucoes.cadastropedido.databinding.ActivityVisualizarProdutosBinding;
import benicio.solucoes.cadastropedido.model.ProdutoModel;
import benicio.solucoes.cadastropedido.util.ProdutosUtils;

public class VisualizarProdutosActivity extends AppCompatActivity {

    private ActivityVisualizarProdutosBinding mainBinding;

    private List<ProdutoModel> listaProdutos;
    private List<String> listaNomeProdutos = new ArrayList<>();
//    private RecyclerView recylerProdutos;
//    private AdapterViewProduto adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityVisualizarProdutosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Produtos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listaProdutos = ProdutosUtils.returnProdutos(this);


        for ( ProdutoModel produtoModel : listaProdutos){
            listaNomeProdutos.add(produtoModel.getNome());
        }
        

        String[] sugestoes = listaNomeProdutos.toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sugestoes);

        mainBinding.autoCompleteFiltro.setAdapter(adapter);

        mainBinding.autoCompleteFiltro.setOnClickListener(view -> mainBinding.autoCompleteFiltro.showDropDown());

        mainBinding.autoCompleteFiltro.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFrase = (String) parent.getItemAtPosition(position);
            mainBinding.autoCompleteFiltro.setText(selectedFrase);

            for ( ProdutoModel produtoModel : listaProdutos){
                if ( produtoModel.getNome().equals(selectedFrase)){
                    mainBinding.textInfo.setText(
                            produtoModel.toString()
                    );
                    break;
                }
            }
            Toast.makeText(getApplicationContext(),  selectedFrase + " selecionado!", Toast.LENGTH_SHORT).show();
        });
        mainBinding.btnSearch.setOnClickListener( view -> {
            String selectedFrase = mainBinding.autoCompleteFiltro.getText().toString();

            for ( ProdutoModel produtoModel : listaProdutos){
                if ( produtoModel.getNome().contains(selectedFrase)){
                    mainBinding.textInfo.setText(
                            produtoModel.toString()
                    );
                    break;
                }
            }
        });
//        configurarRecyclerView();
    }

//    private void configurarRecyclerView() {
//        recylerProdutos = mainBinding.recyclerProdutos;
//        recylerProdutos.setLayoutManager(new LinearLayoutManager(this));
//        recylerProdutos.setHasFixedSize(true);
//        recylerProdutos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        adapter = new AdapterViewProduto(listaProdutos, this);
//        recylerProdutos.setAdapter(adapter);
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){finish();}
        return super.onOptionsItemSelected(item);
    }


}