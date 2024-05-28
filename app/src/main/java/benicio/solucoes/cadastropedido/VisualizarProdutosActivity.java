package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import benicio.solucoes.cadastropedido.databinding.ActivityVisualizarProdutosBinding;
import benicio.solucoes.cadastropedido.dblocal.ProdutosDAO;
import benicio.solucoes.cadastropedido.model.ProdutoModel;

public class VisualizarProdutosActivity extends AppCompatActivity {

    private boolean podePesquisar = true;
    private ProdutosDAO produtosDAO;
    private ActivityVisualizarProdutosBinding mainBinding;
    private List<ProdutoModel> listaProdutos = new ArrayList<>();

    //    private List<ProdutoModel> listaNomeProdutosAdapter = new ArrayList<>();
//    private List<ProdutoModel> listaNomeProdutosHelper = new ArrayList<>();
    private List<String> listaNomeProdutos = new ArrayList<>();

//    private AdapterProdutoVisualizacao adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityVisualizarProdutosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Produtos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        produtosDAO = new ProdutosDAO(VisualizarProdutosActivity.this);

//        mainBinding.rvProdutos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        mainBinding.rvProdutos.setLayoutManager(new LinearLayoutManager(this));
//        mainBinding.rvProdutos.setHasFixedSize(true);
//        adapter = new AdapterProdutoVisualizacao(listaNomeProdutosAdapter, this);
//        mainBinding.rvProdutos.setAdapter(adapter);

        new Thread() {
            @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
            @Override
            public void run() {
                super.run();


//                listaProdutos = ProdutosUtils.returnProdutos(VisualizarProdutosActivity.this);
                listaProdutos = produtosDAO.listarProdutos();
//                listaNomeProdutosHelper.addAll(listaProdutos);

//                int maxItens = 100;
                for (ProdutoModel produtoModel : listaProdutos) {
                    listaNomeProdutos.add(produtoModel.getNome());
                    listaNomeProdutos.add(produtoModel.getSku());
                    listaNomeProdutos.add(produtoModel.getFornecedor());

//                    if ( maxItens > 0){
//                        listaNomeProdutosAdapter.add(produtoModel);
//                        listaNomeProdutosHelper.remove(produtoModel);
//                    }
//                    maxItens--;
                }

                runOnUiThread(() -> {
//                    mainBinding.mais.setVisibility(View.VISIBLE);
                    mainBinding.infoProduto.setText(
                            String.format("Foram encontrados %d produtos, filtre por fornecedor, nome ou sku!", listaProdutos.size())
                    );
                    mainBinding.progressLista.setVisibility(View.GONE);
//                    adapter.notifyDataSetChanged();
                });


                String[] sugestoes = listaNomeProdutos.toArray(new String[0]);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(VisualizarProdutosActivity.this, android.R.layout.simple_dropdown_item_1line, sugestoes);

                mainBinding.btnLimpar.setOnClickListener(view -> {
                    mainBinding.labalinfoproduto.setVisibility(View.GONE);
                    mainBinding.autoCompleteFiltro.setText("");
                    mainBinding.textInfo.setText("");
                });


                runOnUiThread(() -> mainBinding.autoCompleteFiltro.setAdapter(adapter));

                mainBinding.autoCompleteFiltro.setOnClickListener(view -> mainBinding.autoCompleteFiltro.showDropDown());

                mainBinding.autoCompleteFiltro.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedFrase = (String) parent.getItemAtPosition(position);
                    mainBinding.autoCompleteFiltro.setText(selectedFrase);

                    for (ProdutoModel produtoModel : listaProdutos) {
                        if (produtoModel.getNome().equals(selectedFrase)) {
                            mainBinding.textInfo.setText(
                                    produtoModel.toString()
                            );
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), selectedFrase + " selecionado!", Toast.LENGTH_SHORT).show();
                });
            }
        }.start();


        mainBinding.btnSearch.setOnClickListener(view -> {
            if (podePesquisar) {
                mainBinding.progressPesquisa.setVisibility(View.VISIBLE);
                podePesquisar = false;
                new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        String selectedFrase = mainBinding.autoCompleteFiltro.getText().toString();

                        if (!selectedFrase.isEmpty()) {
                            StringBuilder info = new StringBuilder();
                            for (ProdutoModel produtoModel : produtosDAO.buscarProduto(selectedFrase)) {
                                info.append(produtoModel.toString()).append("\n\n");
                            }

                            runOnUiThread(() -> {
                                mainBinding.progressPesquisa.setVisibility(View.GONE);
                                mainBinding.textInfo.setText(info.toString());
                                podePesquisar = true;
                            });
                        }
                    }
                }.start();
            }


        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}