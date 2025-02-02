package com.blackbox.model;

import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.TensorInfo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OnnxModel {

    private final OrtEnvironment environment;
    private final OrtSession session;

    // Create the environment from a path to an ONNX file.
    public OnnxModel(String filePath) throws FileNotFoundException {
        try {
            this.environment = OrtEnvironment.getEnvironment();
            environment.setTelemetry(false);
            this.session = environment.createSession(filePath, new OrtSession.SessionOptions());
        } catch (Exception e) {
            throw new FileNotFoundException("Onnx file not found");
        }
    }

    // Get the session.
    public OrtSession getSession() {
        return this.session;
    }

    // Close the session.
    public void endSession() {
        try {
            session.close();
            environment.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get the number of features.
    public int getNumInputFeatures() {
        try {
            Map<String, NodeInfo> inputInfo = session.getInputInfo();
            return inputInfo.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Get the input names.
    public List<String> getInputNames() {
        try {
            Map<String, NodeInfo> inputInfo = session.getInputInfo();
            return new ArrayList<>(inputInfo.keySet());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get the tensor info.
    public List<TensorInfo> getTensorInfos() {
        Map<String, NodeInfo> inputInfo;
        try {
            inputInfo = session.getInputInfo();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return inputInfo.values().stream()
            .filter(nodeInfo -> nodeInfo.getInfo() instanceof TensorInfo)
            .map(tensorInfo -> (TensorInfo)tensorInfo.getInfo())
            .collect(Collectors.toList());
    }

    // Get the input types.
    public List<String> getInputTypes() {
        return getTensorInfos().stream()
            .map(tensorInfo -> tensorInfo.type.name())
            .collect(Collectors.toList());
    }

    // Get the shape of the input data.
    public List<long[]> getInputShapes() {
        return getTensorInfos().stream()
            .map(tensorInfo -> tensorInfo.getShape())
            .collect(Collectors.toList());
    }
}
