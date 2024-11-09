package com.blackbox.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Vector3f cameraPos;
    private Vector3f cameraTarget;
    private Vector3f cameraDirection;
    private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

    public Camera(){
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    public Matrix4f createTransformMatrix(Vector3f translation, Vector3f rotation, float scale){
        Matrix4f mat  = new Matrix4f();
        mat.identity();
        mat.translate(translation);
        mat.rotate((float)Math.toRadians(rotation.x()), (new Vector3f(1,0,0)));
        mat.rotate((float)Math.toRadians(rotation.y()), (new Vector3f(0,1,0)));
        mat.rotate((float)Math.toRadians(rotation.z()), (new Vector3f(0,0,1)));
        mat.scale(scale);

        return mat;
    }
}
