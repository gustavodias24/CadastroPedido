package benicio.solucoes.cadastropedido.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

import benicio.solucoes.cadastropedido.model.CreditoModel;
import benicio.solucoes.cadastropedido.model.ItemCompra;
import benicio.solucoes.cadastropedido.model.PedidoModel;


public class CSVGenerator {
    public static void gerarPedidoCSV(Activity activity, List<PedidoModel> pedidos) {
        try {

            Toast.makeText(activity, "Aguarde.", Toast.LENGTH_SHORT).show();
            Toast.makeText(activity, "Gerando...", Toast.LENGTH_SHORT).show();


            // Crie um diretório para armazenar o arquivo CSV
            File directory = new File(activity.getExternalFilesDir(null), "redCloudRelatorios");
            if (!directory.exists()) {
                directory.mkdirs();
            }


            // Crie o arquivo CSV dentro do diretório
            File file = new File(directory, "relatório_" + UUID.randomUUID().toString() + ".csv");

            FileWriter fileWriter = new FileWriter(file, Charset.forName("ISO-8859-1"));
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Escrever o cabeçalho do CSV
            String[] header = {"Loja Vendedora", "Nome Cliente", "CNPJ", "email", "ID Pedido", "Método de Pagamento", "% Desconto", "Sub total", "Total Desconto", "Total do Pedido"};

            csvWriter.writeNext(header);

            new Thread() {
                @Override
                public void run() {
                    super.run();

                    // Escrever cada objeto IndicacaoModel como uma linha no CSV
                    for (PedidoModel pedido : pedidos) {
                        for (ItemCompra item : pedido.getProdutos()) {
                            String[] linha = {
                                    pedido.getLojaVendedor(),
                                    pedido.getNomeComprador(),
                                    pedido.getCnpj(),
                                    pedido.getEmail(),
                                    pedido.getId(),
                                    pedido.getFormaPagamento(),
                                    item.getDesconto(),
                                    item.getValorTotalPorItem(),
                                    item.getValorTotalFinal(),
                                    pedido.getTotalCompra().replace("Total Compra:", "")
                            };
                            csvWriter.writeNext(linha);
                        }
                    }

                    // Fechar o CSVWriter
                    try {
                        csvWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // Compartilhar o arquivo CSV via WhatsApp
                    compartilharCSVViaWhatsApp(activity, file);
                }
            }.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void gerarPedidoADMCSV(Activity activity, List<PedidoModel> pedidos) {
        try {

            Toast.makeText(activity, "Aguarde.", Toast.LENGTH_SHORT).show();
            Toast.makeText(activity, "Gerando...", Toast.LENGTH_SHORT).show();

            // Crie um diretório para armazenar o arquivo CSV
            File directory = new File(activity.getExternalFilesDir(null), "redCloudRelatorios");
            if (!directory.exists()) {
                directory.mkdirs();
            }


            // Crie o arquivo CSV dentro do diretório
            File file = new File(directory, "relatório_" + UUID.randomUUID().toString() + ".csv");

            FileWriter fileWriter = new FileWriter(file, Charset.forName("ISO-8859-1"));
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Escrever o cabeçalho do CSV
            String[] header = {"ID Agente", "Loja Vendedora", "Nome Cliente", "CNPJ", "email", "Telefone", "Data do Pedido", "ID Pedido", "Status Pedido", "Método de Pagamento", "Endereço", "Cep", "% Desconto", "Sub total", "Total Desconto", "Total (valor) do Pedido"};

            csvWriter.writeNext(header);

            new Thread() {
                @Override
                public void run() {
                    super.run();

                    // Escrever cada objeto IndicacaoModel como uma linha no CSV
                    for (PedidoModel pedido : pedidos) {


                        for (ItemCompra item : pedido.getProdutos()) {
                            String[] linha = {
                                    pedido.getIdAgente(),
                                    pedido.getLojaVendedor(),
                                    pedido.getNomeComprador(),
                                    pedido.getCnpj(),
                                    pedido.getEmail(),
                                    pedido.getTele(),
                                    pedido.getData(),
                                    pedido.getId(),
                                    pedido.getStatus(),
                                    pedido.getFormaPagamento(),
                                    pedido.getEnderecoCompleto(),
                                    pedido.getCep(),
                                    item.getDesconto(),
                                    item.getValorTotalPorItem(),
                                    item.getValorTotalFinal(),
                                    pedido.getTotalCompra().replace("Total Compra:", "")
                            };
                            csvWriter.writeNext(linha);
                        }
                    }

                    // Fechar o CSVWriter
                    try {
                        csvWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // Compartilhar o arquivo CSV via WhatsApp
                    compartilharCSVViaWhatsApp(activity, file);
                }
            }.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void gerarCreditoADMCSV(Activity activity, List<CreditoModel> creditos) {
        try {

            Toast.makeText(activity, "Aguarde.", Toast.LENGTH_SHORT).show();
            Toast.makeText(activity, "Gerando...", Toast.LENGTH_SHORT).show();

            // Crie um diretório para armazenar o arquivo CSV
            File directory = new File(activity.getExternalFilesDir(null), "redCloudRelatorios");
            if (!directory.exists()) {
                directory.mkdirs();
            }


            // Crie o arquivo CSV dentro do diretório
            File file = new File(directory, "relatório_" + UUID.randomUUID().toString() + ".csv");

            FileWriter fileWriter = new FileWriter(file, Charset.forName("ISO-8859-1"));
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Escrever o cabeçalho do CSV
            String[] header = {
                    "ID Agent Solicitante (field Officer)",
                    "ID Solicitação de Crédito",
                    "Data da Solicitação",
                    "Loja Vendedora",
                    "Nome Cliente",
                    "CNPJ",
                    "email",
                    "Prazo Solicitado",
                    "Valor Solicitado",
                    "Status da solicitação"
            };

            csvWriter.writeNext(header);

            new Thread() {
                @Override
                public void run() {
                    super.run();

                    // Escrever cada objeto IndicacaoModel como uma linha no CSV
                    for (CreditoModel credito : creditos) {

                        String[] linha = {
                                credito.getIdAgent(),
                                credito.getId(),
                                credito.getData(),
                                credito.getDistribuidor(),
                                credito.getNome(),
                                credito.getCnpj(),
                                credito.getEmail(),
                                credito.getPrazoSocilitado(),
                                credito.getValorSolicitado(),
                                credito.getStatus()

                        };
                        csvWriter.writeNext(linha);
                    }

                    // Fechar o CSVWriter
                    try {
                        csvWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // Compartilhar o arquivo CSV via WhatsApp
                    compartilharCSVViaWhatsApp(activity, file);
                }
            }.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compartilharCSVViaWhatsApp(Activity activity, File file) {
        Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(intent);
    }
}
