package cn.xufucun.udacity.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MayiSenlin on 2017/12/9.
 */

public final class InventoryContract {

    private InventoryContract(){}

    public static final String CONTENT_AUTHORITY = "cn.xufucun.udacity.inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "inventory";

    public static final class InventoryEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public final static String TABLE_NAME = "inventory";

        public final static String _ID = BaseColumns._ID;  //ID INTEGER
        public final static String COLUMN_INVENTORY_IMAGE = "img"; //商品图片
        public final static String COLUMN_INVENTORY_NAME ="name"; //商品名称 TEXT
        public final static String COLUMN_INVENTORY_QUANTITY ="quantity";//数量 INTEGER
        public final static String COLUMN_INVENTORY_PRICE ="price";//单价 TEXT
        public final static String COLUMN_SUPPLIER_NAME ="supplier";//供应商 TEXT
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER ="phoneNumber"; //手机号 TEXT
    }
}
