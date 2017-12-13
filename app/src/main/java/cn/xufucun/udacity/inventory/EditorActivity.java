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
    private static final int EXISTING_LOADER = 0;
    private Uri mCurrentUri;
    private boolean mGoodsHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editorBinding = DataBindingUtil.setContentView(this,R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if (mCurrentUri == null) {
            setTitle("添加新数据");
            invalidateOptionsMenu();
        } else {
            setTitle("修改数据");
            getLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }

        editorBinding.editGoodsName.setOnTouchListener(this);
        editorBinding.editGoodsQuantity.setOnTouchListener(this);
        editorBinding.editGoodsPrice.setOnTouchListener(this);
        editorBinding.editSupplierName.setOnTouchListener(this);
        editorBinding.editSupplierPhoneNumber.setOnTouchListener(this);

    }


    private void saveGoods() {
        String goodsNameString = editorBinding.editGoodsName.getText().toString().trim();
        String goodsQuantityString = editorBinding.editGoodsQuantity.getText().toString().trim();
        String goodsPriceString = editorBinding.editGoodsPrice.getText().toString().trim();
        String supplierNameString = editorBinding.editSupplierName.getText().toString().trim();
        String supplierPhoneNumber = editorBinding.editSupplierPhoneNumber.getText().toString().trim();

        if (goodsNameString.isEmpty() ||goodsQuantityString.isEmpty() ||supplierNameString.isEmpty() ||supplierPhoneNumber.isEmpty()){
            Toast.makeText(this, "输入有误，请重新输入", Toast.LENGTH_SHORT).show();//不明白，这里的Toast前面为什么会有应用名称？
            return;
        }



        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, goodsNameString);
        values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, goodsQuantityString);
        values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, goodsPriceString);

        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);


        if (mCurrentUri == null){
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            }
        }else {

            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

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
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this, mCurrentUri, projection, null, null, null);
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
            int sNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int sPhoneNuberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String gName = cursor.getString(gNameColumnIndex);
            int gQuantity = cursor.getInt(gQuantityColumnIndex);
            String gPrice = cursor.getString(gPriceColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            String sPhoneNuber = cursor.getString(sPhoneNuberColumnIndex);

            editorBinding.editGoodsName.setText(gName);
            editorBinding.editGoodsQuantity.setText(String.valueOf(gQuantity));
            editorBinding.editGoodsPrice.setText(gPrice);
            editorBinding.editSupplierName.setText(sName);
            editorBinding.editSupplierPhoneNumber.setText(sPhoneNuber);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        editorBinding.editGoodsName.setText("");
        editorBinding.editGoodsQuantity.setText("");
        editorBinding.editGoodsPrice.setText("");
        editorBinding.editSupplierName.setText("");
        editorBinding.editSupplierPhoneNumber.setText("");
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
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
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
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveGoods();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mGoodsHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                showUnsavedChangesDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除提示");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteGoods();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteGoods() {
        if (mCurrentUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
