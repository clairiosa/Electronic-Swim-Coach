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
    Mat lines;
    int threshold;
    int minLineSize;
    int lineGap;
    double[][] vectorLines;
    double maxLen;
    int maxLenPoint;
    double len;
    double x1, x2, y1, y2;
    Point start;
    Point end;

    int maxX;
    int maxY;

    int right;
    int left;

    int[] eol;

    boolean endOfLine = false;

    public ImageProcessingThread(Mat frame, Mat colorFrame, int[] eol, int maxX, int maxY){
        this.frame = frame;
        this.colorFrame = colorFrame;

        this.maxX = maxX;
        this.maxY = maxY;

        this.eol = eol;
    }

    @Override
    public void run() {
        org.opencv.core.Size s = new Size(5,5);
        Imgproc.GaussianBlur(frame, frame, s, 0);  //2-3 frames

        Imgproc.threshold(frame, frame, 127, 255, Imgproc.THRESH_OTSU); // neg



        Mat kernelClose = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5));
        Imgproc.morphologyEx(frame, frame, Imgproc.MORPH_CLOSE, kernelClose);


        Imgproc.Canny(frame, frame, 80, 100); //6-8 frames
        lines = new Mat(frame.size(),org.opencv.core.CvType.CV_8UC1);

        threshold = 100;
        minLineSize = 100;
        lineGap = 50;

        Imgproc.HoughLinesP(frame, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);

        //Log.i("~~~~~~~~~~~", lines.cols() + " " + lines.rows());

        vectorLines = new double[lines.rows()][4];
        maxLen = 0;
        maxLenPoint = 0;

        double maxRight = 0;
        double maxLeft = 288;


        //Log.i("~~~~~~~~~~~", "Loop start");
        for (int x = 0; x < lines.rows(); x++) {
            vectorLines[x] = lines.get(x, 0);

             x1 = vectorLines[x][0];
                    y1 = vectorLines[x][1];
                    x2 = vectorLines[x][2];
                    y2 = vectorLines[x][3];

            if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {

                Log.i("~~~~~~~~~~~", "x1:"+ x1 + " y1:" + y1 + " x1:" + x2 + " y2:" + y2);
                //Log.i("~~~~~~~~~~~", "Length calculation");
                len = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

                //Log.i("~~~~~~~~~~~", "Max Length against " + len);
                if (len > maxLen) {
                    if (!(((y1 == y2) && (y1 >= 283 || y1 <= 5)) || ((x1 == x2) && (x1 >= 595 || x1 <= 5)))) {
                        //Log.i("~~~~~~~~~~~", "Success");
                        maxLen = len;
                        maxLenPoint = x;
                    }
                }
            }
            if (y1 > maxRight && (y1 <= 283)) maxRight = y1;
            if (y2 > maxRight && (y2 <= 283)) maxRight = y2;
            if (y1 < maxLeft && (y1 >= 5)) maxLeft = y1;
            if (y2 < maxLeft && (y2 >= 5)) maxLeft = y2;
        }
        //Log.i("~~~~~~~~~~~", "Drawing");

        for (int x = 0; x < lines.rows(); x++) {

            x1 = vectorLines[x][0];
            y1 = vectorLines[x][1];
            x2 = vectorLines[x][2];
            y2 = vectorLines[x][3];

            start = new Point(x1, y1);
            end = new Point(x2, y2);

            if (!(((y1 == y2) && (y1 >= 283 || y1 <= 5)) || ((x1 == x2) && (x1 >= 595 || x1 <= 5)))) {
                if (maxLenPoint == x && (Math.abs(y2 - y1) > Math.abs(x2 - x1))) {
                    //Log.i("~~~~~~~~~~~", x1 + " " + y1 + " " + x2 + " " + y2);
                    Imgproc.line(colorFrame, start, end, new Scalar(255, 0, 0), 2);
                    endOfLine = true;
                } else {
                    Imgproc.line(colorFrame, start, end, new Scalar(0, 255, 0), 2);
                }
            }
        }

        if (endOfLine == true) {
            eol[0]++;
        } else eol[0] = 0;
        Log.i("~~~~~~~~~~~", "maxRight" + maxRight + " maxLeft" + maxLeft);
        if (maxRight < 90){
            eol[2]++;
        } else eol[2] = 0;

        if (maxLeft > (210)) {
            eol[1]++;
        } else eol[1] = 0;
    }
}
