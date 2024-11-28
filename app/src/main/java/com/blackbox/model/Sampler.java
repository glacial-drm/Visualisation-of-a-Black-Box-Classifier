package com.blackbox.model;

import ai.onnxruntime.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

public class Sampler {
    private final OnnxModel onnxModel;

    public Sampler(OnnxModel onnxModel) {
        this.onnxModel = onnxModel;
    }

    /**
     * Method to sample a binary classifier model, returns a list of pointData objects
     * Each of which contains the coordinates (feature1 and feature2), as well as the label
     * given to it by the model.
     */
    public List<PointData> sampleBC(float[][] data) {
        // Checking the model has input features
        List<String> inputNames = onnxModel.getInputNames();
        if (inputNames == null || inputNames.isEmpty()) {
            return null;
        }

        // Takes first input from the list (binary classifier should only have 1)
        String inputName = inputNames.getFirst();

        // Create input tensor
        OrtEnvironment environment = OrtEnvironment.getEnvironment();
        OnnxTensor inputTensor;
        try {
            inputTensor = OnnxTensor.createTensor(environment, data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // Run model
        OrtSession session = onnxModel.getSession();
        try (OrtSession.Result outputs = session.run(Collections.singletonMap(inputName, inputTensor))) {
            // Not used again, data is accessed from the function param
            inputTensor.close();

            OnnxTensor outputTensor = StreamSupport.stream(outputs.spliterator(), false)
                // This has to be a scope because of Closable
                .filter(entry -> { return entry.getValue() instanceof OnnxTensor; })
                .map(entry -> (OnnxTensor) entry.getValue())
                .filter(tensor -> {
                    TensorInfo info = tensor.getInfo();

                    // Check shape and data type of tensor, for labels the shape should be
                    // 1 and the data type should either be int[] or long[]
                    boolean isCorrectShape = info.getShape().length == 1;
                    boolean isCorrentType = info.type == OnnxJavaType.INT64
                        || info.type == OnnxJavaType.INT32;

                    return isCorrectShape && isCorrentType;
                })
                .findFirst()
                .orElse(null);

            if (outputTensor == null) {
                return null;
            }

            long[] label = (long[]) outputTensor.getValue();

            // Use DataCombination to create a list of points from label
            List<PointData> combinedData = DataCombination.combineData(data, label);

            return combinedData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
