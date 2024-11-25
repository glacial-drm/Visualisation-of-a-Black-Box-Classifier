package com.blackbox.model;

import ai.onnxruntime.*;
import java.util.*;

public class OnnxModel {

    private OrtEnvironment environment;
    private OrtSession session;

    //Constructor takes in file path of onnx file and creates environment.
    public OnnxModel(String filePath){
        try {
            this.environment = OrtEnvironment.getEnvironment();
            this.session = environment.createSession(filePath, new OrtSession.SessionOptions());
        }
        catch (Exception e) {
            //e.printStackTrace();
        }
    }

    //Method to return the session
    public OrtSession getSession() {
        return this.session;
    }


    //Method to close session.
    public void endSession(){
        try {
            session.close();
            environment.close();
        }
        catch (Exception e){
            //e.printStackTrace();
        }
    }

    //Method to retrieve the number of features in the loaded model.
    public int getNumInputFeatures(){
        try{
            Map<String, NodeInfo> inputInfo = session.getInputInfo();
            return inputInfo.size();
        }
        catch (Exception e){
            //e.printStackTrace();
        }
        return 0;
    }

    //Method to return names of inputs
    public List<String> getInputNames(){
        try{
            Map<String, NodeInfo> inputInfo = session.getInputInfo();
            return new ArrayList<>(inputInfo.keySet());
        }
        catch (Exception e){
            //e.printStackTrace();
        }
        return null;
    }
}
