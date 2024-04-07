package benicio.solucoes.cadastropedido;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import benicio.solucoes.cadastropedido.adapter.AdapterProduto;
import benicio.solucoes.cadastropedido.databinding.ActivityProdutosBinding;
import benicio.solucoes.cadastropedido.databinding.LoadingLayoutBinding;
import benicio.solucoes.cadastropedido.model.ClienteModel;
import benicio.solucoes.cadastropedido.model.DistribuidorModel;
import benicio.solucoes.cadastropedido.model.ItemCompra;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ProdutoModel;
import benicio.solucoes.cadastropedido.service.ProdutosServices;
import benicio.solucoes.cadastropedido.util.MathUtils;
import benicio.solucoes.cadastropedido.util.ProdutosUtils;
import benicio.solucoes.cadastropedido.util.RetrofitUitl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProdutosActivity extends AppCompatActivity {
    private boolean jaTemRegra = false;
    //    private Dialog loadingDialog;
    private int valorMinimo = 0;
    private RecyclerView r;
    private ActivityProdutosBinding mainBinding;
    private Bundle b;
    private PedidoModel pedidoModel;

    private List<ProdutoModel> listaProdutos;

    private List<ItemCompra> listaCompra = new ArrayList<>();
    private AdapterProduto adapterProdutos;
    private List<String> listaNomeProdutos = new ArrayList<>();
    private List<String> listaNomeSKU = new ArrayList<>();

    private float estoqueAtual = 0.0f;
    private Retrofit retrofit;
    private ProdutosServices services;

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityProdutosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

//        configurarLoadingDialog();

        retrofit = RetrofitUitl.criarRetrofit();
        services = RetrofitUitl.criarService(retrofit);

        getSupportActionBar().setTitle("Produtos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configurarRecycler();

//        loadingDialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                b = getIntent().getExtras();
                Gson gson = new Gson();
                Type type = new TypeToken<PedidoModel>() {
                }.getType();
                pedidoModel = gson.fromJson(b.getString("dadosPedido", ""), type);

                listaProdutos = ProdutosUtils.returnProdutos(ProdutosActivity.this);
                for (ProdutoModel produtoModel : listaProdutos) {
                    listaNomeProdutos.add(produtoModel.getNome() + " -F- " + produtoModel.getFornecedor());
                    listaNomeSKU.add(produtoModel.getSku());
                }

                String[] sugestoes = listaNomeProdutos.toArray(new String[0]);
                String[] sugestoesSKU = listaNomeSKU.toArray(new String[0]);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ProdutosActivity.this, R.layout.custom_dropdown_item, R.id.textoProdutos, sugestoes);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sugestoes);
                ArrayAdapter<String> adapterSKU = new ArrayAdapter<>(ProdutosActivity.this, android.R.layout.simple_dropdown_item_1line, sugestoesSKU);

                runOnUiThread(() -> {
                    mainBinding.edtNomeProduto.setAdapter(adapter);
                    mainBinding.edtSKU.setAdapter(adapterSKU);
                });

                mainBinding.edtNomeProduto.setOnClickListener(view -> mainBinding.edtNomeProduto.showDropDown());
                mainBinding.edtSKU.setOnClickListener(view -> mainBinding.edtSKU.showDropDown());

                mainBinding.edtSKU.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedFrase = (String) parent.getItemAtPosition(position);
                    mainBinding.edtSKU.setText(selectedFrase);

                    for (ProdutoModel produtoModel : listaProdutos) {
                        if (produtoModel.getSku().equals(selectedFrase)) {
                            configurarRegra(produtoModel.getFornecedor());
                            mainBinding.edtNomeProduto.setText(produtoModel.getNome());
                            mainBinding.edtSKU.setText(produtoModel.getSku());
                            mainBinding.edtValor.setText(formatarMoeda(produtoModel.getPreco()));
                            mainBinding.textEstoque.setText("Esse produto tem " + produtoModel.getEstoque() + " no estoque.");
                            estoqueAtual = produtoModel.getEstoque();
                            mainBinding.textEstoque.setVisibility(View.VISIBLE);
                            break;
                        }
                    }

                    Toast.makeText(getApplicationContext(), selectedFrase + " selecionado!", Toast.LENGTH_SHORT).show();
                });

                mainBinding.edtNomeProduto.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedFrase = (String) parent.getItemAtPosition(position);
                    selectedFrase = selectedFrase.split(" -F- ")[0];
                    mainBinding.edtNomeProduto.setText(selectedFrase);

                    for (ProdutoModel produtoModel : listaProdutos) {
                        if (produtoModel.getNome().equals(selectedFrase)) {
                            configurarRegra(produtoModel.getFornecedor());
                            mainBinding.edtSKU.setText(produtoModel.getSku());
                            mainBinding.edtValor.setText(formatarMoeda(produtoModel.getPreco()));
                            mainBinding.textEstoque.setText("Esse produto tem " + produtoModel.getEstoque() + " no estoque.");
                            estoqueAtual = produtoModel.getEstoque();
                            mainBinding.textEstoque.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), selectedFrase + " selecionado!", Toast.LENGTH_SHORT).show();
                });
                runOnUiThread(() -> mainBinding.layoutCarregando.setVisibility(View.GONE));
            }
        }.start();


        mainBinding.btnCalcular.setOnClickListener(view -> calcularValorProduto());
        mainBinding.btnAddProdutos.setOnClickListener(view -> {

            calcularValorProduto();

            if (estoqueAtual > 0) {

                float soma = 0.f;
                String valorString = mainBinding.edtValorTotalDesconto.getText().toString().replace(",", "").replace("R$", "").replace(" ", "").replace(" ", "");

                try {
                    soma = Float.parseFloat(valorString);
                } catch (Exception ignored) {
                }

                if (soma > 0) {
                    String valorTotalString = mainBinding.edtValorTotal.getText().toString();

                    listaCompra.add(new ItemCompra(mainBinding.edtNomeProduto.getText().toString(), mainBinding.edtSKU.getText().toString(), mainBinding.edtValor.getText().toString(), mainBinding.edtQTD.getText().toString(), valorTotalString, mainBinding.edtDesconto.getText().toString(), mainBinding.edtValorTotalDesconto.getText().toString()));

                    adapterProdutos.notifyDataSetChanged();

                    Double valorAtual = MathUtils.converterParaDouble(mainBinding.textValorTotal.getText().toString().split(" ")[2]);

                    valorAtual += MathUtils.converterParaDouble(mainBinding.edtValorTotalDesconto.getText().toString());

                    mainBinding.textValorTotal.setText("Total Compra: " + MathUtils.formatarMoeda(valorAtual));

                    mainBinding.edtNomeProduto.setText("");
                    mainBinding.edtSKU.setText("");
                    mainBinding.edtValor.setText("");
                    mainBinding.edtQTD.setText("");
                    mainBinding.edtValorTotal.setText("");
                    mainBinding.edtDesconto.setText("");
                    mainBinding.edtValorTotalDesconto.setText("");
                }

            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("AVISO!");
                b.setMessage("produto sem estoque!");
                b.setPositiveButton("ok", null);
                b.create().show();
            }
        });

        mainBinding.btnEnviarEmail.setOnClickListener(view -> {
            List<ItemCompra> listaAntigaDeProdutos = pedidoModel.getProdutos();
            pedidoModel.setTotalCompra(mainBinding.textValorTotal.getText().toString());
            listaAntigaDeProdutos.addAll(listaCompra);
            pedidoModel.setProdutos(listaAntigaDeProdutos);
            Intent i = new Intent(this, EnviarEmailActivity.class);
            i.putExtra("dados", new Gson().toJson(pedidoModel));

            double somaCompra = 0;

            for (ItemCompra item : pedidoModel.getProdutos()) {
                somaCompra += Double.parseDouble(item.getValorTotalFinal().replace(",", "").replace("R$", "").replace(" ", "").replace(" ", ""));
            }

//            if (somaCompra >= valorMinimo) {
            if (true) {
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Valor Mínimo não Alcançado!", Toast.LENGTH_LONG).show();
            }

        });
    }


    private void configurarRegra(String nomeDist) {
        if (!jaTemRegra) {
            mainBinding.progressBarRegraDistri.setVisibility(View.VISIBLE);
            StringBuilder regrasDist = new StringBuilder();
            regrasDist.append("Regras do Distribuidor").append("\n");

            services.pegarDistribuidores().enqueue(new Callback<List<DistribuidorModel>>() {
                @Override
                public void onResponse(Call<List<DistribuidorModel>> call, Response<List<DistribuidorModel>> response) {
                    mainBinding.progressBarRegraDistri.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        for (DistribuidorModel distribuidorModel : response.body()) {
                            if (nomeDist.equals(distribuidorModel.getEmpresa())) {

                                try {
                                    valorMinimo = Integer.parseInt(distribuidorModel.getValorMinimo());
                                } catch (Exception ignored) {

                                }

                                regrasDist.append("Aceita vouhcer de desconto?").append(distribuidorModel.getAceitaVouche()).append("\n");
                                regrasDist.append("Valor do pedido mínimo: R$").append(distribuidorModel.getValorMinimo()).append("\n");
                                regrasDist.append("Tipo de Frete: ").append(distribuidorModel.getFrete()).append("\n");
                                regrasDist.append("Meio de Pagamento aceitos: ").append(distribuidorModel.getMeioPagamento()).append("\n");
                                regrasDist.append("Detalhes sobre prazo de entrega: ").append(distribuidorModel.getPrazo()).append("\n");

                                jaTemRegra = true;

                                mainBinding.textRegrasDoDistribuidor.setText(regrasDist.toString());
                                mainBinding.textRegrasDoDistribuidor.setVisibility(View.VISIBLE);

                                break;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<DistribuidorModel>> call, Throwable t) {
                    mainBinding.progressBarRegraDistri.setVisibility(View.GONE);
                }
            });
        }


    }

    public static String formatarMoeda(String valor) {
        // Remove o prefixo "R$ " e substitui vírgulas por pontos para garantir formato numérico correto
        String valorNumerico = valor.replace("R$ ", "").replace(",", ".");

        // Converte a string para um valor double
        double valorDouble = Double.parseDouble(valorNumerico);

        // Cria um formato para moeda brasileira
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
        DecimalFormat formatoMoeda = new DecimalFormat("¤ ###,##0.00", simbolos);

        // Formata o valor para moeda brasileira
        return formatoMoeda.format(valorDouble);
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
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void calcularValorProduto() {
        Double valorTotalPorItem = MathUtils.converterParaDouble(mainBinding.edtValor.getText().toString()) * MathUtils.converterParaDouble(mainBinding.edtQTD.getText().toString());

        mainBinding.edtValorTotal.setText(MathUtils.formatarMoeda(valorTotalPorItem));


        Double descontoPorcentagem = (Double.parseDouble(mainBinding.edtDesconto.getText().toString().isEmpty() ? "0" : mainBinding.edtDesconto.getText().toString())) / 100;

        Double desconto = valorTotalPorItem * descontoPorcentagem;


        mainBinding.edtValorTotalDesconto.setText(MathUtils.formatarMoeda(valorTotalPorItem - desconto));

    }
}