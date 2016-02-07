package com.bewmens.ElectronicSwimCoach;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class Main_Menu extends Activity implements TextToSpeech.OnInitListener{



    private TextToSpeech mEngine;
    private Vibrator mVibrator;

	@Override
	protected void onCreate(Bundle savedState) {
		// TODO Auto-generated method stub
		super.onCreate(savedState);
		setContentView(R.layout.activity_main);

		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)) {

			} else {


				ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {


                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

		//set up the button sound
		final MediaPlayer mpButtonClick = MediaPlayer.create(this, R.raw.button_click);



		//Begin button

		Button bRecording = (Button) findViewById(R.id.bCamera);
		bRecording.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.bewmens.ElectronicSwimCoach.CAMERA"));
				mpButtonClick.start();
			}
		});

		Button bCamera = (Button) findViewById(R.id.bRecording);
		bCamera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.bewmens.ElectronicSwimCoach.RECORD"));
				mpButtonClick.start();
			}
		});

        Button bLeft = (Button) findViewById(R.id.bLeft);
        bLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                speakLeft();
            }
        });

        Button bRight = (Button) findViewById(R.id.bRight);
        bRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                speakRight();
            }
        });

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mEngine = new TextToSpeech(this, this);

	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater awesome = getMenuInflater ();
		awesome.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected (MenuItem item){
		switch (item.getItemId()) {
		
		case R.id.menuSweet:
			startActivity(new Intent("com.bewmens.ElectronicSwimCoach.SWEET"));
			return true;
		case R.id.menuToast:
			Toast display = Toast.makeText(this, "HOWAREYOU?", Toast.LENGTH_SHORT);
			display.show();
			return true;
		}
		return false;
	}

    @Override
    public void onInit(int status) {
        Log.d("Speech", "OnInit - Status [" + status + "]");

        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speech", "Success!");
            mEngine.setLanguage(Locale.UK);
        }
    }

    private void speakLeft() {
        mEngine.speak("Left.", TextToSpeech.QUEUE_FLUSH, null, null);
        mVibrator.vibrate(500);
    }
    private void speakRight() {
        mEngine.speak("Right.", TextToSpeech.QUEUE_FLUSH, null, null);
        mVibrator.vibrate(new long[]{0, 500, 110, 500}, -1);
    }
}
