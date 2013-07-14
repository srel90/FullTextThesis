package com.thesis.FullTextThesis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 7/6/13
 * Time: 4:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainMenuActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mainmenu_activity);
        ((TextView)findViewById(R.id.txtwelcome)).setText("Welcome :"+getIntent().getExtras().getString("name")+" "+getIntent().getExtras().getString("lname"));
        findViewById(R.id.btnsearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivity = new Intent(MainMenuActivity.this,SearchActivity.class);
                newActivity.putExtra("idmem", getIntent().getExtras().getString("idmem"));
                newActivity.putExtra("name", getIntent().getExtras().getString("name"));
                newActivity.putExtra("lname", getIntent().getExtras().getString("lname"));
                startActivity(newActivity);
            }
        });
        findViewById(R.id.btnhistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivity = new Intent(MainMenuActivity.this,HistoryActivity.class);
                newActivity.putExtra("idmem", getIntent().getExtras().getString("idmem"));
                newActivity.putExtra("name", getIntent().getExtras().getString("name"));
                newActivity.putExtra("lname", getIntent().getExtras().getString("lname"));
                startActivity(newActivity);
            }
        });
        findViewById(R.id.btnAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivity = new Intent(MainMenuActivity.this,AboutActivity.class);
                startActivity(newActivity);
            }
        });
        findViewById(R.id.btnexit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                moveTaskToBack(true);
            }
        });

    }

}