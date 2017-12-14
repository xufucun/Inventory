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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import cn.xufucun.udacity.inventory.data.InventoryContract;
import cn.xufucun.udacity.inventory.databinding.ActivityInfoBinding;

public class InfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private ActivityInfoBinding infoBinding;

    private Uri mCurrentUri;
    private static final int EXISTING_LOADER = 0;

    private String contactTel;

    private int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoBinding = DataBindingUtil.setContentView(this, R.layout.activity_info);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        infoBinding.btnQuantityMinus.setOnClickListener(this);
        infoBinding.btnQuantityEdit.setOnClickListener(this);
        infoBinding.btnQuantityPlus.setOnClickListener(this);
        infoBinding.btnSubscribeTel.setOnClickListener(this);
        infoBinding.btnDeleteGoods.setOnClickListener(this);

        getLoaderManager().initLoader(EXISTING_LOADER, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //需要的列
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER,};

        return new CursorLoader(this, mCurrentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int gNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
            int gQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int sPhoneNuberColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String gName = cursor.getString(gNameColumnIndex);
            int gQuantity = cursor.getInt(gQuantityColumnIndex);
            String sPhoneNuber = cursor.getString(sPhoneNuberColumnIndex);

            infoBinding.tvGoodsName.setText(gName);
            infoBinding.tvGoodsQuantity.setText(String.valueOf(gQuantity));

            contactTel = sPhoneNuber;

            quantity = gQuantity;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        infoBinding.tvGoodsName.setText("");
        infoBinding.tvGoodsQuantity.setText("");

    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定要删除吗？");
        builder.setMessage("删除将无法恢复,请谨慎使用！");
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
                Utils.show(this,getString(R.string.delete_fild));
            } else {
                Utils.show(this,getString(R.string.delete_success));
            }
        }
        finish();
    }

    private void dialogEditQuantity(){
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);//只能输入数字
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
        inputDialog.setTitle(R.string.inptu_number).setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(isVaildNumber(editText.getText().toString().trim())){
                            int q  = Integer.valueOf(editText.getText().toString().trim());
                            editQuantity(q);
                        }

                    }
                }).show();
    }


    /**
     * 修改数量
     * @param quantity 数量
     */
    private void editQuantity(int quantity){
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,quantity);
        int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);
        if (rowsAffected == 0) {
            Utils.show(this,getString(R.string.change_fild));
        } else {
            Utils.show(this,getString(R.string.chaneg_success));
        }
    }

    private boolean isVaildNumber(String number) {
        return !number.equals("") ;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                Intent intent = new Intent(InfoActivity.this, EditorActivity.class);
                intent.setData(mCurrentUri);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_quantity_minus:
                if (quantity == 0) {
                    return;
                }
                quantity = quantity -1;
                editQuantity(quantity);
                break;
            case R.id.btn_quantity_plus:
                quantity = quantity +1;
                editQuantity(quantity);
                break;
            case R.id.btn_quantity_edit:
                dialogEditQuantity();
                break;
            case R.id.btn_subscribe_tel:
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactTel));
                startActivity(dialIntent);
                break;
            case R.id.btn_delete_goods:
                showDeleteConfirmationDialog();
                break;
            default:
                break;
        }
    }

}
