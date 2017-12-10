package cn.xufucun.udacity.inventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.xufucun.udacity.inventory.data.InventoryContract;
import cn.xufucun.udacity.inventory.databinding.ActivityInfoBinding;

public class InfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ActivityInfoBinding infoBinding;

    private Uri mCurrentPetUri;
    private static final int EXISTING_PET_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoBinding = DataBindingUtil.setContentView(this,R.layout.activity_info);

        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();

        getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_BARCODE,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentPetUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int gNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
            int gQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int gPriceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
            int gBarcodeColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_BARCODE);
            int sNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME);
            int sContactColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT);
            int sPhoneNuberColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int sEmailColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL);

            String gName = cursor.getString(gNameColumnIndex);
            int gQuantity = cursor.getInt(gQuantityColumnIndex);
            int gPrice = cursor.getInt(gPriceColumnIndex);
            String gBarcode = cursor.getString(gBarcodeColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            String sContact = cursor.getString(sContactColumnIndex);
            String sPhoneNuber = cursor.getString(sPhoneNuberColumnIndex);
            String sEmail = cursor.getString(sEmailColumnIndex);


            infoBinding.tvGoodsName.setText(gName);
            infoBinding.tvGoodsQuantity.setText(String.valueOf(gQuantity));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
