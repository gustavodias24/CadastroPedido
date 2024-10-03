package benicio.solucoes.cadastropedido.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import benicio.solucoes.cadastropedido.EditarPedidoActivity;
import benicio.solucoes.cadastropedido.PedidoVendedorActivity;
import benicio.solucoes.cadastropedido.R;
import benicio.solucoes.cadastropedido.databinding.LayoutAlterarStatusBinding;
import benicio.solucoes.cadastropedido.model.PedidoModel;
import benicio.solucoes.cadastropedido.model.ResponseModel;
import benicio.solucoes.cadastropedido.service.ApiServices;
import benicio.solucoes.cadastropedido.util.RetrofitApiApp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterPedidos extends RecyclerView.Adapter<AdapterPedidos.MyViewHolde> {

    private final ApiServices apiServices = RetrofitApiApp.criarService(RetrofitApiApp.criarRetrofit());
    List<PedidoModel> listaPedido;
    Activity a;

    boolean isAdm = false;

    Dialog d;
    LinearLayout dialogCarregando;

    public AdapterPedidos(List<PedidoModel> listaPedido, Activity a) {
        this.listaPedido = listaPedido;
        this.a = a;
    }

    public AdapterPedidos(List<PedidoModel> listaPedido, Activity a, boolean isAdm, LinearLayout dialogCarregando) {
        this.listaPedido = listaPedido;
        this.a = a;
        this.isAdm = isAdm;
        this.dialogCarregando = dialogCarregando;
    }

    @NonNull
    @Override
    public MyViewHolde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolde(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolde holder, int position) {
        PedidoModel pedidoModel = listaPedido.get(position);
        holder.btnExcluir.setVisibility(View.GONE);
        holder.info.setText(pedidoModel.toInformacao(isAdm));


        if ( pedidoModel.getStatus().toLowerCase().contains("inadiplente")){
            AlertDialog.Builder builderInadiplente = new AlertDialog.Builder(a);
            builderInadiplente.setTitle("ATENÇÃO!");
            builderInadiplente.setMessage("O cliente " + pedidoModel.getNomeComprador() + " do estabelecimento " + pedidoModel.getNomeEstabelecimento() + " e do CNPJ " + pedidoModel.getCnpj() + " está INADIPLENTE!");
            builderInadiplente.setPositiveButton("ok", null);
            builderInadiplente.create().show();
        }

        if ( isAdm ){
            holder.btn_editar_status_pedidos.setVisibility(View.VISIBLE);
            holder.btn_editar_pedido_completo.setVisibility(View.VISIBLE);

            holder.btn_editar_pedido_completo.setOnClickListener(view -> {
                Intent i = new Intent(a, EditarPedidoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("idPedido", pedidoModel.getId());
                a.startActivity(i);
            });

            holder.btn_editar_status_pedidos.setOnClickListener(view -> {

                AlertDialog.Builder b = new AlertDialog.Builder(a);

                b.setTitle("Atualizar Status");
                b.setNegativeButton("Fechar", (dialog, i) -> d.dismiss());
                LayoutAlterarStatusBinding alterarStatusBinding = LayoutAlterarStatusBinding.inflate(a.getLayoutInflater());

                switch (pedidoModel.getStatus()){
                    case "pendente":
                        alterarStatusBinding.radioPendente.setChecked(true);
                        break;
                    case "em processo":
                        alterarStatusBinding.radioEmProcesso.setChecked(true);
                        break;
                    case "completo":
                        alterarStatusBinding.radioCompleto.setChecked(true);
                        break;
                    case "pagamento pendente":
                        alterarStatusBinding.radioPagamentoPendente.setChecked(true);
                        break;
                    case "cancelado":
                        alterarStatusBinding.radioCancelado.setChecked(true);
                        break;
                    case "fechado":
                        alterarStatusBinding.radioFechado.setChecked(true);
                        break;
                }

                alterarStatusBinding.radioPendente.setOnClickListener(view1 -> {
                    pedidoModel.setStatus("pendente");
                    apiServices.atualizarPedidoProduto(pedidoModel).enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {

                        }
                    });
                    this.notifyDataSetChanged();
                });

                alterarStatusBinding.radioEmProcesso.setOnClickListener(view1 -> {
                    pedidoModel.setStatus("em processo");
                    apiServices.atualizarPedidoProduto(pedidoModel).enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {

                        }
                    });
                    this.notifyDataSetChanged();
                });

                alterarStatusBinding.radioCompleto.setOnClickListener(view1 -> {
                    pedidoModel.setStatus("completo");
                    apiServices.atualizarPedidoProduto(pedidoModel).enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {

                        }
                    });
                    this.notifyDataSetChanged();
                });

                alterarStatusBinding.radioPagamentoPendente.setOnClickListener(view1 -> {
                    pedidoModel.setStatus("pagamento pendente");
                    apiServices.atualizarPedidoProduto(pedidoModel).enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {

                        }
                    });
                    this.notifyDataSetChanged();
                });

                alterarStatusBinding.radioCancelado.setOnClickListener(view1 -> {
                    pedidoModel.setStatus("cancelado");
                    apiServices.atualizarPedidoProduto(pedidoModel).enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {

                        }
                    });
                    this.notifyDataSetChanged();
                });

                alterarStatusBinding.radioFechado.setOnClickListener(view1 -> {
                    pedidoModel.setStatus("fechado");
                    apiServices.atualizarPedidoProduto(pedidoModel).enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {

                        }
                    });
                    this.notifyDataSetChanged();
                });

                b.setView(alterarStatusBinding.getRoot());
                d = b.create();
                d.show();
            });
        }

    }

    @Override
    public int getItemCount() {
        return listaPedido.size();
    }

    public static class  MyViewHolde extends RecyclerView.ViewHolder {

        TextView info;
        ImageButton btnExcluir;
        Button btn_editar_status_pedidos;
        Button btn_editar_pedido_completo;
        public MyViewHolde(@NonNull View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.text_info_generic);
            btnExcluir = itemView.findViewById(R.id.btn_remover_item);
            btn_editar_status_pedidos = itemView.findViewById(R.id.btn_editar_status_pedidos);
            btn_editar_pedido_completo = itemView.findViewById(R.id.btn_editar_pedido_completo);
        }
    }
}
