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
    }
}
