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
import benicio.solucoes.cadastropedido.model.DistribuidorModel;

public class AdapterDist extends RecyclerView.Adapter<AdapterDist.MyViewHolder> {

    List<DistribuidorModel> lista;
    Activity a;

    public AdapterDist(List<DistribuidorModel> lista, Activity a) {
        this.lista = lista;
        this.a = a;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DistribuidorModel dist = lista.get(position);

        holder.indodistgenerica.setText(dist.toString());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView indodistgenerica;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            indodistgenerica = itemView.findViewById(R.id.indodistgenerica);
        }
    }
}
