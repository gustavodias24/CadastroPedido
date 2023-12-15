package benicio.solucoes.cadastropedido.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.model.ProdutoModel;

public class ProdutosUtils {
    public static final String name_prefs = "produtos_prefs";
    public static final String name_produtos = "name_produtos";


    public static void saveProdutos(Context c, List<ProdutoModel> lista){
        SharedPreferences sharedPreferences = c.getSharedPreferences(name_prefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name_produtos, new Gson().toJson(lista)).apply();
    }

    public static List<ProdutoModel> returnProdutos(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences(name_prefs, Context.MODE_PRIVATE);

        String produtosString = sharedPreferences.getString(name_produtos, "");
        Type type = new TypeToken<List<ProdutoModel>>(){}.getType();
        Gson gson = new Gson();

        List<ProdutoModel> lista = gson.fromJson(produtosString, type);

        if ( lista == null){
            return new ArrayList<>();
        }

        return lista;

    }
}
