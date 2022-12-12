package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DBContract;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DatabaseHelper;

public class PersistentAccountDAO implements AccountDAO {
    private final DatabaseHelper databaseHelper;

    public PersistentAccountDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> out = new ArrayList<>();
        String query="SELECT "+DBContract.C_accountNo+ " FROM "+ DBContract.TABLE_NAME;
        //String query="SELECT * FROM "+DBContract.TABLE_NAME+";";
        SQLiteDatabase db=databaseHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);

        while (cursor.moveToNext()){
            out.add(cursor.getString(0));
        }
        cursor.close();
        return out;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> out = new ArrayList<>();
        String query="SELECT * FROM "+DBContract.TABLE_NAME;
        SQLiteDatabase db=databaseHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        while (cursor.moveToNext()){
            String accountNo = cursor.getString(0);
            String bankName = cursor.getString(1);
            String accountHolderName = cursor.getString(2);
            double balance = cursor.getDouble(3);

            Account account = new Account(accountNo, bankName, accountHolderName, balance);
            out.add(account);
        }
        cursor.close();
        return out;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        String query ="SELECT * FROM "+DBContract.TABLE_NAME+" WHERE "+ DBContract.C_accountNo +" =?;";
        SQLiteDatabase db=databaseHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(query,new String[]{accountNo});
        if (cursor.moveToFirst()){
            Account acc = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
            cursor.close();
            return acc;
        } else {
            cursor.close();
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db=databaseHelper.getWritableDatabase();
        ContentValues cv=new ContentValues();

        cv.put(DBContract.C_accountNo,account.getAccountNo());
        cv.put(DBContract.C_bankName,account.getBankName());
        cv.put(DBContract.C_accountHolderName,account.getAccountHolderName());
        cv.put(DBContract.C_balance,account.getBalance());
        db.insert(DBContract.TABLE_NAME,null,cv);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db=databaseHelper.getWritableDatabase();
        long delete = db.delete(DBContract.TABLE_NAME,DBContract.C_accountNo + "= ?", new String[]{accountNo});
        if (delete == -1) {
            throw new InvalidAccountException("CHECK ACCOUNT NUMBER!");
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Account account = this.getAccount(accountNo);
        double updatedBalance;
        ContentValues val = new ContentValues();
        switch (expenseType){
            case INCOME:
                updatedBalance = account.getBalance() + amount;
                val.put(DBContract.C_balance, updatedBalance);
                long updateIncome = db.update(DBContract.TABLE_NAME, val,DBContract.C_accountNo + "=?", new String[]{accountNo});

                if (updateIncome == 0) {
                    throw new InvalidAccountException("DATABASE ERROR!!");
                }
                break;

            case EXPENSE:
                if (account.getBalance() - amount < 0){
                    throw new InvalidAccountException("Insufficient Balance!!. Available balance is " + account.getBalance());
                } else {
                    updatedBalance = account.getBalance() - amount;
                    val.put(DBContract.C_balance, updatedBalance);
                    long updateExpense = db.update(DBContract.TABLE_NAME, val,DBContract.C_accountNo + "=?", new String[]{accountNo});
                    if (updateExpense == 0) {
                        throw new InvalidAccountException("DATABASE ERROR!!");
                    }
                }
                break;


        }

    }
}
