package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


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

        new Thread() {
            @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
            @Override
            public void run() {
                super.run();


                listaProdutos = produtosDAO.listarProdutos();

                for (ProdutoModel produtoModel : listaProdutos) {
                    listaNomeProdutos.add(produtoModel.getNome() + " -F- " + produtoModel.getFornecedor());
                    listaNomeProdutos.add(produtoModel.getSku());
                }

                runOnUiThread(() -> {
                    mainBinding.infoProduto.setText(
                            String.format("Foram encontrados %d produtos, filtre por fornecedor, nome ou sku!", listaProdutos.size())
                    );
                    mainBinding.progressLista.setVisibility(View.GONE);
                });


                String[] sugestoes = listaNomeProdutos.toArray(new String[0]);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(VisualizarProdutosActivity.this, R.layout.custom_dropdown_item, R.id.textoProdutos, sugestoes);

                mainBinding.btnLimpar.setOnClickListener(view -> {
                    mainBinding.labalinfoproduto.setVisibility(View.GONE);
                    mainBinding.autoCompleteFiltro.setText("");
                    removerLinha();
                });


                runOnUiThread(() -> mainBinding.autoCompleteFiltro.setAdapter(adapter));

                mainBinding.autoCompleteFiltro.setOnClickListener(view -> mainBinding.autoCompleteFiltro.showDropDown());

                mainBinding.autoCompleteFiltro.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedFrase = (String) parent.getItemAtPosition(position);

                    mainBinding.autoCompleteFiltro.setText((selectedFrase.contains("-F-") ?selectedFrase.split("-F-")[0]:selectedFrase).trim());

                    for (ProdutoModel produtoModel : listaProdutos) {
                        if (produtoModel.getNome().equals(selectedFrase.contains("-F-") ? selectedFrase.split("-F-")[0]:selectedFrase)) {
                            addLinha(produtoModel);
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), selectedFrase + " selecionado!", Toast.LENGTH_SHORT).show();
                });
            }
        }.start();


        mainBinding.btnSearch.setOnClickListener(view -> {
            if (podePesquisar) {
                removerLinha();
                mainBinding.progressPesquisa.setVisibility(View.VISIBLE);
                podePesquisar = false;
                new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        String selectedFrase = mainBinding.autoCompleteFiltro.getText().toString();

                        if (!selectedFrase.isEmpty()) {
                            List<ProdutoModel> info = new ArrayList<>(produtosDAO.buscarProduto(selectedFrase.contains("-F-") ? selectedFrase.split("-F-")[0]:selectedFrase));

                            runOnUiThread(() -> {
                                mainBinding.progressPesquisa.setVisibility(View.GONE);
                                for (ProdutoModel produtoModel : info) {
                                    addLinha(produtoModel);
                                }
                                podePesquisar = true;
                            });
                        }
                    }
                }.start();
            }


        });
    }


    private void removerLinha() {
        for (int i = 0; i < mainBinding.tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) mainBinding.tableLayout.getChildAt(i);
            if ( tableRow.getId() != mainBinding.rowPrincipal.getId()){
                mainBinding.tableLayout.removeViewAt(i);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void addLinha(ProdutoModel produto) {
        TableRow tableRow = new TableRow(this);
        tableRow.setId(new Random().nextInt(1000));

        TextView textViewNome = new TextView(this);
        textViewNome.setText(produto.getNome());
        textViewNome.setPadding(8, 8, 8, 8);
        textViewNome.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        textViewNome.setGravity(Gravity.CENTER);

        TextView textViewEstoque = new TextView(this);
        textViewEstoque.setText(String.valueOf(produto.getEstoque()));
        textViewEstoque.setPadding(8, 8, 8, 8);
        textViewEstoque.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        textViewEstoque.setGravity(Gravity.CENTER);

        TextView textViewSKU = new TextView(this);
        textViewSKU.setText(produto.getSku());
        textViewSKU.setPadding(8, 8, 8, 8);
        textViewSKU.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        textViewSKU.setGravity(Gravity.CENTER);

        TextView textViewPreco = new TextView(this);
        textViewPreco.setText(produto.getPrecoFormatado());
        textViewPreco.setPadding(8, 8, 8, 8);
        textViewPreco.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        textViewPreco.setGravity(Gravity.CENTER);

        tableRow.addView(textViewNome);
        tableRow.addView(textViewEstoque);
        tableRow.addView(textViewSKU);
        tableRow.addView(textViewPreco);

        mainBinding.tableLayout.addView(tableRow);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}