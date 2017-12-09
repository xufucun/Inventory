package cn.xufucun.udacity.inventory;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by MayiSenlin on 2017/12/9.
 */

public class InventoryCursorAdapter extends CursorAdapter {


    public InventoryCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public InventoryCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

//        int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
//        int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
//
//        String petName = cursor.getString(nameColumnIndex);
//        String petBreed = cursor.getString(breedColumnIndex);
//
//        if (TextUtils.isEmpty(petBreed)) {
//            petBreed = context.getString(R.string.unknown_breed);
//        }

//        nameTextView.setText(petName);
//        summaryTextView.setText(petBreed);

    }
}
