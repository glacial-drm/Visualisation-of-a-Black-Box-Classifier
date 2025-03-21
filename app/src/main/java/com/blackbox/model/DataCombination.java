package com.blackbox.model;

import java.util.ArrayList;
import java.util.List;

public class DataCombination {

    public static List<PointData> combineData(float[][] data, long[] labels) {
        List<PointData> dataToSend = new ArrayList<>();
        int length = data.length;

        for (int i = 0; i < length; i++) {
            float[] features = data[i];
            long lab = labels[i];

            dataToSend.add(new PointData(features, lab));
        }
        return dataToSend;
    }

}
