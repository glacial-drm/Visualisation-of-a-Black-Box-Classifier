from concurrent import futures
import logging
import sys
import grpc
import connector_pb2
import connector_pb2_grpc


class Connector(connector_pb2_grpc.ConnectorServicer):
    def SayHello(self, request, context):
        return connector_pb2.HelloReply(message="Hello, %s!" % request.name)


def serve():
    port = sys.argv[1]
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    connector_pb2_grpc.add_ConnectorServicer_to_server(Connector(), server)
    server.add_insecure_port("[::]:" + port)
    server.start()
    print("Server started, listening on " + port)
    server.wait_for_termination()


if __name__ == "__main__":
    logging.basicConfig()
    serve()

