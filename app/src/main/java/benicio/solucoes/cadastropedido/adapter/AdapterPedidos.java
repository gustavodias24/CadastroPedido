package benicio.solucoes.cadastropedido.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import benicio.solucoes.cadastropedido.R;
import benicio.solucoes.cadastropedido.model.PedidoModel;

public class AdapterPedidos extends RecyclerView.Adapter<AdapterPedidos.MyViewHolde> {
    List<PedidoModel> listaPedido;
    Activity a;

    public AdapterPedidos(List<PedidoModel> listaPedido, Activity a) {
        this.listaPedido = listaPedido;
        this.a = a;
    }

    @NonNull
    @Override
    public MyViewHolde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolde(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolde holder, int position) {
        PedidoModel pedidoModel = listaPedido.get(position);
        holder.btnExcluir.setVisibility(View.GONE);
        holder.info.setText(pedidoModel.toString());

    }

    @Override
    public int getItemCount() {
        return listaPedido.size();
    }

    public static class  MyViewHolde extends RecyclerView.ViewHolder {

        TextView info;
        ImageButton btnExcluir;
        public MyViewHolde(@NonNull View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.text_info_generic);
            btnExcluir = itemView.findViewById(R.id.btn_remover_item);
        }
    }
}
