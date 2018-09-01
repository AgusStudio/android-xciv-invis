package xciv.invis.SqlLiteConfig;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Betha on 8/22/2018.
 */

public class SqlLiteInvis extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "invis_myInventory";
    private static final int DATABASE_VERSION = 1;

    public SqlLiteInvis(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "CREATE TABLE config_tbl (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, api_url TEXT, metode TEXT );"
        );
        db.execSQL("INSERT INTO config_tbl (id,api_url,metode) VALUES ('1','','Scanner')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS config");
    }
}
