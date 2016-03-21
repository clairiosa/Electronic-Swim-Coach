package com.bewmens.ElectronicSwimCoach;

import com.bewmens.ElectronicSwimCoach.ImageProcessingThread;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import org.opencv.core.Point;
import org.opencv.core.Core;
import org.opencv.core.MatOfPoint;

/**
 * Created by Dave on 2015-11-09.
 */
public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, TextToSpeech.OnInitListener {

    private FrameBuffer mFrameBuffer;

    private CameraBridgeViewBase mOpenCvCameraView;
    private SensorManager mSensorManager;
    private RotationalSensor mRotationalSensor;

    private TextView tvAzimuth;
    private TextView tvPitch;
    private TextView tvRoll;

    private TextToSpeech mEngine;

    private int maxFrameX = 600;
    private int maxFrameY = 360;

    private int[] eol = {0,0,0};

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
        mOpenCvCameraView.setMaxFrameSize(maxFrameX, maxFrameY);
        mOpenCvCameraView.setCvCameraViewListener(this);

        tvAzimuth = (TextView)findViewById(R.id.textViewZ);
        tvRoll = (TextView)findViewById(R.id.textViewY);
        tvPitch = (TextView)findViewById(R.id.textViewX);

        mRotationalSensor.start();

        mFrameBuffer = new FrameBuffer();
        mEngine = new TextToSpeech(this, this);
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
        //**ZEROING RED AND GREEN CHANNELS AND CONVERT TO GRAYSCLALE BASED ON ONLY THE BLUE CHANNEL**
        //		List<Mat> lRgb = new ArrayList<Mat>(3);
        //		Core.split(inputFrame,lRgb );
        //		Mat greyFrame = lRgb.get(2);

        Mat greyFrame = inputFrame.gray();
        Mat colorFrame = inputFrame.rgba();


        Thread thread = new Thread(new ImageProcessingThread(greyFrame, colorFrame, eol, maxFrameX, maxFrameY));
        thread.start();



        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String azimuth = Float.toString(mFrameBuffer.getAzimuth());
                String pitch = Float.toString(mFrameBuffer.getPitch());
                String roll = Float.toString(mFrameBuffer.getRoll());

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


        Log.d("!!!!!!!!", "eol " + eol[0]);
        if ((eol[0] % 10  == 2) && Math.abs(mFrameBuffer.getRoll()) < 0.2 && Math.abs(mFrameBuffer.getPitch()) < 0.2) {
            speakEnd();
        }
        if (mFrameBuffer.getRoll() == 0 && mFrameBuffer.getPitch() ==0 && mFrameBuffer.getAzimuth() == 0) {
            if ((eol[1] % 10 == 2) && Math.abs(mFrameBuffer.getRoll()) < 0.2 && Math.abs(mFrameBuffer.getPitch()) < 0.2) {
                speakLeft();
            } else if ((eol[2] % 10 == 2) && Math.abs(mFrameBuffer.getRoll()) < 0.2 && Math.abs(mFrameBuffer.getPitch()) < 0.2) {
                speakRight();
            }
        }

        mFrameBuffer.putFrame(colorFrame, mRotationalSensor.getMRotationMatrix());
        return colorFrame;
    }

    @Override
    public void onInit(int status) {
        Log.d("Speech", "OnInit - Status [" + status + "]");

        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speech", "Success!");
            mEngine.setLanguage(Locale.UK);
        }
    }

    private void speakEnd() {
        mEngine.speak("End of lane.", TextToSpeech.QUEUE_FLUSH, null, null);
    }
    private void speakLeft() {
        mEngine.speak("Left.", TextToSpeech.QUEUE_FLUSH, null, null);
    }
    private void speakRight() {
        mEngine.speak("Right.", TextToSpeech.QUEUE_FLUSH, null, null);
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
