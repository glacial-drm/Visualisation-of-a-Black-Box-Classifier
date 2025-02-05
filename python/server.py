from concurrent import futures
import logging
import sys
import grpc
import grpc._server
import connector_pb2
import connector_pb2_grpc

import numpy as np
from sklearn.svm import SVC
from sklearn.model_selection import train_test_split
from SAO import SaveAsONNX

def trainSvc(X, y, kernel: str) -> str:
    # This should return the onnx path, but this is useful for debug
    return str(X) + "\n" + str(y) + "\n" + kernel

def parsePoints(points: connector_pb2.Points):
    points = points.points

    X = np.zeros((len(points), len(points[0].values)), dtype=np.float32)
    y = np.zeros(len(points), dtype=np.float32)
    for i, point in enumerate(points):
        X[i] = point.values
        y[i] = point.label

    return X, y

class Connector(connector_pb2_grpc.ConnectorServicer):
    def SayHello(self, request: connector_pb2.HelloRequest, context: grpc._server._Context):
        return connector_pb2.HelloReply(message="Hello, %s!" % request.name)

    def TrainSvc(self, request: connector_pb2.SvcRequest, context: grpc._server._Context):
        X, y = parsePoints(request.points)
        kernel = connector_pb2.SvcRequest.Kernel.Name(request.kernel).lower()

        onnxPath = trainSvc(X, y, kernel)

        return connector_pb2.ModelResults(onnxPath=onnxPath)


def serve():
    port = sys.argv[1]
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    connector_pb2_grpc.add_ConnectorServicer_to_server(Connector(), server)
    server.add_insecure_port("localhost:" + port)
    server.start()
    print("Server started, listening on " + port)
    server.wait_for_termination()


if __name__ == "__main__":
    logging.basicConfig()
    serve()

