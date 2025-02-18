import importlib.metadata

def check_import(library):
    version = importlib.metadata.version(library)
    if version:
        print(f"Found : {library} version: {version}")
    else:
        raise ImportError(f"Failed to import: {library}")

def main():
    print("Checking if all library are installed")

    with open("python/requirements.txt") as file:
        libraries = file.readlines()
        for lib in libraries:
            try:
                check_import(lib.rstrip())
            except ImportError:
                print(f"Failed to find {lib.rstrip()} please install all packages from \
                      requirements.txt using python install -m requirements.txt \
                      If this is not the problem ensure you have active your virtual environment \
                      if you are using one")
                raise ImportError(f"failed to import {lib.rstrip()}")
                

if __name__ == "__main__":
    try:
        main()
    except ImportError as e:
        raise e