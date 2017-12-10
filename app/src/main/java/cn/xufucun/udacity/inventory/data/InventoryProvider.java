package cn.xufucun.udacity.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import cn.xufucun.udacity.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by xufuc on 2017/12/10.
 */

public class InventoryProvider extends ContentProvider{

    private static final String TAG = "InventoryProvider";

    /** 宠物表的内容URI的URI匹配器代码 */
    private static final int PETS = 100;

    /** 宠物表中单个宠物的内容URI的URI匹配器代码 */
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, PETS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", PET_ID);
    }

    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match){

            case PETS:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PET_ID:

                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;

                default:
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from  the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }



    private Uri insertPet(Uri uri, ContentValues values) {

        String gName = values.getAsString(InventoryEntry.COLUMN_INVENTORY_NAME);
        if (gName == null) {
            throw new IllegalArgumentException("Inventory requires a name");
        }

        Integer gQUantity = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        if (gQUantity == null) {
            throw new IllegalArgumentException("Inventory requires a name");
        }

        Integer gPrice = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_PRICE);
        if (gPrice == null) {
            throw new IllegalArgumentException("Inventory requires a name");
        }

        String gBarcode = values.getAsString(InventoryEntry.COLUMN_INVENTORY_BARCODE);
        if (gBarcode == null) {
            throw new IllegalArgumentException("Inventory requires a name");
        }

        String sName = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
        if (sName == null) {
            throw new IllegalArgumentException("Inventory requires a name");
        }

        String sContact = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_CONTACT);
        if (sContact == null) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        String sPhoneNumber = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (sPhoneNumber == null) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        String sEmail = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
        if (sEmail == null ) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_INVENTORY_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_PRICE)) {
            Integer price = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_PRICE);
            if (price == null ) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_BARCODE)) {
            String barcode = values.getAsString(InventoryEntry.COLUMN_INVENTORY_BARCODE);
            if (barcode == null ) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
            if (name == null ) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_CONTACT)) {
            String contact = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_CONTACT);
            if (contact == null ) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String phone = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (phone == null ) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_EMAIL)) {
            String email = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            if (email == null ) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }


        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
