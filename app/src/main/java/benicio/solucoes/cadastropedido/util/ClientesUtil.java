package benicio.solucoes.cadastropedido.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.model.ClienteModel;

public class ClientesUtil {
    public static final String name_prefs = "clientes_prefs";
    public static final String name_clientes = "name_clientes";


    public static void saveClientes(Context c, List<ClienteModel> lista){
        SharedPreferences sharedPreferences = c.getSharedPreferences(name_prefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name_clientes, new Gson().toJson(lista)).apply();
    }

    public static List<ClienteModel> returnClientes(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences(name_prefs, Context.MODE_PRIVATE);

        String clientesString = sharedPreferences.getString(name_clientes, "");
        Type type = new TypeToken<List<ClienteModel>>(){}.getType();
        Gson gson = new Gson();

        List<ClienteModel> lista = gson.fromJson(clientesString, type);

        if ( lista == null){
            return new ArrayList<>();
        }

        return lista;

    }
}
