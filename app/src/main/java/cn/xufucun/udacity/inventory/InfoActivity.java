package cn.xufucun.udacity.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.xufucun.udacity.inventory.data.InventoryContract;
import cn.xufucun.udacity.inventory.databinding.ActivityInfoBinding;

public class InfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private ActivityInfoBinding infoBinding;

    private Uri mCurrentPetUri;
    private static final int EXISTING_PET_LOADER = 0;

    private String contactTel;
    private String contactEmail;


    private int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoBinding = DataBindingUtil.setContentView(this, R.layout.activity_info);

        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();

        infoBinding.btnQuantityMinus.setOnClickListener(this);
        infoBinding.btnQuantityEdit.setOnClickListener(this);
        infoBinding.btnQuantityPlus.setOnClickListener(this);
        infoBinding.btnSubscribeTel.setOnClickListener(this);
        infoBinding.btnSubscribeEmail.setOnClickListener(this);
        infoBinding.btnDeleteGoods.setOnClickListener(this);

        getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //需要的列
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL};

        return new CursorLoader(this, mCurrentPetUri, projection, null, null, null);
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
            int sEmailColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL);

            String gName = cursor.getString(gNameColumnIndex);
            int gQuantity = cursor.getInt(gQuantityColumnIndex);
            String sPhoneNuber = cursor.getString(sPhoneNuberColumnIndex);
            String sEmail = cursor.getString(sEmailColumnIndex);

            infoBinding.tvGoodsName.setText(gName);
            infoBinding.tvGoodsQuantity.setText(String.valueOf(gQuantity));

            contactTel = sPhoneNuber;
            contactEmail = sEmail;

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

    private void dialogEditQuantity(){
        final EditText editText = new EditText(this);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
        inputDialog.setTitle("输入要修改的数量").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(MainActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                        int q  = Integer.valueOf(editText.getText().toString());
                        editQuantity(q);
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
        int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);
        if (rowsAffected == 0) {
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        }
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
                intent.setData(mCurrentPetUri);
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
            case R.id.btn_subscribe_email:
                Intent i = new Intent(Intent.ACTION_SEND);
                // i.setType("text/plain"); //模拟器请使用这行
                i.setType("message/rfc822"); // 真机上使用这行
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{contactEmail});
                i.putExtra(Intent.EXTRA_SUBJECT, "您的建议");
                i.putExtra(Intent.EXTRA_TEXT, "我们很希望能得到您的建议！！！");
                startActivity(Intent.createChooser(i, "Select email application."));
                break;
            case R.id.btn_delete_goods:
                showDeleteConfirmationDialog();
                break;
            default:
                break;
        }
    }
}
