package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.Database.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import android.database.sqlite.SQLiteDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PersistentTransactionDAO implements TransactionDAO {
    private DBHelper DB;

    public PersistentTransactionDAO(DBHelper databaseHelper) {
        this.DB = databaseHelper ;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        if(expenseType.equals(ExpenseType.INCOME)) {
            SQLiteDatabase DB1 = this.DB.getWritableDatabase();
            ContentValues values = new ContentValues();

            String sDate = date.toString();
            String sExpense = expenseToString(expenseType);

            values.put("Date", sDate);
            values.put("Account_no", accountNo);
            values.put("Type", sExpense);
            values.put("Amount", amount);

            DB1.insert("Transaction_table", null, values);
        }
        else {
            SQLiteDatabase sqLiteDatabase = DB.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Accounts WHERE Account_no = ?",new String[]{accountNo});
            cursor.moveToFirst();
            double balance = cursor.getDouble(3);
            if (balance<amount){
                return;
            }

            SQLiteDatabase DB2 = this.DB.getWritableDatabase();
            ContentValues values = new ContentValues();

            String sDate = date.toString();
            String sExpense = expenseToString(expenseType);

            values.put("Date", sDate);
            values.put("Account_no", accountNo);
            values.put("Type", sExpense);
            values.put("Amount", amount);

            DB2.insert("Transaction_table", null, values);
        }
    }

    private String expenseToString(ExpenseType expenseType){
        if (expenseType.equals(ExpenseType.EXPENSE)){
            return "EXPENSE";
        }
        else{
            return "INCOME";
        }
    }

    private Date toDate(String sDate){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = simpleDateFormat.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private ExpenseType toExpenseType(String sExpense){
        if(sExpense.equals("EXPENSE")){
            return ExpenseType.EXPENSE;
        }else{
            return ExpenseType.INCOME;
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase sqLiteDatabase = this.DB.getReadableDatabase();

        Cursor cursorTransactions = sqLiteDatabase.rawQuery("SELECT * FROM Transaction_Table", null);
        ArrayList<Transaction> transactions = new ArrayList<>();

        if (cursorTransactions.moveToFirst()) {
            do {

                Date transactionDate = toDate(cursorTransactions.getString(1));
                String transactionAccountNo = cursorTransactions.getString(2);
                ExpenseType transactionExpenseType = toExpenseType(cursorTransactions.getString(3));
                double transactionBalance = cursorTransactions.getDouble(4);

                transactions.add(new Transaction(transactionDate,
                        transactionAccountNo,
                        transactionExpenseType,
                        transactionBalance
                ));
            } while (cursorTransactions.moveToNext());
        }
        cursorTransactions.close();

        return transactions;
    }


    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = getAllTransactionLogs();
        int size = transactions.size();
        if (size <= limit ){
            return transactions;
        }
        return transactions.subList(size - limit,size);
    }
}
