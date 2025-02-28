package com.blackbox.model;

import ai.onnxruntime.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import org.tinylog.Logger;

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
            Logger.error("Returning from sampling method due to invalid or insufficient input names");
            return null;
        }

        // Takes first input from the list (binary classifier should only have 1)
        String inputName = inputNames.getFirst();

        // Create input tensor
        OrtEnvironment environment = OrtEnvironment.getEnvironment();
        OnnxTensor inputTensor;
        Logger.info("Creating input tensor for sampling model");
        try {
            inputTensor = OnnxTensor.createTensor(environment, data);
        } catch (Exception e) {
            Logger.error("Failed to create Onnx Tensor object", e);
            return null;
        }

        // Run model
        OrtSession session = onnxModel.getSession();
        Logger.info("Running sampler for sampleBC");
        Logger.info(onnxModel.getTensorInfos());
        Logger.info("Input data shape: [" + data.length + ", " + data[1].length + "]" );
        try (OrtSession.Result outputs = session.run(Collections.singletonMap(inputName, inputTensor))) {
            // Not used again, data is accessed from the function param
            Logger.info("Closing tensor");
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
                Logger.error("Could not retrieve data from output tensor as this was found to be null");
                return null;
            }

            long[] label = (long[]) outputTensor.getValue();

            Logger.info("data has dimensions: " + data[0].length);
            int dimensions = data[0].length;
            // Use DataCombination to create a list of points from label
            List<PointData> combinedData = DataCombination.combineData(data, label, dimensions);

            return combinedData;
        } catch (Exception e) {
            Logger.error("Error sampling the model",e);
            return null;
        }
    }

}
