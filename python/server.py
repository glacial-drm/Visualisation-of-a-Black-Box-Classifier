from concurrent import futures
import logging
import sys
import grpc
import grpc._server
import connector_pb2
import connector_pb2_grpc

import numpy as np
from TrainSVM import SVMClassifier

def parsePoints(points: connector_pb2.Points):
    points = points.points

    X = np.zeros((len(points), len(points[0].values)), dtype=np.float32)
    y = np.zeros(len(points), dtype=np.int16)
    for i, point in enumerate(points):
        X[i] = point.values
        y[i] = int(point.label)

    return X, y

class Connector(connector_pb2_grpc.ConnectorServicer):
    def TrainSvc(self, request: connector_pb2.SvcRequest, context: grpc._server._Context):
        X, y = parsePoints(request.points)
        kernel = connector_pb2.SvcRequest.Kernel.Name(request.kernel).lower()

        svm = SVMClassifier(kernel, C=1.0, gamma="scale", test_size=0.2, random_state=42)
        svm.fit(X, y)
        svm.save(X, y)
        onnxPath = "SVM.onnx"

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

