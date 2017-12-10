package cn.xufucun.udacity.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import cn.xufucun.udacity.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by MayiSenlin on 2017/12/9.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvGoodsName = view.findViewById(R.id.tv_goods_name);
        TextView tvGoodsPrice = view.findViewById(R.id.tv_goods_price);
        TextView tvGoodsQuantity = view.findViewById(R.id.tv_goods_quantity);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);

        String goodsName = cursor.getString(nameColumnIndex);
        String goodsPrice = cursor.getString(priceColumnIndex);
        String goodsQuantity = cursor.getString(quantityColumnIndex);

        tvGoodsName.setText(goodsName);
        tvGoodsPrice.setText(goodsPrice);
        tvGoodsQuantity.setText(goodsQuantity);

    }
}
