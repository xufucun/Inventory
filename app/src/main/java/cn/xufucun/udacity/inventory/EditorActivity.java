package cn.xufucun.udacity.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import cn.xufucun.udacity.inventory.data.InventoryContract.InventoryEntry;
import cn.xufucun.udacity.inventory.databinding.ActivityEditorBinding;

public class EditorActivity extends AppCompatActivity implements View.OnTouchListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ActivityEditorBinding editorBinding;
    private static final int EXISTING_PET_LOADER = 0;
    private Uri mCurrentPetUri;
    private boolean mGoodsHasChanged = false; //修改过？

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editorBinding = DataBindingUtil.setContentView(this,R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();

        if (mCurrentPetUri == null) {
            setTitle("添加新数据");
            invalidateOptionsMenu();
        } else {
            setTitle("修改数据");
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        editorBinding.editGoodsName.setOnTouchListener(this);
        editorBinding.editGoodsQuantity.setOnTouchListener(this);
        editorBinding.editGoodsPrice.setOnTouchListener(this);
        editorBinding.editGoodsBarcode.setOnTouchListener(this);

        editorBinding.editSupplierName.setOnTouchListener(this);
        editorBinding.editSupplierContact.setOnTouchListener(this);
        editorBinding.editSupplierPhoneNumber.setOnTouchListener(this);
        editorBinding.editSupplierEmail.setOnTouchListener(this);

    }


    private void savePet() {
        String goodsNameString = editorBinding.editGoodsName.getText().toString().trim();
        String goodsQuantityString = editorBinding.editGoodsQuantity.getText().toString().trim();
        String goodsPriceString = editorBinding.editGoodsPrice.getText().toString().trim();
        String goodsBarcodeString = editorBinding.editGoodsBarcode.getText().toString().trim();
        String supplierNameString = editorBinding.editSupplierName.getText().toString().trim();
        String supplierContactString = editorBinding.editSupplierContact.getText().toString().trim();
        String supplierPhoneNumber = editorBinding.editSupplierPhoneNumber.getText().toString().trim();
        String supplierEmail = editorBinding.editSupplierEmail.getText().toString().trim();

        if (mCurrentPetUri == null
                &&goodsNameString.isEmpty()
                &&goodsQuantityString.isEmpty()
//                && goodsPriceString.isEmpty()
                &&goodsBarcodeString.isEmpty()
                &&supplierNameString.isEmpty()
                &&supplierPhoneNumber.isEmpty()
                &&supplierEmail.isEmpty()){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, goodsNameString);
        values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, goodsQuantityString);
        values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, goodsPriceString);
        values.put(InventoryEntry.COLUMN_INVENTORY_BARCODE, goodsBarcodeString);

        values.put(InventoryEntry.COLUMN_SUPPLIER_CONTACT, supplierContactString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);


        if (mCurrentPetUri == null){
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            }
        }else {

            int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryEntry.COLUMN_INVENTORY_BARCODE,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_CONTACT,
                InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL };

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

            int gNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
            int gQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int gPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
            int gBarcodeColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_BARCODE);
            int sNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int sContactColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_CONTACT);
            int sPhoneNuberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int sEmailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL);

            String gName = cursor.getString(gNameColumnIndex);
            int gQuantity = cursor.getInt(gQuantityColumnIndex);
            int gPrice = cursor.getInt(gPriceColumnIndex);
            String gBarcode = cursor.getString(gBarcodeColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            String sContact = cursor.getString(sContactColumnIndex);
            String sPhoneNuber = cursor.getString(sPhoneNuberColumnIndex);
            String sEmail = cursor.getString(sEmailColumnIndex);


            editorBinding.editGoodsName.setText(gName);
            editorBinding.editGoodsQuantity.setText(String.valueOf(gQuantity));
            editorBinding.editGoodsPrice.setText(String.valueOf(gPrice));
            editorBinding.editGoodsBarcode.setText(gBarcode);
            editorBinding.editSupplierName.setText(sName);
            editorBinding.editSupplierContact.setText(sContact);
            editorBinding.editSupplierPhoneNumber.setText(sPhoneNuber);
            editorBinding.editSupplierEmail.setText(sEmail);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        editorBinding.editGoodsName.setText("");
        editorBinding.editGoodsQuantity.setText("");
        editorBinding.editGoodsPrice.setText("");
        editorBinding.editGoodsBarcode.setText("");
        editorBinding.editSupplierName.setText("");
        editorBinding.editSupplierContact.setText("");
        editorBinding.editSupplierPhoneNumber.setText("");
        editorBinding.editSupplierEmail.setText("");
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mGoodsHasChanged = true;
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!mGoodsHasChanged) {
            super.onBackPressed();
            return;
        }

        showUnsavedChangesDialog();
    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("未保存，要退出吗?");
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("继续编辑", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savePet();
                finish();
                return true;
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mGoodsHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                showUnsavedChangesDialog2();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void showUnsavedChangesDialog2() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("未保存，要退出吗");
        builder.setPositiveButton("DisCard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
            }
        });
        builder.setNegativeButton("继续编辑", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除提示");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentPetUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
