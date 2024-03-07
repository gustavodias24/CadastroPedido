package benicio.solucoes.cadastropedido.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import benicio.solucoes.cadastropedido.EditarPedidoActivity;
import benicio.solucoes.cadastropedido.PedidoVendedorActivity;
import benicio.solucoes.cadastropedido.R;
import benicio.solucoes.cadastropedido.databinding.LayoutAlterarStatusBinding;
import benicio.solucoes.cadastropedido.model.PedidoModel;

public class AdapterPedidos extends RecyclerView.Adapter<AdapterPedidos.MyViewHolde> {
    List<PedidoModel> listaPedido;
    Activity a;

    boolean isAdm = false;

    Dialog d;
    Dialog dialogCarregando;

    public AdapterPedidos(List<PedidoModel> listaPedido, Activity a) {
        this.listaPedido = listaPedido;
        this.a = a;
    }

    public AdapterPedidos(List<PedidoModel> listaPedido, Activity a, boolean isAdm, Dialog dialogCarregando) {
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
                    case 0:
                        alterarStatusBinding.radioPendente.setChecked(true);
                        break;
                    case 1:
                        alterarStatusBinding.radioEmProcesso.setChecked(true);
                        break;
                    case 2:
                        alterarStatusBinding.radioCompleto.setChecked(true);
                        break;
                    case 3:
                        alterarStatusBinding.radioPagamentoPendente.setChecked(true);
                        break;
                    case 4:
                        alterarStatusBinding.radioCancelado.setChecked(true);
                        break;
                    case 5:
                        alterarStatusBinding.radioFechado.setChecked(true);
                        break;
                }

                alterarStatusBinding.radioPendente.setOnClickListener(view1 -> {
                    dialogCarregando.show();
                    pedidoModel.setStatus(0);
                    PedidoVendedorActivity.refPedidos.child(pedidoModel.getId()).setValue(pedidoModel).addOnCompleteListener(task -> dialogCarregando.dismiss());
                    this.notifyDataSetChanged();
                });

                alterarStatusBinding.radioEmProcesso.setOnClickListener(view1 -> {
                    dialogCarregando.show();
                    pedidoModel.setStatus(1);
                    PedidoVendedorActivity.refPedidos.child(pedidoModel.getId()).setValue(pedidoModel).addOnCompleteListener(task -> dialogCarregando.dismiss());
                    this.notifyDataSetChanged();
                });

                alterarStatusBinding.radioCompleto.setOnClickListener(view1 -> {
                    dialogCarregando.show();
                    pedidoModel.setStatus(2);
                    PedidoVendedorActivity.refPedidos.child(pedidoModel.getId()).setValue(pedidoModel).addOnCompleteListener(task -> dialogCarregando.dismiss());
                    this.notifyDataSetChanged();
                });

                alterarStatusBinding.radioPagamentoPendente.setOnClickListener(view1 -> {
                    dialogCarregando.show();
                    pedidoModel.setStatus(3);
                    PedidoVendedorActivity.refPedidos.child(pedidoModel.getId()).setValue(pedidoModel).addOnCompleteListener(task -> dialogCarregando.dismiss());
                    this.notifyDataSetChanged();
                });

                alterarStatusBinding.radioCancelado.setOnClickListener(view1 -> {
                    dialogCarregando.show();
                    pedidoModel.setStatus(4);
                    PedidoVendedorActivity.refPedidos.child(pedidoModel.getId()).setValue(pedidoModel).addOnCompleteListener(task -> dialogCarregando.dismiss());
                    this.notifyDataSetChanged();
                });

                alterarStatusBinding.radioFechado.setOnClickListener(view1 -> {
                    dialogCarregando.show();
                    pedidoModel.setStatus(5);
                    PedidoVendedorActivity.refPedidos.child(pedidoModel.getId()).setValue(pedidoModel).addOnCompleteListener(task -> dialogCarregando.dismiss());
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
