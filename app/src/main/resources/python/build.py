from precheck import check_all_imports
import subprocess
import sys
import os

def build_protobuf_files():
    proto_file = "../../proto/connector.proto"
    output_dir = "."
    proto_include = "-I../../proto"

    command = [
        sys.executable, "-m", "grpc_tools.protoc",
        proto_include,
        f"--python_out={output_dir}",
        f"--pyi_out={output_dir}",
        f"--grpc_python_out={output_dir}",
        proto_file
    ]

    try:
        subprocess.run(command, check=True)
        print("gRPC code generated successfully!")
    except subprocess.CalledProcessError as e:
        print(f"Error during gRPC code generation: {e}")
        raise e

def main():
    # Set the current working directory to where this file is
    os.chdir(os.path.dirname(os.path.realpath(__file__)))

    print("Checking if all library are installed")
    check_all_imports()
    print("All packages are installed")

    print("Building protobuf files")
    build_protobuf_files()
    print("Built protobuf files")

if __name__ == "__main__":
    try:
        main()
    except ImportError:
        sys.exit(1)
    except subprocess.CalledProcessError:
        sys.exit(2)

