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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.xufucun.udacity.inventory.data.InventoryContract.InventoryEntry;
import cn.xufucun.udacity.inventory.databinding.ActivityEditorBinding;

public class EditorActivity extends AppCompatActivity implements View.OnTouchListener, LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private ActivityEditorBinding editorBinding;
    private static final int EXISTING_LOADER = 0;
    private Uri mCurrentUri;
    private boolean mGoodsHasChanged = false;

    private final static int TAKE_PHOTO_REQUEST = 101;
    private final static int CHOOSE_PHOTO_REQUEST = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editorBinding = DataBindingUtil.setContentView(this, R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if (mCurrentUri == null) {
            setTitle(getString(R.string.add_new_data));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.change_data));
            getLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }

        editorBinding.editGoodsName.setOnTouchListener(this);
        editorBinding.editGoodsQuantity.setOnTouchListener(this);
        editorBinding.editGoodsPrice.setOnTouchListener(this);
        editorBinding.editSupplierName.setOnTouchListener(this);
        editorBinding.editSupplierPhoneNumber.setOnTouchListener(this);

        editorBinding.upImg.setOnClickListener(this);

    }


    private boolean saveGoods() {

        BitmapDrawable bd = (BitmapDrawable)editorBinding.upImg.getDrawable();

        String goodsNameString = editorBinding.editGoodsName.getText().toString().trim();
        String goodsQuantityString = editorBinding.editGoodsQuantity.getText().toString().trim();
        String goodsPriceString = editorBinding.editGoodsPrice.getText().toString().trim();
        String supplierNameString = editorBinding.editSupplierName.getText().toString().trim();
        String supplierPhoneNumber = editorBinding.editSupplierPhoneNumber.getText().toString().trim();

        if (goodsNameString.isEmpty()
                ||goodsQuantityString.isEmpty()
                || goodsPriceString.isEmpty()
                || supplierNameString.isEmpty()
                || supplierPhoneNumber.isEmpty()) {
            Utils.showToast(this, getString(R.string.enter_error));
            return false;
        }

        if (bd == null){
            Utils.showToast(this,"请上传一张商品图片");
            return false;
        }

        if (goodsPriceString.length() >= 10) {
            Utils.showToast(this, getString(R.string.enter_price_error));
            return false;
        }

        if (goodsQuantityString.length() >= 10) {
            Utils.showToast(this, getString(R.string.enter_quantity_error));
            return false;
        }

        Bitmap bitmap = bd.getBitmap();

        ContentValues values = getInventoryValues(bitmap,goodsNameString, goodsQuantityString, goodsPriceString, supplierNameString, supplierPhoneNumber);

        if (mCurrentUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Utils.showToast(this, getString(R.string.add_fild));
            } else {
                Utils.showToast(this, getString(R.string.add_success));
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            if (rowsAffected == 0) {
                Utils.showToast(this, getString(R.string.change_fild));
            } else {
                Utils.showToast(this, getString(R.string.chang_success));
            }
        }

        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_IMAGE,
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

            int gImageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_IMAGE);
            int gNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
            int gQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int gPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
            int sNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int sPhoneNuberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            byte[] gImg = cursor.getBlob(gImageColumnIndex);
            String gName = cursor.getString(gNameColumnIndex);
            int gQuantity = cursor.getInt(gQuantityColumnIndex);
            String gPrice = cursor.getString(gPriceColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            String sPhoneNuber = cursor.getString(sPhoneNuberColumnIndex);

            ByteArrayInputStream stream = new ByteArrayInputStream(gImg);

            editorBinding.upImg.setImageDrawable(Drawable.createFromStream(stream, "img"));
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
        builder.setMessage(R.string.quiet_msg);
        builder.setPositiveButton(R.string.quit_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
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
                if (saveGoods()) {
                    finish();
                }
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
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(R.string.delete_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteGoods();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                Utils.showToast(this, getString(R.string.delete_fild));
            } else {
                Utils.showToast(this, getString(R.string.delete_success));
            }
        }
        finish();
    }


    /**
     * 创建 values
     *
     * @param name     货物名称
     * @param quantity 数量
     * @param price    价格
     * @param sName    供应商
     * @param phone    手机号
     * @return
     */
    private ContentValues getInventoryValues(Bitmap bitmap,String name, String quantity, String price, String sName, String phone) {

        ContentValues values = new ContentValues();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        values.put(InventoryEntry.COLUMN_INVENTORY_IMAGE,byteArrayOutputStream.toByteArray());
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, name);
        values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, price);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, sName);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, phone);

        return values;
    }


    private void selectImg(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("上传照片");
        final String[] items = { "拍摄照片","选择图片"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
                                break;
                            case 1:
                                Intent intent2 = new Intent();
                                intent2.setAction(Intent.ACTION_GET_CONTENT);
                                intent2.setType("image/*");
                                startActivityForResult(intent2, CHOOSE_PHOTO_REQUEST);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case TAKE_PHOTO_REQUEST:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "取消了拍照", Toast.LENGTH_LONG).show();
                    return;
                }
                Bitmap  photo = data.getParcelableExtra("data");
                editorBinding.upImg.setImageBitmap(photo);

                break;


            case CHOOSE_PHOTO_REQUEST:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "点击取消从相册选择", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    Uri imageUri = data.getData();
                    Log.e("TAG", imageUri.toString());
                    editorBinding.upImg.setImageURI(imageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.up_img:
                selectImg();
                break;
        }
    }
}
