package lk.ac.mrt.cse.dbs.simpleexpensemanager.db;

public class DBContract {
    public static final String DATABASE_NAME="Accounts_Backup.DB";
    public static final int DATABASE_VERSION=1;
    public static final String TABLE_NAME="Accounts";
    public static final String C_accountNo="accountNo";
    public static final String C_accountHolderName="accountHolderName";
    public static final String C_bankName="bankName";
    public static final String C_balance="balance";


    public final static String TRANSACTION_TABLE = "transaction_table";
    public final static String C_trasactionId= "transaction_id";
    public final static String C_date = "date";
    public final static String C_expenceType = "expense_type";
    public final static String C_amount= "amount";
}
