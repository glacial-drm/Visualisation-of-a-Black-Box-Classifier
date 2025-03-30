package com.blackbox.renderer.model;

import com.blackbox.graph.Input.InputHandler;
import com.blackbox.model.PointData;
import com.blackbox.renderer.controllers.IndexBuffer;
import com.blackbox.renderer.controllers.VertexArray;
import com.blackbox.renderer.controllers.VertexBuffer;
import com.blackbox.renderer.model.primitives.Point;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;

public class PointManager {
    private final List<Point> points;
    // Shared objects for instanced rendering:
    private final VertexArray vao;
    private IndexBuffer indexBuffer;
    // Instance VBOs:
    private VertexBuffer instanceVBO;       // For MVP matrices
    private VertexBuffer instanceColorVBO;  // For per-instance colors

    public PointManager() {
        vao = new VertexArray();
        points = new ArrayList<>();
        initInstanceRendering();
    }

    /**
     * Adds a point using your provided data.
     */
    public void addPoint(PointData pointData, boolean isTraining, float aspect) {
        // Don't expect any number of dimensions
        Vector3f position = new Vector3f(
            pointData.getValue(0).orElse(0f),
            pointData.getValue(1).orElse(0f),
            pointData.getValue(2).orElse(0f)
        );

        float brightness = isTraining ? 0.5f : 0.0f;
        Vector3f color = pointData.label == 1
            ? new Vector3f(1.0f, brightness, brightness)
            : new Vector3f(brightness, brightness, 1.0f);

        float scale;
        float fov = (float) Math.toRadians(70);
        float depth = (float) InputHandler.getZoom();

        float visibleHeight = (float) (2.0f * depth * Math.tan(fov / 2.0f));
        float visibleWidth = visibleHeight * aspect;

        if(isTraining) {
            scale = 0.05f;
        } else{
            scale = visibleWidth * 0.03f;
        }
        points.add(new Point(position, new Vector3f(0, 0, 0), scale, color));
    }

    /**
     * Delete all of the points.
     */
    public void clearPoints() {
        points.clear();
    }

    /**
     * Sets up the shared VAO, geometry VBO, index buffer,
     * instance VBO for MVP matrices, and instance VBO for colors.
     */
    private void initInstanceRendering() {
        vao.bind();

        initVertexPositionData();
        initIndexBuffer();
        // Create the instance VBO for MVP matrices.
        initModelData();
        initColourData();

        vao.unbind();
    }

    private void initVertexPositionData() {
        float[] vertices = {-0.5f, 0.5f, 0f, -0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, 0.5f, 0f,};

        // Create and upload geometry vertex data.
        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.bind();
        vertexBuffer.storeFloatData(vertices);
        int stride = (3) * Float.BYTES;  // 3 floats for position, 3 for color
        // Attribute 0: position
        vao.setVertexAttrib(0, 3, GL_FLOAT, false, stride, 0, false);
        vertexBuffer.unbind();
    }

    private void initIndexBuffer() {
        // Indices for two triangles (quad)
        final int[] indices = {0, 1, 3, 3, 1, 2};
        // Create and upload index data.
        indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        indexBuffer.storeIndexData(indices);
        indexBuffer.unbind();
    }


    private void initColourData() {
        // Create the instance VBO for per-instance color.
        instanceColorVBO = new VertexBuffer();
        instanceColorVBO.bind();
        vao.setVertexAttrib(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0, true);
        instanceColorVBO.unbind();
    }

    private void initModelData() {
        instanceVBO = new VertexBuffer();
        instanceVBO.bind();
        // (No initial data; each instance supplies a 4x4 matrix = 16 floats.)
        int matrixStride = 16 * Float.BYTES;
        // Set up four vertex attributes (locations 2-5) for each column of the matrix.
        for (int i = 0; i < 4; i++) {
            vao.setVertexAttrib(2 + i, 4, GL_FLOAT, false, matrixStride, i * 4 * Float.BYTES, true);
        }
        instanceVBO.unbind();
    }

    public void prepPoints() {
        uploadModel();
        uploadColourData();

        vao.bind();
        vao.enableVertexAttributes();
        indexBuffer.bind();
    }

    public void cleanUp() {
        vao.disableVertexAttributes();
        indexBuffer.unbind();
        vao.unbind();
    }

    private void uploadModel() {
        int count = points.size();

        float[] mvpData = new float[count * 16];
        int mvpOffset = 0;
        for (Point point : points) {
            Matrix4f model = point.getModelMatrix();
            model.get(mvpData, mvpOffset);
            mvpOffset += 16;
        }
        instanceVBO.bind();
        instanceVBO.storeFloatData(mvpData);
        instanceVBO.unbind();
    }

    private void uploadColourData() {
        // upload per-instance color data.
        float[] colorData = extractColors(points);
        instanceColorVBO.bind();
        instanceColorVBO.storeFloatData(colorData);
        instanceColorVBO.unbind();
    }

    private float[] extractColors(List<Point> points) {
        float[] colorData = new float[points.size() * 3];
        int colorOffset = 0;
        for (Point point : points) {
            Vector3f col = point.colour();
            colorData[colorOffset++] = col.x();
            colorData[colorOffset++] = col.y();
            colorData[colorOffset++] = col.z();
        }
        return colorData;
    }

    public boolean isPoints() {
        return !points.isEmpty();
    }

    public int getPointCount() {
        return points.size();
    }

    public int getIndicesLength() {
        return indexBuffer.getCount();
    }
}
