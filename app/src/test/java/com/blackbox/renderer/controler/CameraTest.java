package com.blackbox.renderer.controler;

import com.blackbox.renderer.controlers.Camera;
import com.blackbox.renderer.controlers.Renderer;
import com.blackbox.renderer.model.Model;
import org.joml.Matrix4f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.lwjgl.opengl.GL;

import java.util.Random;
import java.util.stream.Stream;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.lwjgl.glfw.GLFW.*;

public class CameraTest {
    @BeforeEach
    void init() {
    }

    static Stream<Arguments> randomCameraInits() {
        Random random = new Random();
        return Stream.generate(() -> {
                    float width = random.nextFloat(2000);
                    float height = random.nextFloat(2000);
                    float fov = random.nextFloat(180);
                    float near = random.nextFloat(5);
                    float far = random.nextFloat(10000);
                    return Arguments.of(width, height, fov, near, far);
                }).limit(1000);
    }

    @Test
    void constructorTest() {
        Camera camera = new Camera(10, 10, 10, 1, 2);
        assertTrue(true, "Camera tests failed to start");
    }

    @ParameterizedTest
    @MethodSource("randomCameraInits")
    void perspectiveProjectionMatrixTest(float width, float height, float fov ,float near, float far) {
        float aspect = width / height;
        float tanHalfFovy = (float) Math.tan(fov / 2.0f);

        float m00 = 1.0f / (aspect * tanHalfFovy);
        float m11 = 1.0f / tanHalfFovy;
        float m22 = -(far + near) / (far - near);
        float m23 = -1.0f;
        float m32 = -(2.0f * far * near) / (far - near);

        Matrix4f perspectiveMatrix = new Matrix4f();
        perspectiveMatrix.set(m00, 0, 0, 0, 0,m11, 0, 0, 0, 0,
                m22, m23, 0, 0, m32, 0);

        Camera camera = new Camera(width, height, fov, near, far);
        assertEquals(perspectiveMatrix, camera.getPerspective(),
                String.format("Perspective projection matrix test failed, %f, %f, %f, %f, %f\n"
                        , width, height, fov, near, far));
    }

}
