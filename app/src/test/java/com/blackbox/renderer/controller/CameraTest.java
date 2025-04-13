package com.blackbox.renderer.controller;

import com.blackbox.graph.Input.InputHandler;
import com.blackbox.graph.Input.MouseInputNames;
import com.blackbox.renderer.controllers.Camera;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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


    @Test
    void transformMatrixTest() {
        Vector3f translation = new Vector3f(3, 4, 5);
        float scale = 2.0f;
        Matrix4f transform = Camera.createTransformMatrix(translation, scale);

        Vector3f point = new Vector3f(1, 1, 1);
        Vector3f transformed = transform.transformPosition(new Vector3f(point));

        Vector3f expected = new Vector3f(5, 6, 7);
        assertTrue(transformed.equals(expected, 0.001f), "Transform matrix did not produce expected result");
    }

    /**
     * Tests that getViewMatrix() returns the expected view for the initial camera position.
     */
    @Test
    void getViewMatrixTest() {
        try (MockedStatic<InputHandler> mocked = Mockito.mockStatic(InputHandler.class)) {
            mocked.when(InputHandler::getZoom).thenReturn(5.0);

            Camera camera = new Camera(800, 600, 70, 0.1f, 1000);
            Matrix4f view = camera.getViewMatrix();

            Matrix4f expectedView = new Matrix4f().lookAt(
                    new Vector3f(0, 0, 5.0f),
                    new Vector3f(0, 0, 6.0f),
                    new Vector3f(0, 1, 0)
            );
            assertEquals(expectedView, view, "The initial view matrix is not as expected");
        }
    }

    /**
     * Tests the branch in move() when a left-mouse click is active.
     */
    @Test
    void moveWithMouseMovementTest() {
        try (MockedStatic<InputHandler> mocked = Mockito.mockStatic(InputHandler.class)) {
            // Set up singleton state
            mocked.when(() -> InputHandler.getMouseState(MouseInputNames.LEFT_CLICK)).thenReturn(true);
            mocked.when(InputHandler::getOriginalMouseX).thenReturn(100.0);
            mocked.when(InputHandler::getOriginalMouseY).thenReturn(100.0);
            mocked.when(InputHandler::getMouseX).thenReturn(120.0);
            mocked.when(InputHandler::getMouseY).thenReturn(80.0);
            mocked.when(InputHandler::getZoom).thenReturn(0.0);

            Camera camera = new Camera(800, 600, 70, 0.1f, 1000);
            Matrix4f viewBefore = new Matrix4f(camera.getViewMatrix());
            boolean moved = camera.move();

            Matrix4f viewAfter = camera.getViewMatrix();
        }
    }

    /**
     * Tests the branch in move() where keyboard movement (e.g. 'W' key) causes movement.
     */
    @Test
    void moveWithKeyboardMovementTest() {
        try (MockedStatic<InputHandler> mocked = Mockito.mockStatic(InputHandler.class)) {
            // Set up singleton state
            mocked.when(() -> InputHandler.getMouseState(MouseInputNames.LEFT_CLICK)).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('W')).thenReturn(true);
            mocked.when(() -> InputHandler.getKeyState('S')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('A')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('D')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('Q')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('E')).thenReturn(false);
            mocked.when(InputHandler::getZoom).thenReturn(0.0);

            Camera camera = new Camera(800, 600, 70, 0.1f, 1000);
            Matrix4f viewBefore = new Matrix4f(camera.getViewMatrix());
            boolean moved = camera.move();

            Matrix4f viewAfter = camera.getViewMatrix();
        }
    }

    /**
     * Tests the branch in move() where no keys or mouse clicks are active and zoom is zero.
     */
    @Test
    void moveNoInputTest() {
        try (MockedStatic<InputHandler> mocked = Mockito.mockStatic(InputHandler.class)) {
            // Set up singleton state
            mocked.when(() -> InputHandler.getMouseState(MouseInputNames.LEFT_CLICK)).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('W')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('S')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('A')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('D')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('Q')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('E')).thenReturn(false);
            mocked.when(InputHandler::getZoom).thenReturn(0.0);

            Camera camera = new Camera(800, 600, 70, 0.1f, 1000);
            Matrix4f viewBefore = new Matrix4f(camera.getViewMatrix());
            boolean moved = camera.move();

            assertFalse(moved, "Move should return false when no input is detected");
            Matrix4f viewAfter = camera.getViewMatrix();
            assertEquals(viewBefore, viewAfter, "View matrix should not change when there is no input");
        }
    }

    /**
     * Tests the keyboard zoom branch when the 'Q' key is pressed.
     * In this branch, the camera zoom is decreased.
     */
    @Test
    void moveWithKeyboardZoomQTest() {
        try (MockedStatic<InputHandler> mocked = Mockito.mockStatic(InputHandler.class)) {
            // Set up singleton state
            mocked.when(() -> InputHandler.getMouseState(MouseInputNames.LEFT_CLICK)).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('W')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('S')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('A')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('D')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('Q')).thenReturn(true);
            mocked.when(() -> InputHandler.getKeyState('E')).thenReturn(false);

            mocked.when(InputHandler::getZoom).thenReturn(5.0, 4.8);

            Camera camera = new Camera(800, 600, 70, 0.1f, 1000);
            boolean moved = camera.move();
            assertTrue(moved, "Move should return true when the Q key is pressed for zoom");

            Matrix4f invView = new Matrix4f(camera.getViewMatrix()).invert();
            Vector3f cameraPos = new Vector3f();
            invView.getTranslation(cameraPos);

            assertEquals(0.0f, cameraPos.x, 0.0001);
            assertEquals(0.0f, cameraPos.y, 0.0001);
            assertEquals(4.8f, cameraPos.z, 0.0001, "Camera zoom did not update correctly with Q key");
        }
    }

    /**
     * Tests the keyboard zoom branch when the 'E' key is pressed.
     * In this branch, the camera zoom is increased.
     */
    @Test
    void moveWithKeyboardZoomETest() {
        try (MockedStatic<InputHandler> mocked = Mockito.mockStatic(InputHandler.class)) {
            // Set up singleton state
            mocked.when(() -> InputHandler.getMouseState(MouseInputNames.LEFT_CLICK)).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('W')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('S')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('A')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('D')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('Q')).thenReturn(false);
            mocked.when(() -> InputHandler.getKeyState('E')).thenReturn(true);
            mocked.when(InputHandler::getZoom).thenReturn(5.0, 5.2);

            Camera camera = new Camera(800, 600, 70, 0.1f, 1000);
            boolean moved = camera.move();
            assertTrue(moved, "Move should return true when the E key is pressed for zoom");

            Matrix4f invView = new Matrix4f(camera.getViewMatrix()).invert();
            Vector3f cameraPos = new Vector3f();
            invView.getTranslation(cameraPos);

            assertEquals(0.0f, cameraPos.x, 0.001);
            assertEquals(0.0f, cameraPos.y, 0.001);
            assertEquals(5.2f, cameraPos.z, 0.001, "Camera zoom did not update correctly with E key");
        }
    }
}
