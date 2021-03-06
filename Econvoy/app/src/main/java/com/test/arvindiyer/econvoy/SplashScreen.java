/*
Created by Arvind iyer
*/

package com.test.arvindiyer.econvoy;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SplashScreen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);

                }catch (InterruptedException ie)
                {
                    ie.printStackTrace();

                }finally {
                    Intent login=new Intent("com.test.arvindiyer.econvoy.Login");
                    startActivity(login);
                }
            };
        };

        timer.start();
    }
    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }
}
