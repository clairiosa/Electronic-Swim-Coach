package com.bewmens.ElectronicSwimCoach;

import android.hardware.SensorManager;

import org.opencv.core.Mat;

/**
 * Created by Dave on 2015-11-11.
 */
public class FrameBuffer {

    private static final int SIZE_OF_FRAME_BUFFER = 20;

    private int frameBufferIterator;

    private Mat[] frame;
    private float[][] orientation;
    private float[][] rotationalMatrix;

    public FrameBuffer() {
        frameBufferIterator = 0;
        frame = new Mat[SIZE_OF_FRAME_BUFFER];
        orientation = new float[SIZE_OF_FRAME_BUFFER][3];
        rotationalMatrix = new float[SIZE_OF_FRAME_BUFFER][16];
    }

    public void putFrame(Mat newFrame, float[] newRotationalMatrix) {
        this.frame[frameBufferIterator] = newFrame;
        this.rotationalMatrix[frameBufferIterator] = newRotationalMatrix;

        SensorManager.getOrientation(rotationalMatrix[frameBufferIterator], orientation[frameBufferIterator]);

        frameBufferIterator = (frameBufferIterator >= SIZE_OF_FRAME_BUFFER-1) ? 0 : frameBufferIterator + 1;
    }

    public Mat getMat(int specificFrame) {
        return frame[specificFrame];
    }

    public float[] getOrientation(int specificFrame) {
        return SensorManager.getOrientation(rotationalMatrix[specificFrame], orientation[specificFrame]);
    }

    public float getAzimuth(int specificFrame){
        return orientation[specificFrame][0];
    }

    public float getPitch(int specificFrame){
        return orientation[specificFrame][1];
    }

    public float getRoll(int specificFrame){
        return orientation[specificFrame][2];
    }

    public float[] getQuaternions(int specificFrame){
        return rotationalMatrix[specificFrame];
    }

    public Mat getMat() {
        return frame[(frameBufferIterator == 0)? SIZE_OF_FRAME_BUFFER-1 : frameBufferIterator - 1];
    }

    public float[] getOrientation() {
        return SensorManager.getOrientation(rotationalMatrix[frameBufferIterator-1], orientation[frameBufferIterator-1]);
    }

    public float getAzimuth(){
        return orientation[(frameBufferIterator == 0)? SIZE_OF_FRAME_BUFFER-1 : frameBufferIterator - 1][0];
    }

    public float getPitch(){
        return orientation[(frameBufferIterator == 0)? SIZE_OF_FRAME_BUFFER-1 : frameBufferIterator - 1][1];
    }

    public float getRoll(){
        return orientation[(frameBufferIterator == 0)? SIZE_OF_FRAME_BUFFER-1 : frameBufferIterator - 1][2];
    }

    public float[] getQuaternions(){
        return rotationalMatrix[(frameBufferIterator == 0)? SIZE_OF_FRAME_BUFFER-1 : frameBufferIterator - 1];
    }
}
