package com.bewmens.ElectronicSwimCoach;

import android.hardware.SensorManager;

import org.opencv.core.Mat;

/**
 * Created by Dave on 2015-11-11.
 */
public class FrameBuffer {

    private static final int SIZE_OF_FRAME_BUFFER = 20;

    private int mFrameBufferIterator;

    private Mat[] mFrame;
    private float[][] mOrientation;
    private float[][] mRotationalMatrix;

    public FrameBuffer() {
        mFrameBufferIterator = 0;
        mFrame = new Mat[SIZE_OF_FRAME_BUFFER];
        mOrientation = new float[SIZE_OF_FRAME_BUFFER][3];
        mRotationalMatrix = new float[SIZE_OF_FRAME_BUFFER][16];
    }

    public void putFrame(Mat newFrame, float[] newRotationalMatrix) {
        this.mFrame[mFrameBufferIterator] = newFrame;
        this.mRotationalMatrix[mFrameBufferIterator] = newRotationalMatrix;

        SensorManager.getOrientation(mRotationalMatrix[mFrameBufferIterator], mOrientation[mFrameBufferIterator]);

        mFrameBufferIterator = (mFrameBufferIterator >= SIZE_OF_FRAME_BUFFER-1) ? 0 : mFrameBufferIterator + 1;
    }

    public Mat getMat(int specificFrame) {
        return mFrame[specificFrame];
    }

    public float[] getOrientation(int specificFrame) {
        return SensorManager.getOrientation(mRotationalMatrix[specificFrame], mOrientation[specificFrame]);
    }

    public float getAzimuth(int specificFrame){
        return mOrientation[specificFrame][0];
    }

    public float getPitch(int specificFrame){
        return mOrientation[specificFrame][1];
    }

    public float getRoll(int specificFrame){
        return mOrientation[specificFrame][2];
    }

    public float[] getQuaternions(int specificFrame){
        return mRotationalMatrix[specificFrame];
    }

    public Mat getMat() {
        return mFrame[(mFrameBufferIterator == 0)? SIZE_OF_FRAME_BUFFER-1 : mFrameBufferIterator - 1];
    }

    public float[] getOrientation() {
        return SensorManager.getOrientation(mRotationalMatrix[mFrameBufferIterator-1], mOrientation[mFrameBufferIterator-1]);
    }

    public float getAzimuth(){
        return mOrientation[(mFrameBufferIterator == 0)? SIZE_OF_FRAME_BUFFER-1 : mFrameBufferIterator - 1][0];
    }

    public float getPitch(){
        return mOrientation[(mFrameBufferIterator == 0)? SIZE_OF_FRAME_BUFFER-1 : mFrameBufferIterator - 1][1];
    }

    public float getRoll(){
        return mOrientation[(mFrameBufferIterator == 0)? SIZE_OF_FRAME_BUFFER-1 : mFrameBufferIterator - 1][2];
    }

    public float[] getQuaternions(){
        return mRotationalMatrix[(mFrameBufferIterator == 0)? SIZE_OF_FRAME_BUFFER-1 : mFrameBufferIterator - 1];
    }
}
