package benicio.solucoes.cadastropedido.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import benicio.solucoes.cadastropedido.R;
import benicio.solucoes.cadastropedido.model.CreditoModel;

public class AdapterCredito extends RecyclerView.Adapter<AdapterCredito.MyViewHolder> {

    List<CreditoModel> lista;
    Activity a;

    public AdapterCredito(List<CreditoModel> lista, Activity a) {
        this.lista = lista;
        this.a = a;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dist, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        CreditoModel pedidoCredito = lista.get(position);

        if ( pedidoCredito.getStatus().toLowerCase().contains("inadiplente") ){
            AlertDialog.Builder b = new AlertDialog.Builder(a);
            b.setTitle("Aviso de Inadiplência");
            b.setMessage(String.format("O Cliente de CNPJ %s está inadiplente!\nId Pedido: %s", pedidoCredito.getCnpj(), pedidoCredito.getId()));
            b.setPositiveButton("ok", null);
            b.create().show();
        }
        String[] informacao = pedidoCredito.toString().split("\n");

        StringBuilder infoBuilder = new StringBuilder();

        for( String info : informacao){
            if ( info.contains("Status:")){

                if (pedidoCredito.getStatus().toLowerCase().contains("solicitado")) {
                    infoBuilder.append("<br>").append("<font color='blue'>").append(info).append("</font>");
                } else if (pedidoCredito.getStatus().toLowerCase().contains("aprovado")) {
                    infoBuilder.append("<br>").append("<font color='green'>").append(info).append("</font>");
                }else if (pedidoCredito.getStatus().toLowerCase().contains("negado")) {
                    infoBuilder.append("<br>").append("<font color='red'>").append(info).append("</font>");
                }

            }else{
                infoBuilder.append("<br>").append(info);
            }
        }



        holder.indodistgenerica.setText(Html.fromHtml(infoBuilder.toString()));
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
