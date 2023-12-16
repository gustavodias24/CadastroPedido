package benicio.solucoes.cadastropedido.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.model.PedidoModel;

public class PedidosUtil {
    public static final String name_prefs = "pedidos_prefs";
    public static final String name_pedidos = "name_pedidos";


    public static void savePedidos(Context c, List<PedidoModel> lista){
        SharedPreferences sharedPreferences = c.getSharedPreferences(name_prefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name_pedidos, new Gson().toJson(lista)).apply();
    }

    public static List<PedidoModel> returnPedidos(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences(name_prefs, Context.MODE_PRIVATE);

        String preditoString = sharedPreferences.getString(name_pedidos, "");
        Type type = new TypeToken<List<PedidoModel>>(){}.getType();
        Gson gson = new Gson();

        List<PedidoModel> lista = gson.fromJson(preditoString, type);

        if ( lista == null){
            return new ArrayList<>();
        }

        return lista;

    }
}
