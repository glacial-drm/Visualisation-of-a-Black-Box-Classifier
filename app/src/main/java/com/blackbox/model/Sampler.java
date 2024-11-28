package com.blackbox.model;

import ai.onnxruntime.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Sampler {

    private final OnnxModel onnxModel;

    //Constructor
    public Sampler(OnnxModel onnxModel) {
        this.onnxModel = onnxModel;
    }

    /*
    Method to sample a binary classifier model, returns a list of pointData objects
    Each of which contains the coordinates (feature1 and feature2), as well as the label
    given to it by the model.
    * */
    public List<PointData> sampleBC(float[][] data) {
        try {
            //Checking the model has input features, otherwise returns null.
            List<String> inputNames = onnxModel.getInputNames();
            if (inputNames == null || inputNames.isEmpty()) {
                return null;
            }

            //Takes first input from the list (binary classifier should only have 1)
            String inputName = inputNames.getFirst();

            //Create OnnxTensor and input data
            OrtEnvironment environment = OrtEnvironment.getEnvironment();
            OnnxTensor inputTensor = OnnxTensor.createTensor(environment, data);

            //Run model
            OrtSession session = onnxModel.getSession();
            try (OrtSession.Result outputs = session.run(Collections.singletonMap(inputName, inputTensor))) {

                //Loop through the outputs, recording the name and value at each.
                for (Map.Entry<String, OnnxValue> entry : outputs) {
                    String outputName = entry.getKey();
                    OnnxValue value = entry.getValue();
                    if (value instanceof OnnxTensor tensor) {

                        /*
                        Check shape and data type of tensor, for labels the shape should be
                        1 and the data type should either be int[] or long[]
                        */

                        //Check shape
                        long[] shape = tensor.getInfo().getShape();
                        if (shape.length == 1) {
                            TensorInfo tensorInfo = tensor.getInfo();
                            //Check data type
                            if (tensorInfo.type == OnnxJavaType.INT64 || tensorInfo.type == OnnxJavaType.INT32) {
                                long[] labelData = (long[]) tensor.getValue();

                                //Use DataCombination to create a list of points from labelData
                                List<PointData> combinedData = DataCombination.combineData(data, labelData);

                                //close tensor
                                inputTensor.close();

                                //return list
                                return combinedData;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }

}