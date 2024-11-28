package com.blackbox.model;

import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.TensorInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.io.FileNotFoundException;

public class OnnxModel {

    private OrtEnvironment environment;
    private OrtSession session;

    //Constructor takes in file path of onnx file and creates environment.
    public OnnxModel(String filePath) throws FileNotFoundException {
        try {
            this.environment = OrtEnvironment.getEnvironment();
            environment.setTelemetry(false);
            this.session = environment.createSession(filePath, new OrtSession.SessionOptions());
        } catch (Exception e) {
            throw new FileNotFoundException("Onnx file not found");
        }
    }

    //Method to return the session
    public OrtSession getSession() {
        return this.session;
    }


    //Method to close session.
    public void endSession() {
        try {
            session.close();
            environment.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method to retrieve the number of features in the loaded model.
    public int getNumInputFeatures() {
        try {
            Map<String, NodeInfo> inputInfo = session.getInputInfo();
            return inputInfo.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //Method to return names of inputs
    public List<String> getInputNames() {
        try {
            Map<String, NodeInfo> inputInfo = session.getInputInfo();
            return new ArrayList<>(inputInfo.keySet());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Method to return input types
    public List<String> getInputtypes() {
        List<String> inputTypes = new ArrayList<>();
        try {
            Map<String, NodeInfo> inputInfo = session.getInputInfo();
            for (NodeInfo nodeInfo : inputInfo.values()) {
                if (nodeInfo.getInfo() instanceof TensorInfo tensorInfo) {
                    inputTypes.add(tensorInfo.type.name());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputTypes;
    }

    //Method to return the shape of the expected input data.
    public List<long[]> getInputShapes() {
        List<long[]> shapes = new ArrayList<>();
        try {
            Map<String, NodeInfo> inputInfo = session.getInputInfo();
            for (NodeInfo nodeInfo : inputInfo.values()) {
                if (nodeInfo.getInfo() instanceof TensorInfo tensorInfo) {
                    shapes.add(tensorInfo.getShape());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shapes;
    }
}
