import importlib.metadata
import subprocess
import sys

def check_import(library):
    version = importlib.metadata.version(library)
    if version:
        print(f"Found : {library} version: {version}")
    else:
        raise ImportError(f"Failed to import: {library}")

def build_protobuf_files():
    proto_file = "app/src/main/proto/connector.proto"
    output_dir = "python"
    proto_include = "-Iapp/src/main/proto"

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
    print("Checking if all library are installed")

    with open("python/requirements.txt") as file:
        libraries = file.readlines()
        for lib in libraries:
            try:
                check_import(lib.rstrip())
            except ImportError:
                print(f"Failed to find {lib.rstrip()} please install all packages from " +
                      f"requirements.txt using python install -m requirements.txt\n"
                      f"If this is not the problem ensure you have active your virtual environment "
                      f"if you are using one")
                raise ImportError(f"failed to import {lib.rstrip()}")
                
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