package comdbewselectronic_swim_coach.httpsgithub.electronic_swim_coach;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs; // imread, imwrite, etc
import java.io.PrintWriter;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;   // VideoCapture


public class tutorial1 extends Activity  {
	private Mat img_bw;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial1);
		img_bw = null;
		try {
			String path = "\\mnt\\sdcard\\car.jpg";
			//Mat im_gray = new Mat();

			Mat im_raw = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_COLOR);   // Read the file
			if(im_raw.empty()) Log.i("OpenFile", "OpenFile FAILED");
			else Log.i("OpenFile", "OpenFile  SUCCESS");



			//crop 2 horizontal secions of the image and combine them together
			Rect roi = new Rect(0, 0, im_raw.width(), im_raw.height()/10);
			Mat im_rgb1 = new Mat(im_raw, roi);

			roi.y = im_raw.height()/2;
			//Rect roi2 = new Rect(0, im_raw.height()/2, im_raw.width(), im_raw.height()/10);
			Mat im_rgb2 = new Mat(im_raw, roi);
			Mat im_rgb = new Mat(im_rgb1.width(),im_rgb1.height()*2, CvType.CV_8UC4);
			List<Mat> matList = new ArrayList<Mat>();
			matList.add(im_rgb1);
			matList.add(im_rgb2);
			Core.vconcat(matList, im_rgb); //im_rgb is the cropped concatenated image to be processes

			Mat im_gray = new Mat(im_rgb.size(), CvType.CV_8UC1, new Scalar(0));
			//Mat im_gray = imread("image.jpg",CV_LOAD_IMAGE_GRAYSCALE);   //load the image directly into grayscale

			//**ZEROING RED AND GREEN CHANNELS AND CONVERT TO GRAYSCLALE BASED ON ONLY THE BLUE CHANNEL**
	//		List<Mat> lRgb = new ArrayList<Mat>(3);
	//		Core.split(im_rgb,lRgb );
	//		Mat im_gray = lRgb.get(2);

			Imgproc.cvtColor(im_rgb, im_gray, Imgproc.COLOR_BGR2GRAY);

			org.opencv.core.Size s = new Size(5,5);
			Imgproc.GaussianBlur(im_gray, im_gray, s, 0);


			Imgproc.threshold(im_gray, img_bw, 127, 255, Imgproc.THRESH_OTSU);

			//Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size (3,3));
			//Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5));
			//Imgproc.erode(img_bw,img_bw,kernelErode);
			//Imgproc.dilate(img_bw, img_bw, kernelDilate);
			Mat kernelClose = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5));
			Imgproc.morphologyEx(img_bw, img_bw, Imgproc.MORPH_CLOSE, kernelClose);  //dillation followed by erosion

			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Mat hierarchy = new Mat();
			//Mat hierarchy = new Mat(200, 200, CvType.CV_8UC1, new Scalar(0));

			Imgproc.findContours(img_bw, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
			int largest_area=0;
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

			//do end of lane and side-to-side detection
			//divide Mat into horizontal strips, if any of the strips are all white, warn swimmer he is at the end of the lane

			//LOOK INTO ITERATING OVER HALVES OF THE MATRIX, KEEPING 2 SEPERATE IMAGES THE WHOLE WAY, OR SPLITTING AND MERGIN MATS -- http://stackoverflow.com/questions/22445108/splitting-merging-matrices-in-opencv
			for(int row = 0; row < img_bw.rows(); ++row) {
				for(int col = 0; col < img_bw.cols(); ++col) {
					//points to each pixel value in turn assuming a CV_8UC1 greyscale image
				}
			}
			org.opencv.core.Core.countNonZero(img_bw); //COUNT ALL THE WHILE PIXELS IN A BINARY IMAGE






			Imgcodecs.imwrite("\\mnt\\sdcard\\filtererd.jpg", img_bw); //save processed image

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
