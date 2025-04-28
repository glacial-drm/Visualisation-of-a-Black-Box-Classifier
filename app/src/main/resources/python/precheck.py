import importlib.metadata
import sys
import os

def check_import(library):
    version = importlib.metadata.version(library)
    if version:
        print(f"Found : {library} version: {version}")
    else:
        raise ImportError(f"Failed to import: {library}")

def check_all_imports():
    """Will check all imports from requirements.txt.
    If a library is missing it will print instructions for the user,
    and throw an ImportError"""

    with open("requirements.txt") as file:
        libraries = file.readlines()
        for lib in libraries:
            try:
                check_import(lib.rstrip())
            except ImportError:
                print(f"Failed to find '{lib.rstrip()}' please install all packages from " +
                      f"requirements.txt using (pip install -r requirements.txt)\n"
                      f"If this is not the problem ensure you have activated your virtual environment "
                      f"if you are using one")
                raise ImportError(f"failed to import {lib.rstrip()}")

def main():
    # Set the current working directory to where this file is
    os.chdir(os.path.dirname(os.path.realpath(__file__)))

    print("Checking if all library are installed")
    check_all_imports()
    print("All packages are installed")

if __name__ == "__main__":
    try:
        main()
    except ImportError:
        sys.exit(1)

