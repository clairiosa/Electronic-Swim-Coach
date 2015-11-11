package com.bewmens.ElectronicSwimCoach;

import android.content.Context;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave on 2015-11-10.
 */
public class ImageProcessingThread implements Runnable {

    private Mat frame;
    public ImageProcessingThread(Mat frame){
        this.frame = frame;
    }

    @Override
    public void run() {
        org.opencv.core.Size s = new Size(5,5);
        Imgproc.GaussianBlur(frame, frame, s, 0);

        //Mat img_bw = null;
        Imgproc.threshold(frame, frame, 127, 255, Imgproc.THRESH_OTSU);

        Mat kernelClose = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5));
        Imgproc.morphologyEx(frame, frame, Imgproc.MORPH_CLOSE, kernelClose);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        //Mat hierarchy = new Mat(200, 200, CvType.CV_8UC1, new Scalar(0));

        Imgproc.findContours(frame, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        int largest_area=0;
//			pw.println("Error finding contours");
        int largest_contour_index=0;
        for( int i = 0; i< contours.size(); i++ ) // iterate through each contour.
        {
            int a=(int)Imgproc.contourArea(contours.get(i), false);  //  Find the area of contour
            if(a>largest_area){
                largest_area=a;
                largest_contour_index=i;                //Store the index of largest contour
            }
        }
        Scalar color = new Scalar( 255,255,255);
        Imgproc.drawContours(frame, contours, largest_contour_index, color);
    }
}
