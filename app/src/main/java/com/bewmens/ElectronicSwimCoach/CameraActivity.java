package com.bewmens.ElectronicSwimCoach;

import com.bewmens.ElectronicSwimCoach.ImageProcessingThread;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

/**
 * Created by Dave on 2015-11-09.
 */
public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase mOpenCvCameraView;
    private SensorManager mSensorManager;
    private RotationalSensor mRotationalSensor;

    private TextView tvAzimuth;
    private TextView tvPitch;
    private TextView tvRoll;

    private static final String TAG = "ESC::CameraActivity";


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraActivity(){
        Log.i(TAG, "New " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        mRotationalSensor = new RotationalSensor();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setMaxFrameSize(800, 480);
        mOpenCvCameraView.setCvCameraViewListener(this);

        tvAzimuth = (TextView)findViewById(R.id.textViewZ);
        tvRoll = (TextView)findViewById(R.id.textViewY);
        tvPitch = (TextView)findViewById(R.id.textViewX);

        mRotationalSensor.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        mRotationalSensor.stop();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        mRotationalSensor.start();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        mRotationalSensor.stop();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat greyFrame = inputFrame.gray();
        Thread thread = new Thread(new ImageProcessingThread(greyFrame));
        thread.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String azimuth=Float.toString(mRotationalSensor.getAzimuth());
                String pitch=Float.toString(mRotationalSensor.getPitch());
                String roll=Float.toString(mRotationalSensor.getRoll());

                tvAzimuth.setText(azimuth);
                tvPitch.setText(pitch);
                tvRoll.setText(roll);
            }
        });


        try {
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }


        return greyFrame;
    }


    class RotationalSensor implements SensorEventListener {

        private Sensor mRotationVectorSensor;
        private final float[] mRotationMatrix = new float[16];
        private final float[] orientation = new float[3];

        public RotationalSensor() {
            Log.i(TAG, "RotationalSensor Initialized");
            mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mRotationMatrix[ 0] = 1;
            mRotationMatrix[ 4] = 1;
            mRotationMatrix[ 8] = 1;
            mRotationMatrix[12] = 1;
        }

        public void start() {
            Log.i(TAG, "RotationalSensor Started");
            mSensorManager.registerListener(this, mRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        public void stop() {
            Log.i(TAG, "RotationalSensor Stopped");
            mSensorManager.unregisterListener(this);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.i(TAG, "RotationalSensor Changed");
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(mRotationMatrix , event.values);
                SensorManager.getOrientation(mRotationMatrix, orientation);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public float getAzimuth(){
            return orientation[0];
        }

        public float getPitch(){
            return orientation[1];
        }

        public float getRoll(){
            return orientation[2];
        }
    }

}
