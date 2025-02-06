package com.blackbox.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.blackbox.model.PointData;

public class DataGen {
    private static Random rng = new Random();

    private static float[] genQuadrant(float range, float[] offsets) {
        float x = rng.nextFloat(-range / 2f, range / 2f) + offsets[0];
        float y = rng.nextFloat(-range / 2f, range / 2f) + offsets[1];
        return new float[] { x, y };
    }

    public static PointData[] genCircle(
        int count, float range, double radius,
        boolean tl, boolean tr, boolean bl, boolean br
    ) {
        double radius_2 = Math.pow(radius, 2);
        PointData[] data = new PointData[count];

        List<float[]> quadrantOffsets = new ArrayList<>();
        if (tl) { quadrantOffsets.add(new float[] { -range / 2f, range / 2f }); }
        if (tr) { quadrantOffsets.add(new float[] { range / 2f, range / 2f }); }
        if (bl) { quadrantOffsets.add(new float[] { -range / 2f, -range / 2f }); }
        if (br) { quadrantOffsets.add(new float[] { range / 2f, -range / 2f }); }

        for (int i = 0; i < count; i++) {
            float[] point = genQuadrant(range, quadrantOffsets.get(rng.nextInt(quadrantOffsets.size())));
            int label = Math.pow((double)point[0], 2) + Math.pow((double)point[1], 2) >= radius_2 ? 0 : 1;

            data[i] = new PointData(point[0], point[1], label);
        }

        return data;
    }
}
