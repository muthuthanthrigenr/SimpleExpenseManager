package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DBContract;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DatabaseHelper;

public class PersistentTransactionDAO implements TransactionDAO {

    private final DatabaseHelper databaseHelper;

    public PersistentTransactionDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        SimpleDateFormat d_Format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        String query = "SELECT " + DBContract.C_balance+ " FROM " + DBContract.TABLE_NAME + " WHERE " + DBContract.C_accountNo + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{accountNo});
        if (cursor.moveToFirst()){
            double balance = cursor.getDouble(0);
            cursor.close();
            //Log Transaction if it is an income or possible expense(if sufficient funds are available)
            if (expenseType == ExpenseType.INCOME || balance - amount >= 0){
                ContentValues cv = new ContentValues();
                cv.put(DBContract.C_date, d_Format.format(date));
                cv.put(DBContract.C_accountNo, accountNo);
                cv.put(DBContract.C_expenceType, String.valueOf(expenseType));
                cv.put(DBContract.C_amount, amount);
                db.insert(DBContract.TRANSACTION_TABLE, null, cv);
            }
        }
    }
    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBContract.TRANSACTION_TABLE + " ORDER BY " + DBContract.C_trasactionId + " desc" ;
        Cursor cursor = db.rawQuery(query, null);
        List<Transaction> out=toList(cursor);
        return out;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBContract.TRANSACTION_TABLE + " ORDER BY " + DBContract.C_trasactionId + " desc LIMIT "+" ?";
        Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(limit)});
        return toList(cursor);
    }
    //GET THE LIST
    private List<Transaction> toList(Cursor cursor){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        List<Transaction> out = new ArrayList<>();

        while (cursor.moveToNext()){
            Date date = new Date();
            try {
                date = dateFormat.parse(cursor.getString(1));
            }
            catch(ParseException e ) {
                e.printStackTrace();
            }
            String accountNo = cursor.getString(2);
            ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(3));
            double amount = cursor.getDouble(4);
            Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
            out.add(transaction);
        }
        cursor.close();
        return out;
    }
}
