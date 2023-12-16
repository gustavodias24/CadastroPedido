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
import benicio.solucoes.cadastropedido.model.ProdutoModel;

public class AdapterViewProduto extends RecyclerView.Adapter<AdapterViewProduto.MyViewHolder> {
    List<ProdutoModel> listaProdutos;
    Activity a;

    public AdapterViewProduto(List<ProdutoModel> listaProdutos, Activity a) {
        this.listaProdutos = listaProdutos;
        this.a = a;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProdutoModel produto = listaProdutos.get(position);

        holder.btnExcluir.setVisibility(View.GONE);
        holder.info.setText(produto.toString());

    }

    @Override
    public int getItemCount() {
        return listaProdutos.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView info;
        ImageButton btnExcluir;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.text_info_generic);
            btnExcluir = itemView.findViewById(R.id.btn_remover_item);
        }
    }
}
