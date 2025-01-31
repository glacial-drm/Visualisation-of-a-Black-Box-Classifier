package com.blackbox.app;

import io.grpc.ConnectivityState;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.util.Random;

import com.blackbox.model.PointData;
import com.blackbox.proto.connector.*;
import com.blackbox.proto.connector.ConnectorGrpc.ConnectorBlockingStub;
import com.blackbox.proto.connector.SvcRequest.Kernel;

public class Connector {
    private final boolean debug = false;
    private final int port = 50000 + new Random().nextInt(10000);
    Process server;
    ManagedChannel channel;
    ConnectorBlockingStub stub;

    public Connector() {
        try {
            ProcessBuilder serverBuilder = new ProcessBuilder("python3", "../python/server.py", Integer.toString(port));
            if (debug) serverBuilder = serverBuilder.inheritIO();
            server = serverBuilder.start();
        } catch (Exception e) {
            System.err.println("Can't proceed, failed to start python server:");
            System.err.println(e);
            return;
        }

        channel = Grpc.newChannelBuilder("localhost:" + Integer.toString(port), InsecureChannelCredentials.create())
            .build();

        ConnectivityState lastState = null;
        while (lastState != ConnectivityState.READY) {
            ConnectivityState state = channel.getState(true);
            if (state != lastState) {
                if (debug) System.out.println(state);
                lastState = state;
            }
        }

        stub = ConnectorGrpc.newBlockingStub(channel);
    }

    public String sayHello(String name) {
        HelloRequest req = HelloRequest.newBuilder().setName(name).build();
        HelloReply res = stub.sayHello(req);
        return res.getMessage();
    }

    public String trainSvc() {
        Points points = Points.newBuilder()
            .addPoints(convertPoint(new PointData(10, 11, 1)))
            .addPoints(convertPoint(new PointData(12, 13, 2))).build();
        SvcRequest req = SvcRequest.newBuilder().setPoints(points).setKernel(Kernel.RBF).build();
        ModelResults res = stub.trainSvc(req);
        return res.getOnnxPath();
    }

    public static Point convertPoint(PointData point) {
        return Point.newBuilder()
            .setLabel(point.label)
            .addValues(point.feature1)
            .addValues(point.feature2)
            .build();
    }

    public void close() {
        server.destroy();
    }
}
