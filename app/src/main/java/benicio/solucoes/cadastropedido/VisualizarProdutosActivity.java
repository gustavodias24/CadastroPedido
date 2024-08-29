package benicio.solucoes.cadastropedido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


import benicio.solucoes.cadastropedido.databinding.ActivityVisualizarProdutosBinding;
import benicio.solucoes.cadastropedido.databinding.ExpandirImagemBinding;
import benicio.solucoes.cadastropedido.dblocal.ProdutosDAO;
import benicio.solucoes.cadastropedido.model.ImagemResponseModel;
import benicio.solucoes.cadastropedido.model.ProdutoModel;
import benicio.solucoes.cadastropedido.service.ProdutosServices;
import benicio.solucoes.cadastropedido.util.RetrofitUitl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisualizarProdutosActivity extends AppCompatActivity {

    private boolean podePesquisar = true;
    private ProdutosDAO produtosDAO;
    private ActivityVisualizarProdutosBinding mainBinding;
    private List<ProdutoModel> listaProdutos = new ArrayList<>();

    private List<String> listaNomeProdutos = new ArrayList<>();
    private ProdutosServices produtosServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityVisualizarProdutosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        produtosServices = RetrofitUitl.criarService(
                RetrofitUitl.criarRetrofit()
        );
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

                    mainBinding.autoCompleteFiltro.setText((selectedFrase.contains("-F-") ? selectedFrase.split("-F-")[0] : selectedFrase).trim());

                    for (ProdutoModel produtoModel : listaProdutos) {
                        if (produtoModel.getNome().equals(selectedFrase.contains("-F-") ? selectedFrase.split("-F-")[0] : selectedFrase)) {
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

                        String selectedFrase = mainBinding.autoCompleteFiltro.getText().toString().trim();

                        if (!selectedFrase.isEmpty()) {
                            List<ProdutoModel> info = new ArrayList<>(produtosDAO.buscarProduto(selectedFrase.contains("-F-") ? selectedFrase.split("-F-")[0] : selectedFrase));

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
        // Começa do final para evitar problemas ao remover views durante a iteração
        for (int i = mainBinding.tableLayout.getChildCount() - 1; i >= 0; i--) {
            View child = mainBinding.tableLayout.getChildAt(i);

            // Verifica se o child é um TableRow e não é o rowPrincipal
            if (child instanceof TableRow && child.getId() != mainBinding.rowPrincipal.getId()) {
                mainBinding.tableLayout.removeViewAt(i); // Remove a linha
                if (i > 0) { // Verifica se não é a primeira posição
                    View previousChild = mainBinding.tableLayout.getChildAt(i - 1);
                    if (!(previousChild instanceof TableRow)) {
                        mainBinding.tableLayout.removeViewAt(i - 1); // Remove o divider logo acima da linha removida
                    }
                }
            }
        }
    }


    private void addLinha(ProdutoModel produto) {
        TableRow tableRow = new TableRow(this);
        tableRow.setId(new Random().nextInt(1000));

        // Definindo o layout params específico para o ImageView dentro de um TableRow
        TableRow.LayoutParams imageViewParams = new TableRow.LayoutParams(
                100,  // largura em pixels
                100   // altura em pixels
        );
        imageViewParams.gravity = Gravity.CENTER;  // Centralizar a imagem no TableRow

        // Criar o ProgressBar
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(imageViewParams);
        progressBar.setIndeterminate(true);  // Definir como indeterminado

        // Criar o ImageView
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(imageViewParams);
        imageView.setVisibility(View.GONE);  // Inicialmente oculto até que a imagem seja carregada

        // Adicionar o ProgressBar à tabela
        tableRow.addView(imageView);
        tableRow.addView(progressBar);

        // Carregar a imagem com Picasso
        produtosServices.retornarImagem(produto.getSku()).enqueue(new Callback<ImagemResponseModel>() {
            @Override
            public void onResponse(Call<ImagemResponseModel> call, Response<ImagemResponseModel> response) {
                Picasso.get().load(response.body().getImg()).into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        // Esconder o ProgressBar e mostrar a imagem
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        // Tratar erro de carregamento, talvez mostrar uma imagem de erro
                        progressBar.setVisibility(View.GONE);
                    }
                });


                imageView.setOnClickListener(v -> {
                    AlertDialog.Builder expandirImagem = new AlertDialog.Builder(VisualizarProdutosActivity.this);
                    ExpandirImagemBinding imagemBinding = ExpandirImagemBinding.inflate(getLayoutInflater());
                    Picasso.get().load(response.body().getImg()).into(imagemBinding.imageView4);
                    expandirImagem.setView(imagemBinding.getRoot());
                    expandirImagem.setTitle("Imagem do Produto");
                    expandirImagem.setNegativeButton("Fechar", null);
                    expandirImagem.create().show();
                });
            }

            @Override
            public void onFailure(Call<ImagemResponseModel> call, Throwable t) {
                // Tratar o erro aqui
                progressBar.setVisibility(View.GONE);
            }
        });

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
        mainBinding.tableLayout.addView(createDivider());
    }

    private View createDivider() {
        View divider = new View(this);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,  // largura, ocupa todo o espaço disponível
                2  // altura da linha divisória (em pixels)
        );
        params.setMargins(0, 8, 0, 8);  // adiciona margens (opcional)
        divider.setLayoutParams(params);
        divider.setBackgroundColor(Color.LTGRAY);  // define a cor da linha divisória
        return divider;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}