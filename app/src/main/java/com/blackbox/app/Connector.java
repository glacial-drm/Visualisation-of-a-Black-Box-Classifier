package com.blackbox.app;

import io.grpc.ConnectivityState;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.util.Arrays;
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

    private ProcessBuilder createProcessBuilder(String port) {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("windows")) {
            return new ProcessBuilder("python", "app/src/main/resources/python/server.py", port);
        } else {
            return new ProcessBuilder("python3", "app/src/main/resources/python/server.py", port);
        }
    }

    public String trainModel(Builder<?> request, PointData[] trainingData) {
        List<Point> points = Arrays.stream(trainingData).map(point -> convertPoint(point)).toList();
        Points pointsProto = Points.newBuilder().addAllPoints(points).build();

        ModelResults res;

        if (request instanceof SvcRequest.Builder svc) {
            SvcRequest svcRequest = svc.setPoints(pointsProto).build();
            res = stub.trainSvc(svcRequest);
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
