package com.blackbox.model;

import java.util.Optional;

public class PointData {
    public final float[] features;
    public final long label;

    public PointData(float[] features, long label) {
        this.features = features;
        this.label = label;
    }

    /**
     * Try and get a value.
     * The array itself is available, but this is useful for not assuming the size.
     */
    public Optional<Float> getValue(int index) {
        if (index < features.length) {
            return Optional.of(features[index]);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(label + " : ");
        for (int i = 0; i < features.length; i++) {
            output.append(features[i]);
            if (i != features.length - 1) {
                output.append(", ");
            }
        }
        return output.toString();
    }
}
