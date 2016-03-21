package com.bewmens.ElectronicSwimCoach;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

import org.opencv.android.OpenCVLoader;
//import org.opencv.android.  //how tto import opencv needed classes

public class MainActivity extends Activity {
	MediaPlayer mpSplash;
 //Called when the activity is first created
	static{
		if(!OpenCVLoader.initDebug()){
			Log.i("opencv", "opencv initialization failed");
		}else{
			Log.i("opencv", "opencv initialization successful");
		}
 	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.loadlibs();  //load openCV and other needed libraries
		//setContentView(R.layout.splash);
        
        //mpSplash = MediaPlayer.create(this, R.raw.mario);
        //mpSplash.start();
        /*
        Thread logoTimer= new Thread(){
        	public void run(){
        	try{
        		int logoTimer = 0;
        		while(logoTimer<500) {
        			sleep(100);
        			logoTimer= logoTimer+100;
        		}
        		startActivity(new Intent("com.bewmens.ElectronicSwimCoach.CLEARSCREEN"));
        	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	finally{
        		finish();
        	}
        		
        	}
        };
        logoTimer.start();
        */
        startActivity(new Intent("com.bewmens.ElectronicSwimCoach.CLEARSCREEN"));
    }

    
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mpSplash.release();
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mpSplash.pause();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mpSplash.start();
	}




	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	public static void loadlibs() {
		System.loadLibrary("opencv_java3");
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hello, menu);
        return true;
    }
    
}
