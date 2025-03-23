from concurrent import futures
import logging
import sys
import grpc
import grpc._server
import connector_pb2
import connector_pb2_grpc

import numpy as np
from Models import SVMClassifier
from Models import DecisionTreeClassifierWrapper
from Models import RFClassifier
from Models import KNClassifier

def parsePoints(points: connector_pb2.Points):
    points = points.points

    X = np.zeros((len(points), len(points[0].values)), dtype=np.float32)
    y = np.zeros(len(points), dtype=np.int16)
    for i, point in enumerate(points):
        X[i] = point.values
        y[i] = int(point.label)

    return X, y

# Useful for sklearn models that have different behaviour
# when taking in an int or a float.
# Will return an int when a whole number, the float if not.
def splitIntFloat(value):
    epsilon = 0.000001
    if (value % 1 < epsilon) or (1 - value % 1 < epsilon):
        return int(round(value))
    else:
        return value

class Connector(connector_pb2_grpc.ConnectorServicer):
    def TrainSvm(self, request: connector_pb2.SvmRequest, context: grpc._server._Context):
        X, y = parsePoints(request.points)
        kernel = connector_pb2.SvmRequest.Kernel.Name(request.kernel).lower()

        model = SVMClassifier(
            kernel,
            degree = request.polyDegree,
            C=1.0,
            gamma="scale",
            test_size=0.2,
            random_state=42
        )
        model.fit(X, y)
        model.save(X, y)

        return connector_pb2.ModelResults(onnxPath=model.output_name)
    def TrainDecisionTree(self, request: connector_pb2.DecisionTreeRequest, context: grpc._server._Context):
        X, y = parsePoints(request.points)

        model = DecisionTreeClassifierWrapper(
            criterion = connector_pb2.Criterion.Name(request.criterion).lower(),
            max_depth = request.maxDepth if request.maxDepth != -1 else None,
        )

        model.fit(X, y)
        model.save(X, y)

        return connector_pb2.ModelResults(onnxPath=model.output_name)
    def TrainRandomForest(self, request: connector_pb2.RandomForestRequest, context: grpc._server._Context):
        X, y = parsePoints(request.points)

        model = RFClassifier(
            n_estimators = request.nEstimators,
            criterion = connector_pb2.Criterion.Name(request.criterion).lower(),
            max_depth = request.maxDepth if request.maxDepth != -1 else None,
            min_samples_split = splitIntFloat(request.minSamplesSplit),
            min_samples_leaf = splitIntFloat(request.minSamplesLeaf),
            max_features = None
                if request.maxFeatures == connector_pb2.RandomForestRequest.MaxFeatures.N_FEATURES
                else request.maxFeaturesVal
                if request.maxFeatures == connector_pb2.RandomForestRequest.MaxFeatures.VALUE
                else connector_pb2.RandomForestRequest.MaxFeatures.Name(request.maxFeatures).lower(),
        )

        model.fit(X, y)
        model.save(X, y)

        return connector_pb2.ModelResults(onnxPath=model.output_name)

    def TrainKNeighbors(self, request: connector_pb2.KNeighborsRequest, context: grpc._server._Context):
        X, y = parsePoints(request.points)

        model = KNClassifier(
            n_neighbors = request.nNeighbors,
            weights = connector_pb2.KNeighborsRequest.Weights.Name(request.weights).lower(),
            algorithm = connector_pb2.KNeighborsRequest.Algorithm.Name(request.algorithm).lower(),
            leaf_size = request.leaf_size,
            p = request.p,
            metric = connector_pb2.KNeighborsRequest.Metric.Name(request.metric).lower(),
            n_jobs = request.nJobs
        )

        model.fit(X, y)
        model.save(X, y)

        return connector_pb2.ModelResults(onnxPath=model.output_name)

def serve():
    if len(sys.argv) != 2:
        print("Invalid args")
        print("Usage : server.py <PORT>")
        exit(1)

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

