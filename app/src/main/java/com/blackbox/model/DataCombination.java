package com.blackbox.model;

import java.util.ArrayList;
import java.util.List;

public class DataCombination {

    public static List<pointData> combineData(float[][] data, long[] labels) {
        List<pointData> dataToSend = new ArrayList<>();
        int length = data.length;

        for (int i = 0; i < length; i++) {
            float f1 = data[i][0];
            float f2 = data[i][1];
            long lab = labels[i];

            dataToSend.add(new pointData(f1, f2, lab));
        }
        return dataToSend;
    }

}
