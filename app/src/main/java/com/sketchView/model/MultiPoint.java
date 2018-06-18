package com.sketchView.model;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * MultiYPoint holds two integer coordinates
 */
public class MultiPoint<T1, T2> {

    private boolean isMultiY;
    private int point;
    private ArrayList<Integer> points;

    public static MultiPoint<Integer, ArrayList<Integer>> createMultiYPoint (int x) {
        return new MultiPoint<>(x, true);
    }

    public static MultiPoint<ArrayList<Integer>, Integer> createMultiXPoint (int y) {
        return new MultiPoint<>(y, false);
    }

    public MultiPoint(boolean isMultiY) {
        this.isMultiY = isMultiY;
        points = new ArrayList<>();
    }

    public MultiPoint(int p, boolean isMultiY) {
        this(isMultiY);
        point = p;
    }

    /**
     * Set the MultiYPoint's x and y coordinates
     */
    public void set(int p, ArrayList<Integer> ps) {
        point = p;
        points.addAll(ps);
    }

    public void set(int p, Integer[] ps) {
        set(p, (ArrayList<Integer>) Arrays.asList(ps));
    }

    public void setPoint(int p) {
        point = p;
    }

    public void setPoints(ArrayList<Integer> ps) {
        points.addAll(ps);
    }

    public void setPoints(Integer[] ps) {
        points.addAll(Arrays.asList(ps));
    }

    public void add(int p) {
        points.add(p);
    }

    public Integer getPoint() {
        return point;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }

    public ArrayList<Point> getPairs() {
        ArrayList<Point> pairs = new ArrayList<>();
        for(Integer p: points) {
            if (isMultiY) {
                pairs.add(new Point(point, p));
            } else {
                pairs.add(new Point(p, point));
            }
        }
        return pairs;
    }
}