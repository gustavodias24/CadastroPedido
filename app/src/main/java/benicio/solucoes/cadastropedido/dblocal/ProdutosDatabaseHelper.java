package benicio.solucoes.cadastropedido.dblocal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ProdutosDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "produtos.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CREATE =
            "CREATE TABLE produto (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT, " +
                    "sku TEXT, " +
                    "preco TEXT, " +
                    "fornecedor TEXT, " +
                    "estoque REAL);";

    public ProdutosDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuario");
        onCreate(db);
    }
}
