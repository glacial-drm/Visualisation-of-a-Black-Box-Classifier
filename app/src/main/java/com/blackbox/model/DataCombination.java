package com.blackbox.model;

import java.util.ArrayList;
import java.util.List;

public class DataCombination {

    public static List<PointData> combineData(float[][] data, long[] labels, int dimensions) {
        List<PointData> dataToSend = new ArrayList<>();
        int length = data.length;

        for (int i = 0; i < length; i++) {
            float f1 = data[i][0];
            float f2 = data[i][1];
            float f3 = 0;
            //if it is data for 3D, set the third feature to that value
            if (dimensions == 3){
                f3 = data[i][2];
            }
            long lab = labels[i];

            dataToSend.add(new PointData(f1, f2, f3, lab));
        }
        return dataToSend;
    }

}
