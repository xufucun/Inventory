package cn.xufucun.udacity.inventory;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list_view);
        View emptyView = View.inflate(this,R.layout.empty_view,null);
        listView.setEmptyView(emptyView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivity(intent);
    }


}
