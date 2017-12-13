package cn.xufucun.udacity.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.xufucun.udacity.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by xufuc on 2017/12/10.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    //数据库文件名
    private static final String DATABASE_NAME = "shelter.db";

    //数据库版本
    private static final int DATABASE_VERSION = 4;

    // 创建一个包含SQL语句的字符串来创建账单
    private String SQL_CREATE_BILLS_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
            + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, "
            + InventoryEntry.COLUMN_INVENTORY_QUANTITY + " INTEGER DEFAULT 0, "
            + InventoryEntry.COLUMN_INVENTORY_PRICE + " TEXT NOT NULL, "
            + InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
            + InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";

    // 创建一个包含SQL语句的字符串来删除账单
    private static final String SQL_DELETE_BILLS_TABLE = "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //执行sql语句
        sqLiteDatabase.execSQL(SQL_CREATE_BILLS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //删除表格并重新创建
        sqLiteDatabase.execSQL(SQL_DELETE_BILLS_TABLE);
        onCreate(sqLiteDatabase);
    }
}
