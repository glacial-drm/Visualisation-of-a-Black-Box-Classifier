package com.blackbox.generator;

import com.blackbox.generators.CubeGenerator;
import com.blackbox.generators.DataGenerator;
import com.blackbox.generators.GlobalGenOptions;
import com.blackbox.model.PointData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.util.Arrays;

public class CubeGeneratorTest {

    /**
     * Tests if the generated data array length matches the specified count in GlobalGenOptions.
     * Uses reflection to override private fields for controlled testing.
     */
    @Test
    void testBuildDataCount() throws Exception {
        CubeGenerator generator = new CubeGenerator();
        GlobalGenOptions options = new GlobalGenOptions();

        setField(options, "count", 5000);
        setField(options, "range", 3.0f);

        PointData[] data = generator.buildData(options);
        assertEquals(5000, data.length);
    }

    /**
     * Validates that all generated points fall within the specified range [-range, range].
     * Tests X, Y, and Z coordinates independently.
     */
    @Test
    void testDataInRange() throws Exception {
        CubeGenerator generator = new CubeGenerator();
        GlobalGenOptions options = new GlobalGenOptions();
        setField(options, "range", 3.0f);

        PointData[] data = generator.buildData(options);
        for (PointData p : data) {
            assertTrue(p.features[0] >= -3.0f && p.features[0] <= 3.0f);
            assertTrue(p.features[1] >= -3.0f && p.features[1] <= 3.0f);
            assertTrue(p.features[2] >= -3.0f && p.features[2] <= 3.0f);
        }
    }

    /**
     * Verifies label calculation logic:
     * Points with x² + y² >= radius² should be labeled 0, others 1.
     * Uses a fixed random seed for deterministic testing.
     */
    @Test
    void testLabelLogic() throws Exception {
        CubeGenerator generator = new CubeGenerator();
        GlobalGenOptions options = new GlobalGenOptions();
        setField(options, "count", 1);
        setField(options, "range", 3.0f);

        setField(generator, "radius", 1.0f);
        Field rngField = DataGenerator.class.getDeclaredField("rng");
        rngField.setAccessible(true);
        rngField.set(generator, new java.util.Random(12345));

        PointData p = generator.buildData(options)[0];
        double distanceSq = Math.pow(p.features[0], 2) + Math.pow(p.features[1], 2);
        int expectedLabel = distanceSq >= 1.0 ? 0 : 1;
        assertEquals(expectedLabel, p.label);
    }

    /**
     * Ensures points are generated only in enabled quadrants.
     * Disables 7 quadrants and validates coordinates fall within the frt quadrant.
     */
    @Test
    void testDisabledQuadrants() throws Exception {
        CubeGenerator generator = new CubeGenerator();
        GlobalGenOptions options = new GlobalGenOptions();
        setField(options, "count", 1000);
        setField(options, "range", 4.0f);

        // Disable all quadrants except frt (front-right-top)
        setField(generator, "frt", true);
        setField(generator, "flt", false);
        setField(generator, "blt", false);
        setField(generator, "brt", false);
        setField(generator, "frb", false);
        setField(generator, "flb", false);
        setField(generator, "brb", false);
        setField(generator, "blb", false);

        PointData[] data = generator.buildData(options);
        for (PointData p : data) {
            // frt quadrant: x > 0, y > 0, z > 0 (range=4 -> offset=2)
            assertTrue(p.features[0] >= 0 && p.features[0] <= 4, "X should be in [0,4] for frt quadrant");
            assertTrue(p.features[1] >= 0 && p.features[1] <= 4, "Y should be in [0,4] for frt quadrant");
            assertTrue(p.features[2] >= 0 && p.features[2] <= 4, "Z should be in [0,4] for frt quadrant");
        }
    }

    /**
     * Tests if reducing the radius increases points labeled 1 (inside the cube).
     * Counts labeled points to validate logic.
     */
    @Test
    void testRadiusImpactOnLabels() throws Exception {
        CubeGenerator generator = new CubeGenerator();
        GlobalGenOptions options = new GlobalGenOptions();
        setField(options, "count", 1000);
        setField(options, "range", 2.0f);

        // Set radius to 0.5 (smaller than default 1.0)
        setField(generator, "radius", 0.5f);

        PointData[] data = generator.buildData(options);
        long label1Count = Arrays.stream(data).filter(p -> p.label == 1).count();
        assertTrue(label1Count > 0, "At least some points should have label=1 with smaller radius");
    }

    /**
     * Tests edge case: zero count should return an empty array.
     */
    @Test
    void testZeroCountHandling() throws Exception {
        CubeGenerator generator = new CubeGenerator();
        GlobalGenOptions options = new GlobalGenOptions();
        setField(options, "count", 0);  // Edge case

        PointData[] data = generator.buildData(options);
        assertEquals(0, data.length, "Zero count should produce empty array");
    }

    /**
     * Tests invalid input: negative range should throw IllegalArgumentException.
     */
    @Test
    void testNegativeRangeHandling() throws Exception {
        CubeGenerator generator = new CubeGenerator();
        GlobalGenOptions options = new GlobalGenOptions();
        setField(options, "range", -3.0f);  // Invalid range

        assertThrows(IllegalArgumentException.class, () -> {
            generator.buildData(options);  // Should handle negative range gracefully
        }, "Negative range should throw exception");
    }

    /**
     * Validates randomness behavior:
     * - Identical seeds produce identical point data.
     * Field-by-field comparison avoids Object.equals() issues.
     */
    @Test
    void testRandomnessWithDifferentSeeds() throws Exception {
        CubeGenerator generator1 = new CubeGenerator();
        GlobalGenOptions options = new GlobalGenOptions();
        setField(options, "count", 100);

        Field rngField = DataGenerator.class.getDeclaredField("rng");
        rngField.setAccessible(true);
        rngField.set(generator1, new java.util.Random(12345));
        PointData[] data1 = generator1.buildData(options);

        CubeGenerator generator2 = new CubeGenerator();
        rngField.set(generator2, new java.util.Random(12345));
        PointData[] data2 = generator2.buildData(options);

        // Compare each field to bypass Object.equals() limitations
        for (int i = 0; i < data1.length; i++) {
            assertEquals(data1[i].features[0], data2[i].features[0], 1e-6);
            assertEquals(data1[i].features[1], data2[i].features[1], 1e-6);
            assertEquals(data1[i].features[2], data2[i].features[2], 1e-6);
            assertEquals(data1[i].label, data2[i].label);
        }
    }

    /**
     * Helper method to set private fields via reflection.
     * Also looks for the field in the superclass if it wasn't present.
     *
     * @param target The object whose field is modified.
     * @param fieldName The name of the field to set.
     * @param value The value to assign.
     */
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field;
        try {
            field = target.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = target.getClass().getSuperclass().getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        field.set(target, value);
    }
}
