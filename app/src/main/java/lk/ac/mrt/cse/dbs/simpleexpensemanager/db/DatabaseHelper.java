package lk.ac.mrt.cse.dbs.simpleexpensemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {



    public DatabaseHelper(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryForAccT=
                "CREATE TABLE IF NOT EXISTS "+ DBContract.TABLE_NAME + " ("+
                        DBContract.C_accountNo + " TEXT PRIMARY KEY, " +
                        DBContract.C_bankName + " TEXT NOT NULL, "+
                        DBContract.C_accountHolderName + " TEXT NOT NULL, "+
                        DBContract.C_balance + " REAL NOT NULL, "+
                        "CHECK("+ DBContract.C_balance + ">= 0)" + ")";
        db.execSQL(queryForAccT);

        String queryForTranT=
                "CREATE TABLE IF NOT EXISTS " + DBContract.TRANSACTION_TABLE +" (" +
                        DBContract.C_trasactionId + " INTEGER PRIMARY KEY, "+
                        DBContract.C_date + " TEXT NOT NULL, " +
                        DBContract.C_accountNo+" TEXT NOT NULL, "+
                        DBContract.C_expenceType+" TEXT NOT NULL, "+
                        DBContract.C_amount+" REAL NOT NULL, "+
                        "FOREIGN KEY(" + DBContract.C_accountNo + ") REFERENCES "+ DBContract.TABLE_NAME+"("+DBContract.C_accountNo+")"+ ")";
        db.execSQL(queryForTranT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String AccT =("DROP TABLE IF EXISTS "+ DBContract.TABLE_NAME);
        String TranT =("DROP TABLE IF EXISTS "+ DBContract.TRANSACTION_TABLE);
        db.execSQL(AccT);
        db.execSQL(TranT);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int i, int i1) {
        onUpgrade(db, i, i1);
    }
}
