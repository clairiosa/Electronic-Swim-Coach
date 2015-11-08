package com.bewmens.ElectronicSwimCoach;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs; // imread, imwrite, etc


public class tutorial1 extends Activity  {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial1);

		try {
			String path = "C:\\Users\\Dave\\midterm.jpg";
			//Mat im_gray = new Mat();

			Mat im_rgb = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_COLOR);   // Read the file
			if(im_rgb.empty()) Log.i("OpenFile", "OpenFile FAILED");
			else Log.i("OpenFile", "OpenFile  SUCCESS");
//			 Log.i("Error reading file");
			Mat im_gray = new Mat(im_rgb.size(), CvType.CV_8UC1, new Scalar(0));
			//Mat im_gray = imread("image.jpg",CV_LOAD_IMAGE_GRAYSCALE);   //load the image directly into grayscale
//			pw.println("Error creating grayscale matrix");
			Imgproc.cvtColor(im_rgb, im_gray, Imgproc.COLOR_BGR2GRAY);
//			pw.println("Error grayscale");
			//**ATTEMPT ZEROING RED AND GREEN CHANELS AND CONVERT TO GRAYSCLALE BASED ON ONLY THE BLUE CHANNEL**
			org.opencv.core.Size s = new Size(5,5);
			Imgproc.GaussianBlur(im_gray, im_gray, s, 0);
//			pw.println("Error blur");
			Mat img_bw = null;
			Imgproc.threshold(im_gray, img_bw, 127, 255, Imgproc.THRESH_OTSU);
//			pw.println("Error threshold");
			//Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size (3,3));
			//Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5));
			//Imgproc.erode(img_bw,img_bw,kernelErode);
			//Imgproc.dilate(img_bw, img_bw, kernelDilate);
			Mat kernelClose = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5));
			Imgproc.morphologyEx(img_bw, img_bw, Imgproc.MORPH_CLOSE, kernelClose);  //dillation followed by erosion
//			pw.println("Error morph");
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Mat hierarchy = new Mat();
			//Mat hierarchy = new Mat(200, 200, CvType.CV_8UC1, new Scalar(0));

			Imgproc.findContours(img_bw, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
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
			Imgproc.drawContours(img_bw, contours, largest_contour_index, color);
//			pw.println("Error whiting contours");
			Imgcodecs.imwrite("C:\\Users\\JonnyOommen\\Desktop\\filtererd.jpg", img_bw);
//			pw.println("Error writing file");
//			pw.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

/*
	public static List<Blob> findBlobs(Mat rgbaFrame, Scalar color,
									   Scalar colorTolerance, Integer areaThreshold) {
		// blur image
		Mat mPyrDown = new Mat();
		Imgproc.pyrDown(rgbaFrame, mPyrDown);
		Imgproc.pyrDown(mPyrDown, mPyrDown);

		// get HSV
		Mat mHsv = new Mat();
		Imgproc.cvtColor(mPyrDown, mHsv, Imgproc.COLOR_RGB2HSV_FULL);
		mPyrDown.release();

		// calc lower / upper color boundaries
		Scalar lower = new Scalar(color.val[0] - colorTolerance.val[0],
				color.val[1] - colorTolerance.val[1], color.val[2]
				- colorTolerance.val[2]);

		Scalar upper = new Scalar(color.val[0] + colorTolerance.val[0],
				color.val[1] + colorTolerance.val[1], color.val[2]
				+ colorTolerance.val[2]);

		// calc threshold
		Mat mMask = new Mat();
		Core.inRange(mHsv, lower, upper, mMask);
		mHsv.release();

		// dilates
		Mat mDilate = new Mat();
		Imgproc.dilate(mMask, mDilate, new Mat());

		// get contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(mDilate, contours, new Mat(),
				Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		mDilate.release();

		// fill blobs
		List<Blob> blobs = new ArrayList<Blob>();
		for (MatOfPoint m : contours) {
			Core.multiply(m, new Scalar(4, 4), m);
			Blob b = new Blob(m, color);
			if (b.getArea() >= areaThreshold)
				blobs.add(b);
		}
		Collections.sort(blobs, new Blob.compareArea());
		Collections.reverse(blobs);

		return blobs;
	}
	*/

}
