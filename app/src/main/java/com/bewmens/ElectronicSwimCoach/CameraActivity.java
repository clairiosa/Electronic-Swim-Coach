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
import org.opencv.core.Rect;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import org.opencv.core.Point;
import org.opencv.core.Core;
import org.opencv.core.MatOfPoint;

/**
 * Created by Dave on 2015-11-09.
 */
public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private FrameBuffer mFrameBuffer;

    private CameraBridgeViewBase mOpenCvCameraView;
    private SensorManager mSensorManager;
    private RotationalSensor mRotationalSensor;

    private TextView tvAzimuth;
    private TextView tvPitch;
    private TextView tvRoll;

    private static final String TAG = "ESC::CameraActivity";
    private MatOfPoint largestContour;
    private Point centroid;


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

        //later we can optimize by only looking at a third of the image rather then the whole image
        Rect roi = new Rect(0, 0, greyFrame.width(), greyFrame.height()/4);
        Mat greyFrame1 = new Mat(greyFrame, roi);

        //Rect roi2 = new Rect(0, im_raw.height()/2, im_raw.width(), im_raw.height()/10);
        roi.y = greyFrame.height()/4;
        Mat greyFrame2 = new Mat(greyFrame, roi);

        roi.y = greyFrame.height()/2;
        Mat greyFrame3 = new Mat(greyFrame, roi);

        roi.y = (3*greyFrame.height())/4;
        Mat greyFrame4 = new Mat(greyFrame, roi);


        Thread thread1 = new Thread(new ImageProcessingThread(greyFrame1));
        thread1.start();

        Thread thread2 = new Thread(new ImageProcessingThread(greyFrame2));
        thread2.start();

        Thread thread3 = new Thread(new ImageProcessingThread(greyFrame3));
        thread3.start();

        Thread thread4 = new Thread(new ImageProcessingThread(greyFrame4));
        thread4.start();



      //  Mat greyFrame = inputFrame.gray();
      //  Thread thread = new Thread(new ImageProcessingThread(greyFrame));
      //  thread.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String azimuth=Float.toString(mFrameBuffer.getAzimuth());
                String pitch=Float.toString(mFrameBuffer.getPitch());
                String roll=Float.toString(mFrameBuffer.getRoll());

                tvAzimuth.setText(azimuth);
                tvPitch.setText(pitch);
                tvRoll.setText(roll);
            }
        });


        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        List<Mat> matList = new ArrayList<Mat>();
        Mat img_bin = new Mat(greyFrame.size(),org.opencv.core.CvType.CV_8UC1);  //NOT SURE IF WILL WORK, MIGHT TO TO INIT PROPERLY
        matList.add(greyFrame1);
        matList.add(greyFrame2);
        matList.add(greyFrame3);
        matList.add(greyFrame4);
        Core.vconcat(matList, img_bin); //img_bin is the cropped, concatenated and processed image
        img_bin = BinaryContourDetection(img_bin);  //contour detection



//        img_bin = centroidDetection(img_bin); //centroid detection

/*        //DO END OF LANE DETECTION AND SIDE TO SIDE DETECTION BASED ON "img_bin" *****MAKE THEM DO HISTORICAL ANALYSIS BEFORE VIBRATING
        if(centroid.x < (img_bin.width()/5)){
            //vibrate left vibrator
        }
        if(centroid.x > (4*img_bin.width())/5){
            //vibrate right vibrator
        }
        if(centroid.y < (img_bin.height()/5)){
            //pulse both vibrators (end of lane)
        }
*/

 //       mFrameBuffer.putFrame(greyFrame, mRotationalSensor.getMRotationMatrix());
 //       return greyFrame;

       mFrameBuffer.putFrame(img_bin, mRotationalSensor.getMRotationMatrix());
       return img_bin;
    }


    public Mat BinaryContourDetection(Mat frame) {
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        //Mat hierarchy = new Mat(200, 200, CvType.CV_8UC1, new Scalar(0));

        Imgproc.findContours(frame, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        int largest_area = 0;
        int largest_contour_index = 0;
        for (int i = 0; i < contours.size(); i++) // iterate through each contour.
        {
            int a = (int) Imgproc.contourArea(contours.get(i), false);  //  Find the area of contour
            if (a > largest_area) {
                largest_area = a;
                largest_contour_index = i;                //Store the index of largest contour
            }
        }
        Scalar color = new Scalar(255, 255, 255);
        largestContour = contours.get(largest_contour_index);
        Imgproc.drawContours(frame, contours, largest_contour_index, color);
        return frame;
    }


    public Mat centroidDetection (Mat frame){
        //OPENCV MOMENTS BUG THAT IS GOING TO BE FIXED ON 3.1
 //       Moments center = Imgproc.moments(largestContour, false);
 //       centroid = new Point(center.m10 / center.m00, center.m01 / center.m00);   //get centroid of frame
 //       Imgproc.circle(frame, centroid, 4, new Scalar(0, 255, 0, 0), -1, 8, 0);  //draw circle around centroid
        return frame;
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
