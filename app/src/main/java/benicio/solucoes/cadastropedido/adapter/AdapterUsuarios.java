package benicio.solucoes.cadastropedido.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import benicio.solucoes.cadastropedido.PedidoVendedorActivity;
import benicio.solucoes.cadastropedido.R;
import benicio.solucoes.cadastropedido.model.ResponseModel;
import benicio.solucoes.cadastropedido.model.UserModel;
import benicio.solucoes.cadastropedido.service.ApiServices;
import benicio.solucoes.cadastropedido.util.RetrofitApiApp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterUsuarios extends RecyclerView.Adapter<AdapterUsuarios.MyViewHolder> {
    private final ApiServices apiServices = RetrofitApiApp.criarService(RetrofitApiApp.criarRetrofit());
    List<UserModel> usuarios;
    Activity c;

    Dialog dialodRm;

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

        UserModel user = usuarios.get(position);


        holder.btnExcluir.setVisibility(View.GONE);
        holder.btn_ver_pedidos_vendedores.setVisibility(View.VISIBLE);
        holder.btn_ver_pedidos_creditos.setVisibility(View.VISIBLE);
        holder.btn_remover_permanente.setVisibility(View.VISIBLE);


        holder.btn_remover_permanente.setOnClickListener(v -> {

            AlertDialog.Builder b = new AlertDialog.Builder(c);
            b.setTitle("Atenção!");
            b.setMessage("Remover esse Usuário Permantemente?");
            b.setPositiveButton("Sim", (d, i) -> {
                apiServices.deleteUsuario(new UserModel(user.getEmail())).enqueue(new Callback<ResponseModel>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(c, "Usuário Removido!", Toast.LENGTH_SHORT).show();
                            usuarios.remove(position);
                            notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {

                    }
                });
            });
            b.setNegativeButton("Não", null);
            dialodRm = b.create();
            dialodRm.show();

        });


        holder.itemView.getRootView().setClickable(false);

        holder.btn_ver_pedidos_vendedores.setOnClickListener(view -> {
            Intent t = new Intent(c, PedidoVendedorActivity.class);
            t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            t.putExtra("email", user.getEmail());
            c.startActivity(t);
        });

        holder.btn_ver_pedidos_creditos.setOnClickListener(v -> {
            Intent t = new Intent(c, PedidoVendedorActivity.class);
            t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            t.putExtra("email", user.getEmail());
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

        Button btn_ver_pedidos_vendedores, btn_ver_pedidos_creditos, btn_remover_permanente;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            info = itemView.findViewById(R.id.text_info_generic);
            btnExcluir = itemView.findViewById(R.id.btn_remover_item);
            btn_ver_pedidos_vendedores = itemView.findViewById(R.id.btn_ver_pedidos_vendedores);
            btn_ver_pedidos_creditos = itemView.findViewById(R.id.btn_ver_pedidos_creditos);
            btn_remover_permanente = itemView.findViewById(R.id.btn_remover_permanente);
        }
    }
}
