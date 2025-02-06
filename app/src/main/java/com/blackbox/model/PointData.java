package com.blackbox.model;

public class PointData {

    public final float feature1;
    public final float feature2;
    public final long label;

    public PointData(float f1, float f2, long lab) {
        this.feature1 = f1;
        this.feature2 = f2;
        this.label = lab;
    }

    @Override
    public String toString() {
        return label + " : " + feature1 + ", " + feature2;
    }
}
