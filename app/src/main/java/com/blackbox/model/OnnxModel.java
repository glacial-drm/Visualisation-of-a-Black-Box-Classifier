package com.blackbox.model;

import ai.onnxruntime.*;

public class OnnxModel {

    private OrtEnvironment environment;
    private OrtSession session;

    //Constructor takes in file path of onnx file and creates environment.
    public OnnxModel(String filePath) throws OrtException {

        this.environment = OrtEnvironment.getEnvironment();
        this.session = environment.createSession(filePath, new OrtSession.SessionOptions());
    }

    //Method to close session.
    public void endSession() throws OrtException {

        session.close();
    }
}
