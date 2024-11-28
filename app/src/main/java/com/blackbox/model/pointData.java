package com.blackbox.model;

public class pointData {

    private final float feature1;
    private final float feature2;
    private final long label;

    public pointData(float f1, float f2, long lab) {
        this.feature1 = f1;
        this.feature2 = f2;
        this.label = lab;
    }

    public float getFeature1() {
        return this.feature1;
    }

    public float getFeature2() {
        return this.feature2;
    }

    public long getLabel() {
        return this.label;
    }
}
