package com.ls.arcmenu;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ls.view.ArcMenu;


public class MainActivity extends AppCompatActivity implements ArcMenu.OnMenuItemClickListener {
    private ArcMenu mArcMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mArcMenu = (ArcMenu) findViewById(R.id.arc_menu);
        //动态添加一个MenuItem
//        ImageView people = new ImageView(this);
//        people.setImageResource(R.mipmap.composer_with);
//        people.setTag("People");
//        mArcMenu.addView(people);
        mArcMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuItemClick(int index, View view) {
        Toast.makeText(this, "点击的是:" + index + " " + view.getTag(), Toast.LENGTH_SHORT).show();
    }
}
