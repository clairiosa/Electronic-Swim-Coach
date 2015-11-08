package com.bewmens.ElectronicSwimCoach;

/**
 * Created by JonnyOommen on 16/10/2015.
 */
import java.util.Comparator;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;



public class Blob {

    // -------------------------------------------------- BASIC MEMBERS
    private Rect box = null;
    private Scalar color = null;

    public Rect getBox() {
        return box;
    }

    public Scalar getColor() {
        return color;
    }

    // -------------------------------------------------- DYNAMIC MEMBERS
    private Integer area = null;
    private Point center = null;
    private Point contact = null;

    /**
     * calculates the blobs area upon call.
     *
     * @return blob area
     */
    public Integer getArea() {
        if (area == null)
            area = box.height * box.width;

        return area;
    }

    /**
     * calculates the blobs center of mass upon call.
     *
     * @return center of mass
     */
    public Point getCenter() {
        if (center == null)
            center = new Point(box.x + box.width / 2, box.y + box.height / 2);

        return center;
    }

    /**
     * calculates the blobs contact point upon call.
     *
     * @return contact point
     */
    public Point getContact() {
        if (contact == null)
            contact = new Point(box.x + box.width / 2, box.y + box.height);

        return contact;
    }




    // -------------------------------------------------- CONSTRUCTOR
    public Blob(MatOfPoint contour, Scalar color) {
        this(Imgproc.boundingRect(contour), color);
    }

    public Blob(Rect box, Scalar color) {
        this.color = color;
        this.box = box;
    }


    // -------------------------------------------------- COMPARATORS
    public static class compareArea implements Comparator<Blob> {
        @Override
        public int compare(Blob arg0, Blob arg1) {
            return arg0.getArea().compareTo(arg1.getArea());
        }
    }
}
