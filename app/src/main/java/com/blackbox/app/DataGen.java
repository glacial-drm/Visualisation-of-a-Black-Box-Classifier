package com.blackbox.app;

import java.util.Random;

import com.blackbox.model.PointData;

public class DataGen {
    private static Random rng = new Random();

    public static PointData[] genCircle(int count, float range, double radius) {
        double radius_2 = Math.pow(radius, 2);
        PointData[] data = new PointData[count];

        for (int i = 0; i < count; i++) {
            float x = rng.nextFloat(-range, range);
            float y = rng.nextFloat(-range, range);
            int label = Math.pow((double)x, 2) + Math.pow((double)y, 2) >= radius_2 ? 0 : 1;

            data[i] = new PointData(x, y, label);
        }

        return data;
    }
}
