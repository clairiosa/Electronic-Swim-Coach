package com.bewmens.ElectronicSwimCoach;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dave on 2015-11-09.
 */
public class RecordActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private FrameBuffer mFrameBuffer;

    private CameraBridgeViewBase mOpenCvCameraView;
    private SensorManager mSensorManager;
    private RotationalSensor mRotationalSensor;

    String gyro;
    Mat cameraFrame;
    FileWriter writer;
    File sensorFile;
    File mediaFile;
    File mediaStorageDir;
    String timeStamp;
    String filename;
    int frameNum;



    private static final String TAG = "ESC::RecordActivity";


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

    public RecordActivity(){
        Log.i(TAG, "New " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        mRotationalSensor = new RotationalSensor();

        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ESC_Frames");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
            }
        }

        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        frameNum = 0;

        sensorFile = new File(mediaStorageDir.getPath(), timeStamp + ".txt");

        if (!sensorFile.exists()) {
            try {
                sensorFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            writer = new FileWriter(sensorFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_record);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setMaxFrameSize(800, 480);

        mOpenCvCameraView.setCvCameraViewListener(this);


        mRotationalSensor.start();

        mFrameBuffer = new FrameBuffer();
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
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        gyro = Float.toString(mFrameBuffer.getAzimuth()) + "," + Float.toString(mFrameBuffer.getPitch())  + "," + Float.toString(mFrameBuffer.getRoll()) + ",";
        cameraFrame = inputFrame.rgba();
        frameNum++;
        filename = File.separator + timeStamp + "_frame_" + frameNum + ".png";
        mediaFile = new File(mediaStorageDir.getPath(),filename);
        filename = mediaFile.toString();
        if (Imgcodecs.imwrite(filename, cameraFrame))
            Log.i(TAG, "Wrote file to " + filename);
        else
            Log.i(TAG, "FAILED " + filename);


        try {
            writer.append(gyro);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFrameBuffer.putFrame(cameraFrame, mRotationalSensor.getMRotationMatrix());
        return cameraFrame;
    }


    class RotationalSensor implements SensorEventListener {

        private Sensor mRotationVectorSensor;
        private final float[] mRotationMatrix = new float[16];

        public RotationalSensor() {
            Log.i(TAG, "RotationalSensor Initialized");
            mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mRotationMatrix[ 0] = 1;
            mRotationMatrix[4] = 1;
            mRotationMatrix[8] = 1;
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
                SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public float[] getMRotationMatrix(){
            return mRotationMatrix;
        }
    }
}
