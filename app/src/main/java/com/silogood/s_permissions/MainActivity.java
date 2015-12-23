package com.silogood.s_permissions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{    //AppCompatActivity  material 도구 이용할수있게 해주는 extends

    Button applicationbtn;
    Button permissionbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        applicationbtn = (Button) findViewById(R.id.applicationbtn);
        applicationbtn.setOnClickListener(this);

        permissionbtn = (Button) findViewById(R.id.permissionbtn);
        permissionbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.applicationbtn:
                intent = new Intent(MainActivity.this, Applications_Tab.class);
                break;
            case R.id.permissionbtn:
                intent = new Intent(getApplicationContext(), Permissions_Tab.class);
                break;
        }

        startActivity(intent);    //어플당 퍼미션
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }   //메뉴
//

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }    //옵션 이여

        return super.onOptionsItemSelected(item);
    }*/
}
