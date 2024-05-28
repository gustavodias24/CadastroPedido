package benicio.solucoes.cadastropedido.dblocal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.cadastropedido.model.ProdutoModel;

public class ProdutosDAO {

    private SQLiteDatabase db;
    private ProdutosDatabaseHelper dbHelper;

    public ProdutosDAO(Context context) {
        dbHelper = new ProdutosDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long inserirProduto(ProdutoModel usuario) {
        ContentValues values = new ContentValues();
        values.put("nome", usuario.getNome());
        values.put("sku", usuario.getSku());
        values.put("preco", usuario.getPreco());
        values.put("fornecedor", usuario.getFornecedor());
        values.put("estoque", usuario.getEstoque());

        return db.insert("produto", null, values);
    }

    public List<ProdutoModel> listarProdutos() {
        List<ProdutoModel> produtos = new ArrayList<>();
        Cursor cursor = db.query("produto", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ProdutoModel produtoModel = new ProdutoModel();
                produtoModel.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
                produtoModel.setSku(cursor.getString(cursor.getColumnIndexOrThrow("sku")));
                produtoModel.setPreco(cursor.getString(cursor.getColumnIndexOrThrow("preco")));
                produtoModel.setFornecedor(cursor.getString(cursor.getColumnIndexOrThrow("fornecedor")));
                produtoModel.setEstoque(cursor.getFloat(cursor.getColumnIndexOrThrow("estoque")));

                produtos.add(produtoModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return produtos;
    }

    public void limparProdutos() {
        db.delete("produto", null, null);
    }

    public List<ProdutoModel> buscarProduto(String query) {
        List<ProdutoModel> produtos = new ArrayList<>();
        String selection = "nome LIKE ? OR sku LIKE ? OR preco LIKE ? OR fornecedor LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%", "%" + query + "%"};

        Cursor cursor = db.query("produto", null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ProdutoModel produtoModel = new ProdutoModel();
                produtoModel.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
                produtoModel.setSku(cursor.getString(cursor.getColumnIndexOrThrow("sku")));
                produtoModel.setPreco(cursor.getString(cursor.getColumnIndexOrThrow("preco")));
                produtoModel.setFornecedor(cursor.getString(cursor.getColumnIndexOrThrow("fornecedor")));
                produtoModel.setEstoque(cursor.getFloat(cursor.getColumnIndexOrThrow("estoque")));

                produtos.add(produtoModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return produtos;
    }


}
