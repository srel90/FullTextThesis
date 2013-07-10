package com.thesis.FullTextThesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreenActivity extends Activity {
    private static final int REFRESH_SCREEN = 1;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        new Thread() {
            public void run() {
                try{
                    Thread.sleep(3000);
                    hRefresh.sendEmptyMessage(REFRESH_SCREEN);
                }catch(Exception e){
                }
            }
        }.start();
    }
    Handler hRefresh = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case REFRESH_SCREEN:
                    // Open mainmenu_activity
                    Intent newActivity = new Intent(SplashScreenActivity.this,LoginScreenActivity.class);
                    startActivity(newActivity);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
}
