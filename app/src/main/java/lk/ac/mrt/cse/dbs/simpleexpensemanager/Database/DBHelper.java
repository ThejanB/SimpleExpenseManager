package lk.ac.mrt.cse.dbs.simpleexpensemanager.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TABLE_ACCOUNT = "Accounts";
    private static final String ACC_NO_COL = "Account_no";

    private static final String TABLE_TRANSACTION = "Transaction_table";


    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context) {
        super(context, "200698X", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String queryTable1 = "CREATE TABLE " + TABLE_ACCOUNT + " ("
                + ACC_NO_COL + " TEXT PRIMARY KEY, "
                + "Bank TEXT, "
                + "Account_holder TEXT,"
                + "Initial_balance REAL);";
        sqLiteDatabase.execSQL(queryTable1);

        String queryTable2 = "CREATE TABLE " + TABLE_TRANSACTION + " ("
                + "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "Date TEXT,"
                + "Account_no TEXT,"
                + "Type TEXT,"
                + "Amount REAL)";
        sqLiteDatabase.execSQL(queryTable2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        onCreate(db);
    }

    public List<Account>  getAccountDataList (){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursorAccounts = DB.rawQuery("SELECT * FROM Accounts",null);
        ArrayList<Account> accountList = new ArrayList<>();

        if(cursorAccounts.moveToFirst()) {
            do {
                accountList.add(
                        new Account(cursorAccounts.getString(0),
                                cursorAccounts.getString(1),
                                cursorAccounts.getString(2),
                                cursorAccounts.getDouble(3)));
            } while (cursorAccounts.moveToNext());
        }
        cursorAccounts.close();

        return accountList;
    }

}
