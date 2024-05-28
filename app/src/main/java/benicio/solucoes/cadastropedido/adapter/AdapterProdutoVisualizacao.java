package benicio.solucoes.cadastropedido.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import benicio.solucoes.cadastropedido.R;
import benicio.solucoes.cadastropedido.model.ProdutoModel;

public class AdapterProdutoVisualizacao extends RecyclerView.Adapter<AdapterProdutoVisualizacao.MyViewHolder> {

    List<ProdutoModel> lista;
    Activity c;

    public AdapterProdutoVisualizacao(List<ProdutoModel> lista, Activity c) {
        this.lista = lista;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_dist, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.info.setText(lista.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public final class MyViewHolder extends RecyclerView.ViewHolder {

        TextView info;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            info = itemView.findViewById(R.id.indodistgenerica);
        }
    }
}
