package benicio.solucoes.cadastropedido.adapter;

import android.app.Activity;
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

import benicio.solucoes.cadastropedido.PedidoVendedorActivity;
import benicio.solucoes.cadastropedido.R;
import benicio.solucoes.cadastropedido.model.UserModel;

public class AdapterUsuarios extends RecyclerView.Adapter<AdapterUsuarios.MyViewHolder>{
    List<UserModel> usuarios;
    Activity c;

    public AdapterUsuarios(List<UserModel> usuarios, Activity c) {
        this.usuarios = usuarios;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.btnExcluir.setVisibility(View.GONE);
        holder.btn_ver_pedidos_vendedores.setVisibility(View.VISIBLE);
        holder.btn_ver_pedidos_creditos.setVisibility(View.VISIBLE);
        UserModel user = usuarios.get(position);

        holder.itemView.getRootView().setClickable(false);
        holder.btn_ver_pedidos_vendedores.setOnClickListener( view -> {
            Intent t = new Intent(c, PedidoVendedorActivity.class);
            t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            t.putExtra("idUsuario", user.getId());
            c.startActivity(t);
        });

        holder.btn_ver_pedidos_creditos.setOnClickListener(v -> {
            Intent t = new Intent(c, PedidoVendedorActivity.class);
            t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            t.putExtra("idUsuario", user.getId());
            t.putExtra("credito", true);
            c.startActivity(t);
        });
        holder.info.setText(
                user.toString()
        );
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView info;
        ImageButton btnExcluir;

        Button btn_ver_pedidos_vendedores, btn_ver_pedidos_creditos;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            info = itemView.findViewById(R.id.text_info_generic);
            btnExcluir = itemView.findViewById(R.id.btn_remover_item);
            btn_ver_pedidos_vendedores = itemView.findViewById(R.id.btn_ver_pedidos_vendedores);
            btn_ver_pedidos_creditos = itemView.findViewById(R.id.btn_ver_pedidos_creditos);
        }
    }
}
