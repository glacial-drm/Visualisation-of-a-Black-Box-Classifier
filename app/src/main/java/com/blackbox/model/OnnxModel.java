package com.blackbox.model;

import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OnnxModelMetadata;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.TensorInfo;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.tinylog.Logger;

public class OnnxModel {

    private final OrtEnvironment environment;
    private final OrtSession session;

    // Create the environment from a path to an ONNX file.
    public OnnxModel(String filePath) throws FileNotFoundException {
        try {
            this.environment = OrtEnvironment.getEnvironment();
            environment.setTelemetry(false);
            this.session = environment.createSession(filePath, new OrtSession.SessionOptions());
            Logger.info("Created environment and session for onnx file");
        } catch (Exception e) {
            Logger.error("Onnx file for creating environment could not be found: {}", e);
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
            Logger.error("Could not close onnx session: {}", e);
        }
    }

    // Get the number of features.
    public int getNumInputFeatures() {
        try {
            //Get list of all shapes, then take feature number from first one
            List<long[]> shapes = getInputShapes();
            long[] shape = shapes.getFirst();
            return (int) shape[1];
        } catch (Exception e) {
            Logger.error("Failed to obtain feature information for model: {}", e);
            return 0;
        }
    }

    // Get the input names.
    public List<String> getInputNames() {
        try {
            Map<String, NodeInfo> inputInfo = session.getInputInfo();
            return new ArrayList<>(inputInfo.keySet());
        } catch (Exception e) {
            Logger.error("Failed to obtain input information for model: {}", e);
            return null;
        }
    }

    // Get the tensor info.
    public List<TensorInfo> getTensorInfos() {
        Map<String, NodeInfo> inputInfo;
        try {
            inputInfo = session.getInputInfo();
        } catch (Exception e) {
            Logger.error("Failed to obtain tensor information for model: {}", e);
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

    public Optional<List<PointData>> getTrainingData() {
        OnnxModelMetadata metadata;
        try {
            metadata = session.getMetadata();
        } catch (OrtException e) {
            Logger.error("Could not obtain onnx session metadata: {}", e);
            return Optional.empty();
        }

        Optional<String> opX = metadata.getCustomMetadataValue("training_data_X");
        Optional<String> opy = metadata.getCustomMetadataValue("training_data_y");

        if (opX.isEmpty() || opy.isEmpty()) {
            // This isn't a critical error
            Logger.error("No training data found for model");
            return Optional.empty();
        }

        String XStr = opX.get();
        String yStr = opy.get();

        ByteBuffer XBytes = ByteBuffer.wrap(Base64.getDecoder().decode(XStr));
        XBytes.order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer yBytes = ByteBuffer.wrap(Base64.getDecoder().decode(yStr));
        yBytes.order(ByteOrder.LITTLE_ENDIAN);

        int FLOAT_SIZE = 4;
        int FLOATS_PER_POINT = 2;
        int count = XBytes.limit() / (FLOAT_SIZE * FLOATS_PER_POINT);

        List<PointData> points = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            float x = XBytes.getFloat(FLOAT_SIZE * i * FLOATS_PER_POINT);
            float y = XBytes.getFloat(FLOAT_SIZE * (i * FLOATS_PER_POINT + 1));
            short label = yBytes.getShort(i * 2);

            points.add(new PointData(new float[] { x, y }, label));
        }

        return Optional.of(points);
    }
}
