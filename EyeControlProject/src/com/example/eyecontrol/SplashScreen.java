package com.example.eyecontrol;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends ActionBarActivity {

	// Splash screen timer
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        
        PropertiesRetriever p = new PropertiesRetriever(getBaseContext());
 
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer.
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, p.getNumber("splash_screen_time"));
    }

	
}
