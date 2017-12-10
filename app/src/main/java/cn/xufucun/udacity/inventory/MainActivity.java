package cn.xufucun.udacity.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import cn.xufucun.udacity.inventory.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivity";
    private static final int INVENTORY_LOADER = 0;
    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list_view);
      //  View emptyView = View.inflate(this,R.layout.empty_view,null);
      //  listView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Log.d(TAG, "onItemClick: 点击了列表项");
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivity(intent);
    }


//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
//        Log.d(TAG, "onItemClick: 点击了列表项");
//        Intent intent = new Intent(this, EditorActivity.class);
//        Uri currentPetUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
//        intent.setData(currentPetUri);
//        startActivity(intent);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        //定义一个投影来指定我们关心的表中的列。
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryEntry.COLUMN_INVENTORY_QUANTITY};

        //该加载器将在后台线程上执行ContentProvider的查询方法
        return new CursorLoader(this,    // Parent activity context
                InventoryEntry.CONTENT_URI,      // Provider content URI to query
                projection,                      // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,               // No selection arguments
                null);                 // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    //TODO 插入 删除

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //TODO 选择菜单
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //插入虚拟数据
    private void insertInventory(){
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, "商品名称");
        values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, "Terrier");
//        values.put(InventoryEntry.COLUMN_PET_GENDER, InventoryEntry.GENDER_MALE);
//        values.put(InventoryEntry.COLUMN_PET_WEIGHT, 7);

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    //删除所有数据
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

}
