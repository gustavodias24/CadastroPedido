package benicio.solucoes.cadastropedido.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import benicio.solucoes.cadastropedido.R;
import benicio.solucoes.cadastropedido.model.ItemCompra;
import benicio.solucoes.cadastropedido.util.MathUtils;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    List<ItemCompra> lista;
    Activity a;

    TextView textTotalCompra;


    public AdapterProduto(List<ItemCompra> lista, Activity a, TextView textTotalCompra) {
        this.lista = lista;
        this.a = a;
        this.textTotalCompra = textTotalCompra;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false));
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemCompra item = lista.get(position);
        holder.itemView.getRootView().setClickable(false);
        
        holder.info.setText(
                item.toString()
        );
        
        holder.btnExcluir.setOnClickListener( view -> {
            lista.remove(position);
            Double valorAtual = MathUtils.converterParaDouble(textTotalCompra.getText().toString().split(" ")[2]);
            Double novoValor = valorAtual - MathUtils.converterParaDouble(item.getValorTotalFinal());

            textTotalCompra.setText("Total Compra: "+ MathUtils.formatarMoeda(novoValor));

            Toast.makeText(a, "Item removido.", Toast.LENGTH_SHORT).show();
            this.notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView info;
        ImageButton btnExcluir;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.text_info_generic);
            btnExcluir = itemView.findViewById(R.id.btn_remover_item);
        }
    }
}
