package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.Database.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private DBHelper DB;

    private static final String TABLE_NAME = "Accounts";
    private static final String ACC_NO_COL = "Account_no";


    public PersistentAccountDAO(DBHelper DB){
        this.DB = DB;
    }

    @Override
    public List<String> getAccountNumbersList() {

        List<Account> accountList = getAccountsList();
        List<String> accountNumbers = new ArrayList<>();

        for(int i = 0; i < accountList.size(); i++){
            accountNumbers.add(accountList.get(i).getAccountNo());
        }
        return accountNumbers;

    }

    @Override
    public List<Account> getAccountsList() {
        return DB.getAccountDataList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        List<Account> accountArrayList = getAccountsList();
        for(Account account:accountArrayList){
            if(account.getAccountNo().equals(accountNo)){
                return account;
            }
        }
        throw new InvalidAccountException("Invalid Account");
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = this.DB.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("Bank", account.getBankName());
        values.put(ACC_NO_COL, account.getAccountNo());
        values.put("Account_holder", account.getAccountHolderName());
        values.put("Initial_balance", account.getBalance());

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = this.DB.getWritableDatabase();
        try{

            sqLiteDatabase.delete(TABLE_NAME, "name = ?", new String[]{accountNo});
            sqLiteDatabase.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new InvalidAccountException("Invalid Account");
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = this.DB.getWritableDatabase();
        Account account = getAccount(accountNo);
        ContentValues values = new ContentValues();
        double new_balance;
        if(expenseType == ExpenseType.EXPENSE){

            if (account.getBalance()<amount){
                throw new InvalidAccountException("Invalid Account.");
            }else{
                new_balance = account.getBalance()-amount;
                values.put("Initial_balance",new_balance);
                sqLiteDatabase.update(TABLE_NAME,values,ACC_NO_COL+" = ? ",new String[]{accountNo});
                sqLiteDatabase.close();
            }
            return;
        }
        if(expenseType == ExpenseType.INCOME){
            new_balance = account.getBalance()+amount;
            values.put("Initial_balance",new_balance);
            sqLiteDatabase.update(TABLE_NAME,values,ACC_NO_COL+" = ? ",new String[]{accountNo});
            sqLiteDatabase.close();
        }
    }
}
