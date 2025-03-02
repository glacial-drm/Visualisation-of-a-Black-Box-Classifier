package com.blackbox.app;

import com.blackbox.app.events.ControllerEvent;
import com.blackbox.eventqueue.EventConsumer;
import com.blackbox.eventqueue.EventProducer;
import com.blackbox.generators.CircleGenerator;
import com.blackbox.generators.CubeGenerator;
import com.blackbox.generators.DataGenerator;
import com.blackbox.generators.GlobalGenOptions;
import com.blackbox.graph.events.CanvasEvent;
import com.blackbox.graph.events.ClearPoints;
import com.blackbox.graph.events.PointsLoaded;
import com.blackbox.graph.events.StopEvent;
import com.blackbox.model.PointData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.google.protobuf.GeneratedMessage.Builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Controller class.
 *
 * Uses Mockito to mock dependencies and verify Controller behavior.
 */
class ControllerTest {

    @Mock private Model model;
    @Mock private EventConsumer<ControllerEvent> eventQueue;
    @Mock private EventProducer<CanvasEvent> canvasEventSink;
    @Mock private Connector connector; // Mocking Connector to prevent real gRPC calls

    private Controller controller;

    /**
     * Setup method to initialize mocks before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Defining mock behavior for Model to prevent NullPointerException
        when(model.getGenerators()).thenReturn(new DataGenerator[]{ new CircleGenerator(), new CubeGenerator() });
        when(model.getSelectedGenerator()).thenReturn(new CircleGenerator());
        when(model.getGetOptions()).thenReturn(new GlobalGenOptions());
        when(model.getTrainingRequests()).thenReturn(new Builder[]{});
        when(model.getTrainingData()).thenReturn(new PointData[]{});

        // Initialize Controller with mocked dependencies
        controller = new Controller(model, eventQueue, canvasEventSink, connector);
    }

    /**
     * Tests whether clearPoints() sends a ClearPoints event.
     */
    @Test
    void testClearPoints() {
        controller.clearPoints();
        verify(canvasEventSink, times(1)).send(any(ClearPoints.class));
    }

    /**
     * Tests if generateTrainingData() correctly builds and sets training data.
     */
    @Test
    void testGenerateTrainingData() {
        DataGenerator mockGenerator = mock(DataGenerator.class);
        GlobalGenOptions mockOptions = mock(GlobalGenOptions.class);
        when(model.getSelectedGenerator()).thenReturn(mockGenerator);
        when(model.getGetOptions()).thenReturn(mockOptions);

        controller.generateTrainingData();

        verify(mockGenerator, times(1)).buildData(mockOptions);
        verify(model, atLeastOnce()).setTrainingData(any()); // Allows multiple calls
    }

    /**
     * Tests if showTrainingData() sends PointsLoaded events for each training point.
     */
    @Test
    void testShowTrainingData() {
        PointData mockPoint = mock(PointData.class);
        when(model.getTrainingData()).thenReturn(new PointData[]{mockPoint});

        controller.showTrainingData();

        verify(canvasEventSink, times(1)).send(any(PointsLoaded.class));
    }

    /**
     * Tests if getGenerators() returns the expected generators.
     */
    @Test
    void testGetGenerators() {
        DataGenerator[] generators = {new CircleGenerator(), new CubeGenerator()};
        when(model.getGenerators()).thenReturn(generators);

        DataGenerator[] result = controller.getGenerators();

        assertArrayEquals(generators, result);
    }

    /**
     * Tests if getSelectedGenerator() returns the expected generator.
     */
    @Test
    void testGetSelectedGenerator() {
        DataGenerator mockGenerator = mock(DataGenerator.class);
        when(model.getSelectedGenerator()).thenReturn(mockGenerator);

        DataGenerator result = controller.getSelectedGenerator();

        assertEquals(mockGenerator, result);
    }

    /**
     * Tests if setSelectedGenerator() updates the selected generator index.
     */
    @Test
    void testSetSelectedGenerator() {
        controller.setSelectedGenerator(1);
        verify(model, times(1)).setSelectedGenerator(1);
    }

    /**
     * Tests if pointLoaded() sends a PointsLoaded event.
     */
    @Test
    void testPointLoaded() {
        PointData mockPoint = mock(PointData.class);
        controller.pointLoaded(mockPoint, true);
        verify(canvasEventSink, times(1)).send(any(PointsLoaded.class));
    }

    /**
     * Tests if stop() sends a StopEvent.
     */
    @Test
    void testStop() {
        controller.stop();
        verify(canvasEventSink, times(1)).send(any(StopEvent.class));
    }

    /**
     * Tests if loadAllOnnx() correctly loads ONNX files.
     */
    @Test
    void testLoadAllOnnx() {
        File mockFile = mock(File.class);
        when(mockFile.getName()).thenReturn("test.onnx");
        when(mockFile.isDirectory()).thenReturn(false);
        when(mockFile.listFiles()).thenReturn(new File[]{mockFile});

        controller.loadAllOnnx(mockFile);

        verify(model, times(1)).clearClassifiers();
        verify(model, times(1)).addClassifier("test.onnx");
        verify(model, times(1)).setCurrentDir(mockFile);
    }

    /**
     * Tests if sampleCurrentModel() correctly prepares and runs sampling.
     */
    @Test
    void testSampleCurrentModel() {
        File mockFile = mock(File.class);
        when(model.getCurrentDir()).thenReturn(mockFile);
        when(mockFile.getAbsolutePath()).thenReturn("/path/to/file");
        when(model.getSelectedClassifier()).thenReturn("test.onnx");

        controller.sampleCurrentModel();

        verify(canvasEventSink, times(1)).send(any(ClearPoints.class));
        verify(model, times(1)).getCurrentDir();
        verify(model, times(1)).getSelectedClassifier();
    }

    /**
     * Tests if getClassifiers() returns the expected classifier list.
     */
    @Test
    void testGetClassifiers() {
        List<String> classifiers = new ArrayList<>();
        classifiers.add("Classifier1");
        classifiers.add("Classifier2");
        when(model.getClassifiers()).thenReturn(classifiers);

        List<String> result = controller.getClassifiers();

        assertEquals(classifiers, result);
    }

    /**
     * Tests if addClassifier() correctly adds a classifier.
     */
    @Test
    void testAddClassifier() {
        controller.addClassifier("NewClassifier");
        verify(model, times(1)).addClassifier("NewClassifier");
    }

    /**
     * Tests if removeClassifier() correctly removes a classifier.
     */
    @Test
    void testRemoveClassifier() {
        controller.removeClassifier("ClassifierToRemove");
        verify(model, times(1)).removeClassifier("ClassifierToRemove");
    }

    /**
     * Tests if clearClassifiers() correctly clears the classifier list.
     */
    @Test
    void testClearClassifiers() {
        controller.clearClassifiers();
        verify(model, times(1)).clearClassifiers();
    }

    /**
     * Tests if setSelectedClassifier() updates the selected classifier.
     */
    @Test
    void testSetSelectedClassifier() {
        controller.setSelectedClassifier("SelectedClassifier");
        verify(model, times(1)).setSelectedClassifier("SelectedClassifier");
    }

    /**
     * Tests if getNavigationDetails() returns the expected navigation details.
     */
    @Test
    void testGetNavigationDetails() {
        when(model.getNavigationDetails()).thenReturn("Navigation Details");
        String result = controller.getNavigationDetails();
        assertEquals("Navigation Details", result);
    }

    /**
     * Tests if setNavigationDetails() updates the navigation details.
     */
    @Test
    void testSetNavigationDetails() {
        controller.setNavigationDetails("New Navigation Details");
        verify(model, times(1)).setNavigationDetails("New Navigation Details");
    }
}
