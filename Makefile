# These need to be set as env variables
REGISTRY ?= registry
REGISTRY_USER ?= user
REGISTRY_PASSWD ?= password

TAG = utensils/opengl:0.2
PUBLISH_TAG = $(REGISTRY)/$(REGISTRY_USER)/$(TAG)

build:
	@echo -e "===== Building Docker image =====\n"

	docker buildx build \
		--build-arg BASE_IMAGE=ubuntu:latest \
		--build-arg MESA_VERSION=24.2.6 \
		--tag $(TAG) \
		--file Dockerfile .
	docker tag $(TAG) $(PUBLISH_TAG)

	@echo -e "\n\n"

login:
	@echo -e "===== Logging into Docker registry =====\n"

	docker login "$(REGISTRY)" \
		--username "$(REGISTRY_USER)" \
		--password "$(REGISTRY_PASSWD)"

	@echo -e "\n\n"

upload: build login
	@echo -e "===== Uploading Docker image =====\n"

	docker image push $(PUBLISH_TAG)

	@echo -e "\n\n"

test: login
	@echo -e "===== Running gradle tests in Docker =====\n"

	docker run --rm -v ./:/src -i "$(REGISTRY)/$(REGISTRY_USER)/$(TAG)" " \
		cd /src; \
		export GRADLE_USER_HOME="/src/.gradle"; \
		export ENFORCE_VENDOR=1; \
		/src/gradlew check; \
	"

	@echo -e "\n\n"

proto:
	python -m grpc_tools.protoc -Iapp/src/main/proto --python_out=python --pyi_out=python --grpc_python_out=python app/src/main/proto/connector.proto

