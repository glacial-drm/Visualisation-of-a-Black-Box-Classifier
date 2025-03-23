package com.blackbox.app;

import io.grpc.ConnectivityState;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.tinylog.Logger;

import com.blackbox.model.PointData;
import com.blackbox.proto.connector.*;
import com.blackbox.proto.connector.ConnectorGrpc.ConnectorBlockingStub;
import com.google.protobuf.GeneratedMessage.Builder;


/**
 * Connects to the python server through GRPC.
 */
public class Connector {
    private final int port = 50000 + new Random().nextInt(10000);
    Process server;
    ManagedChannel channel;
    ConnectorBlockingStub stub;

    public Connector() {
        try {
            ProcessBuilder serverBuilder = createProcessBuilder(Integer.toString(port));
            if (Logger.isDebugEnabled()) serverBuilder.inheritIO();
            server = serverBuilder.start();
        } catch (Exception e) {
            Logger.error("Can't proceed, failed to start python server:", e);
            return;
        }

        channel = Grpc.newChannelBuilder("localhost:" + Integer.toString(port), InsecureChannelCredentials.create())
            .build();

        ConnectivityState lastState = null;
        while (lastState != ConnectivityState.READY) {
            ConnectivityState state = channel.getState(true);
            if (state != lastState) {
                Logger.debug(state);
                lastState = state;
            }
        }

        stub = ConnectorGrpc.newBlockingStub(channel);
    }

    /**
     * The python scripts are stored in resources,
     * meaning they could be inside the .jar file.
     * Python can't execute from their directly (is is effectively a zip),
     * so we copy the python scripts to a temporary folder before execution.
     *
     * Returns the absolute path of the server python script.
     */
    private String loadPython() throws IOException {
        String tmpPath = System.getProperty("java.io.tmpdir") + File.separator + "blackbox-python";

        // Delete old tmp folder
        if (Path.of(tmpPath).toFile().exists()) {
            Files.walk(Path.of(tmpPath))
                .sorted(Comparator.reverseOrder())
                .forEachOrdered(path -> path.toFile().delete());
        }

        // Create tmp folder
        File tmpDir = new File(tmpPath);
        tmpDir.mkdirs();
        Logger.info("Created tmp folder at {}", tmpDir.getAbsolutePath());

        // All the needed python files
        // Auto generating this from resources was not easy
        String[] paths = new String[] {
            "connector_pb2.py",
            "connector_pb2.pyi",
            "connector_pb2_grpc.py",
            "SAO.py",
            "server.py",
            "TrainModel.py",
            "Models.py"
        };

        // Copy from resources to the temporary folder
        for (String path : paths) {
            InputStream input = getClass().getResourceAsStream("/python/" + path);

            File output = new File(tmpDir, path);
            output.createNewFile();

            try (FileWriter outputWriter = new FileWriter(output)) {
                outputWriter.write(new String(input.readAllBytes()));
            }
        }

        return tmpDir.getAbsolutePath() + File.separator + "server.py";
    }

    private ProcessBuilder createProcessBuilder(String port) {
        String serverPath;
        try {
            serverPath = loadPython();
        } catch (Exception e) {
            Logger.error("Failed to load python : {}", e);
            return null;
        }

        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("windows")) {
            return new ProcessBuilder("python", serverPath, port);
        } else {
            return new ProcessBuilder("python3", serverPath, port);
        }
    }

    public String trainModel(Builder<?> request, PointData[] trainingData) {
        List<Point> points = Arrays.stream(trainingData).map(point -> convertPoint(point)).toList();
        Points pointsProto = Points.newBuilder().addAllPoints(points).build();

        ModelResults res;

        if (request instanceof SvmRequest.Builder svm) {
            SvmRequest modelRequest = svm.setPoints(pointsProto).build();
            res = stub.trainSvm(modelRequest);
        } else if (request instanceof DecisionTreeRequest.Builder builder) {
            DecisionTreeRequest modelRequest = builder.setPoints(pointsProto).build();
            res = stub.trainDecisionTree(modelRequest);
        } else if (request instanceof RandomForestRequest.Builder builder) {
            RandomForestRequest modelRequest = builder.setPoints(pointsProto).build();
            res = stub.trainRandomForest(modelRequest);
        } else if (request instanceof KNeighborsRequest.Builder builder) {
            KNeighborsRequest modelRequest = builder.setPoints(pointsProto).build();
            res = stub.trainKNeighbors(modelRequest);
        } else if (request instanceof LogisticRegressionRequest.Builder builder) {
            LogisticRegressionRequest modelRequest = builder.setPoints(pointsProto).build();
            res = stub.trainLogisticRegression(modelRequest);
        } else {
            return null;
        }

        return res.getOnnxPath();
    }

    public static Point convertPoint(PointData point) {
        Point.Builder builder = Point.newBuilder().setLabel(point.label);
        for (float feature : point.features) {
            builder.addValues(feature);
        }
        return builder.build();
    }

    public void close() {
        server.destroy();
    }
}
