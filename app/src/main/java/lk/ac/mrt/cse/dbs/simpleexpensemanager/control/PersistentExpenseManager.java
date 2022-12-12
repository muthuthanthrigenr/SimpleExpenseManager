package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DatabaseHelper;

public class PersistentExpenseManager extends ExpenseManager {
    private final DatabaseHelper databaseHelper;

    public PersistentExpenseManager(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
        setup();
    }

    @Override
    public void setup() {
        AccountDAO persistentAccountDAO = new PersistentAccountDAO(databaseHelper);
        setAccountsDAO(persistentAccountDAO);

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(databaseHelper);
        setTransactionsDAO(persistentTransactionDAO);

//      sample data
        Account dummyAcc1 = new Account("11111z", "boc", "lahiru", 100000.0);
        Account dummyAcc2 = new Account("22222x", "com", "nus", 87894.0);
        persistentAccountDAO.addAccount(dummyAcc1);
        persistentAccountDAO.addAccount(dummyAcc2);

    }
}