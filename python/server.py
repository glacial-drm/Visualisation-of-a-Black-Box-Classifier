from concurrent import futures
import logging
import sys
import grpc
import grpc._server
import connector_pb2
import connector_pb2_grpc

from sklearn.svm import SVC
from sklearn.model_selection import train_test_split
from SAO import SaveAsONNX

class Connector(connector_pb2_grpc.ConnectorServicer):
    def SayHello(self, request: connector_pb2.HelloRequest, context: grpc._server._Context):
        return connector_pb2.HelloReply(message="Hello, %s!" % request.name)

    def TrainSvc(self, request: connector_pb2.SvcRequest, context: grpc._server._Context):
        return connector_pb2.ModelResults(onnxPath=str(request))


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

