package com.bewmens.ElectronicSwimCoach;

import android.content.Context;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
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
    private Mat colorFrame;
    public ImageProcessingThread(Mat frame, Mat colorFrame){
        this.frame = frame;
        this.colorFrame = colorFrame;
    }

    @Override
    public void run() {
        org.opencv.core.Size s = new Size(5,5);
        Imgproc.GaussianBlur(frame, frame, s, 0);

        //Mat img_bw = null;
        Imgproc.threshold(frame, frame, 127, 255, Imgproc.THRESH_OTSU);

        Mat kernelClose = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5));
        Imgproc.morphologyEx(frame, frame, Imgproc.MORPH_CLOSE, kernelClose);

        Imgproc.Canny(frame, frame, 80, 100);

        Mat lines = new Mat(frame.size(),org.opencv.core.CvType.CV_8UC1);

        int threshold = 50;
        int minLineSize = 20;
        int lineGap = 20;

        Imgproc.HoughLinesP(frame, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);

        Log.i("~~~~~~~~~~~", lines.cols() + " " + lines.rows());

        double[][] vectorLines = new double[lines.rows()][4];
        double maxLen = 0;
        int maxLenPoint = 0;
        for (int x = 0; x < lines.rows(); x++) {
            Log.i("~~~~~~~~~~~", "Loop start");
            vectorLines[x] = lines.get(x, 0);

            double x1 = vectorLines[x][0],
                    y1 = vectorLines[x][1],
                    x2 = vectorLines[x][2],
                    y2 = vectorLines[x][3];

            Log.i("~~~~~~~~~~~", "Length calculation");
            double len = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

            Log.i("~~~~~~~~~~~", "Max Length against " + len);
            if (len > maxLen && !(((y1 == y2) && (y1 == 479)) || ((x1 == x2) && (x1 == 799)))) {
                Log.i("~~~~~~~~~~~", "Success");
                maxLen = len;
                maxLenPoint = x;
            }
        }
        Log.i("~~~~~~~~~~~", "Drawing");
        for (int x = 0; x < lines.rows(); x++) {

            double x1 = vectorLines[x][0],
                    y1 = vectorLines[x][1],
                    x2 = vectorLines[x][2],
                    y2 = vectorLines[x][3];

            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);


            Log.i("~~~~~~~~~~~", x1 + " " + y1 + " " + x2 + " " + y2);
            if (maxLenPoint == x) {
                Imgproc.line(colorFrame, start, end, new Scalar(0,255,255), 3);
            } else {
                Imgproc.line(colorFrame, start, end, new Scalar(255, 0, 0), 3);
            }

        }
    }
}
