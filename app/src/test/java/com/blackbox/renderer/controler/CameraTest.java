package com.blackbox.renderer.controler;

import com.blackbox.renderer.controlers.Camera;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CameraTest {
    @BeforeEach
    void init() {
    }

    static Stream<Arguments> randomCameraInits() {
        Random random = new Random();
        return Stream.generate(() -> {
            int width = random.nextInt(5000);
            int height = random.nextInt(5000);
            float fov = random.nextFloat(180);
            float near = random.nextFloat(1000);
            float far = random.nextFloat(10000) + near;
            return Arguments.of(width, height, fov, near, far);
        }).limit(100);
    }

    @NotNull
    static Matrix4f createPerspectiveMatrix(int width, int height, float fov , float near, float far) {
        float aspect = (float) width / height;
        float tanHalfFovy = (float) Math.tan(fov / 2.0f);

        float m00 = 1.0f / (aspect * tanHalfFovy);
        float m11 = 1.0f / tanHalfFovy;
        float m22 = -(far + near) / (far - near);
        float m23 = -1.0f;
        float m32 = -(2.0f * far * near) / (far - near);

        Matrix4f mat = new Matrix4f();
        mat.set(m00, 0, 0, 0, 0,m11, 0, 0, 0, 0,
                m22, m23, 0, 0, m32, 0);
        return mat;
    }

    @Test
    void constructorTest() {
        new Camera(10, 10, 10, 1, 2);
        assertTrue(true, "Camera tests failed to start");
    }

    @ParameterizedTest
    @MethodSource("randomCameraInits")
    void perspectiveProjectionMatrixTest(int width, int height, float fov ,float near, float far) {
        Camera camera = new Camera(width, height, fov, near, far);
        Matrix4f perspectiveMatrix = createPerspectiveMatrix(width, height, fov, near, far);

        assertEquals(perspectiveMatrix, camera.getPerspective(),
                String.format("Perspective projection matrix test failed, %d, %d, %f, %f, %f\n"
                        , width, height, fov, near, far));
    }

    @ParameterizedTest
    @MethodSource("randomCameraInits")
    void resizeTests(int width, int height, float fov ,float near, float far) {
        Camera camera = new Camera(100, 50, fov, near, far);

        Matrix4f originalMat = createPerspectiveMatrix(100, 50, fov, near, far);
        assertEquals(originalMat, camera.getPerspective(),
                String.format("Perspective projection matrix test failed, %d, %d, %f, %f, %f\n"
                        , width, height, fov, near, far));

        Matrix4f resizedMat = createPerspectiveMatrix(width, height, fov, near, far);
        camera.resize(width, height);

        assertEquals(resizedMat, camera.getPerspective(),
                String.format("Resize didnt resize, %d, %d, %f, %f, %f\n"
                        , width, height, fov, near, far));
    }
}
