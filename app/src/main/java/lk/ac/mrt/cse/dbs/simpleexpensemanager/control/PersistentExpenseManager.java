package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Database.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentExpenseManager extends ExpenseManager {

    private TransactionDAO persistentTransactionDAO ;
    private AccountDAO persistentAccountDAO;

    private DBHelper DB;

    public PersistentExpenseManager(Context context) {

        this.DB = new DBHelper(context);
        try {
            setup();
        } catch (Exception e) {
            System.out.println("Setup ERROR!!!");
        }
    }

    @Override
    public void setup() {
        persistentTransactionDAO = new PersistentTransactionDAO(this.DB);
        persistentAccountDAO = new PersistentAccountDAO(this.DB);
        setTransactionsDAO(persistentTransactionDAO);
        setAccountsDAO(persistentAccountDAO);
    }
}
