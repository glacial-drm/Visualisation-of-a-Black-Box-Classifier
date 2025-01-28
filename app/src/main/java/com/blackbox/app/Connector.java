package com.blackbox.app;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.HelloReply;

public class Connector {
    Process server;
    ManagedChannel channel;
    GreeterBlockingStub stub;

    public Connector() {
        try {
            server = new ProcessBuilder("python3", "python/server.py")
                .start();
        } catch (Exception e) {
            System.err.println("Can't proceed, failed to start python server:");
            System.err.println(e);
            return;
        }

        channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create())
            .build();

        stub = GreeterGrpc.newBlockingStub(channel);
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
