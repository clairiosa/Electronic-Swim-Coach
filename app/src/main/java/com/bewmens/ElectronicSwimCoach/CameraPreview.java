package com.bewmens.ElectronicSwimCoach;
import java.io.IOException;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CameraPreview extends Activity implements SurfaceHolder.Callback{

    static Camera mCamera = null;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;;

    String stringPath = "/sdcard/samplevideo.3gp";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_camera);

        Button buttonStartCameraPreview = (Button)findViewById(R.id.startcamerapreview);
        Button buttonStopCameraPreview = (Button)findViewById(R.id.stopcamerapreview);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if (ContextCompat.checkSelfPermission(CameraPreview.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CameraPreview.this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }
        // // TODO: 2015-11-08 Add proper handling of the permissions check.

        buttonStartCameraPreview.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(!previewing){
                    try {
                        mCamera = Camera.open();
                        Parameters params = mCamera.getParameters();
                    }
                    catch(RuntimeException exception) {
                        Log.i("Camera", "Camera is in use by another application");
                        // Exit gracefully
                    }
                    if (mCamera != null){
                        try {
                            mCamera.setPreviewDisplay(surfaceHolder);
                            mCamera.startPreview();
                            previewing = true;
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }});

        buttonStopCameraPreview.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(mCamera != null && previewing){
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;

                    previewing = false;
                }
            }});

    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mCamera.stopPreview();
        mCamera.release();

        mCamera = null;
    }
}