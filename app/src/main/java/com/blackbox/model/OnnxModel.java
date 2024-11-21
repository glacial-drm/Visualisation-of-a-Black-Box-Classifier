package com.blackbox.model;

import ai.onnxruntime.*;
import java.util.*;

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
        environment.close();
    }

    //Method to retrieve the number of features in the loaded model.
    public int getNumInputFeatures() throws OrtException {

        Map<String, NodeInfo> inputInfo = session.getInputInfo();
        return inputInfo.size();
    }

    //Method to return names of inputs
    public List<String> getInputNames() throws OrtException {
        Map<String, NodeInfo> inputInfo = session.getInputInfo();
        return new ArrayList<>(inputInfo.keySet());
    }
}
