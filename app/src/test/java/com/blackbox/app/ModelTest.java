package com.blackbox.app;

import com.blackbox.generators.DataGenerator;
import com.blackbox.generators.GlobalGenOptions;
import com.blackbox.model.PointData;
import com.blackbox.proto.connector.SvmRequest;
import com.blackbox.proto.connector.UiTest;
import com.google.protobuf.GeneratedMessage;
import javafx.scene.Node;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    Model model = new Model();
    DataGenerator generator = new DataGenerator() {
        @Override
        public int getDimension() {
            return 0;
        }

        @Override
        public Node createNode() {
            return null;
        }

        @Override
        public PointData[] buildData(GlobalGenOptions options) {
            return new PointData[0];
        }
    };
    DataGenerator[] dataList = {generator, generator};

    GeneratedMessage.Builder<?>[] requests = new GeneratedMessage.Builder[] {
        // This specifies the default values for all the training requests
        UiTest.newBuilder()
                .setBoolean(true)
                .setF32(1f)
                .setF64(2.0)
                .setI32(3)
                .setI64(4)
                .setStr("Hello world")
                .setTestEnum(UiTest.TestEnum.OPTION2),
                SvmRequest.newBuilder()
    };

    List<String> classifiers = new ArrayList<String>();
    String newClassifier = "new_classifier";
    File file = new File(System.getProperty("user.dir"));

    @Test
    void testGetSetTrainingData(){
        PointData data = new PointData(new float[] { 0, 0, 0 }, 0);
        PointData[] dataList = {data, data};
        model.setTrainingData(dataList);
        assertEquals(dataList,model.getTrainingData());
    }

    @Test
    void testGetGetOptions(){
        assertNotNull(model.getGetOptions());
    }

    @Test
    void testGetSetGenerators(){
        model.setGenerators(dataList);
        assertEquals(dataList,model.getGenerators());
    }
    @Test
    void testGetSetSelectedGenerators(){
        model.setGenerators(dataList);
        model.setSelectedGenerator(1);
        assertEquals(generator,model.getSelectedGenerator());
    }

    @Test
    void testGetSetTrainingRequests(){
        model.setTrainingRequests(requests);
        assertEquals(requests,model.getTrainingRequests());
    }
    @Test
    void testGetSetSelectedTrainingRequests(){
        model.setTrainingRequests(requests);
        model.setSelectedTrainingRequest(0);
        assertEquals(requests[0],model.getSelectedTrainingRequest());
    }

    @Test
    void testInitClassifiers(){
        model.initClassifiers();
        assertEquals("",model.getClassifiers().getFirst());
    }
    @Test
    void testGetSetSelectedClassifiers(){
        model.setSelectedClassifier(newClassifier);
        assertEquals(newClassifier,model.getSelectedClassifier());
    }

    @Test
    void testGetSetCurrentDir(){
        model.setCurrentDir(file);
        assertEquals(file,model.getCurrentDir());
    }

}
