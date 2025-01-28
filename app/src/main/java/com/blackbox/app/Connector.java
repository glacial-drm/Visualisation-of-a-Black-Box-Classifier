package com.blackbox.app;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import com.blackbox.proto.connector.*;
import com.blackbox.proto.connector.ConnectorGrpc.ConnectorBlockingStub;

public class Connector {
    private int port = 50052;
    Process server;
    ManagedChannel channel;
    ConnectorBlockingStub stub;

    public Connector() {
        try {
            server = new ProcessBuilder("python3", "../python/server.py", Integer.toString(port))
                .start();
        } catch (Exception e) {
            System.err.println("Can't proceed, failed to start python server:");
            System.err.println(e);
            return;
        }

        channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create())
            .build();

        stub = ConnectorGrpc.newBlockingStub(channel);
    }

    public String sayHello(String name) {
        HelloRequest req = HelloRequest.newBuilder().setName(name).build();
        HelloReply res = stub.sayHello(req);
        return res.getMessage();
    }

    public void close() {
        server.destroy();
    }
}
