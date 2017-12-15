package cn.xufucun.udacity.inventory;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;


import cn.xufucun.udacity.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by MayiSenlin on 2017/12/9.
 */

public class InventoryCursorAdapter extends CursorAdapter  {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ImageView imageView = view.findViewById(R.id.image_view);
        TextView tvGoodsName = view.findViewById(R.id.tv_goods_name);
        TextView tvGoodsPrice = view.findViewById(R.id.tv_goods_price);
        TextView tvGoodsQuantity = view.findViewById(R.id.tv_goods_quantity);
        Button button = view.findViewById(R.id.btn_sales);

        int id = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_IMAGE);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);


        byte[] goodsImage = cursor.getBlob(imageColumnIndex);
        String goodsName = cursor.getString(nameColumnIndex);
        String goodsPrice = cursor.getString(priceColumnIndex);
        String goodsQuantity = cursor.getString(quantityColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);

        final Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

        //        Bitmap bitmap = BitmapFactory.decodeByteArray(goodsImage,0,goodsImage.length);


        DecimalFormat df   = new DecimalFormat("#.00");
        String price = df.format(Float.valueOf(goodsPrice));

        ByteArrayInputStream stream = new ByteArrayInputStream(goodsImage);
        imageView.setImageDrawable(Drawable.createFromStream(stream, "img"));

        tvGoodsName.setText(goodsName);

        String s1 = context.getString(R.string.list_price);
        String gPrice = String.format(s1,price);

        String s2 = context.getString(R.string.list_quantity);
        String gQuantity = String.format(s2,goodsQuantity);

        tvGoodsPrice.setText(gPrice);
        tvGoodsQuantity.setText(gQuantity);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                if (quantity == 0){
                    return;
                }

                int q = quantity;
                q = q -1;
                values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, q);

                resolver.update(currentUri, values, null, null);
                context.getContentResolver().notifyChange(currentUri, null);

            }
        });

    }


}
