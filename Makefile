# These need to be set as env variables
REGISTRY ?= registry
REGISTRY_USER ?= user
REGISTRY_PASSWD ?= password

TAG = utensils/opengl
PUBLISH_TAG = $(REGISTRY)/$(REGISTRY_USER)/$(TAG)

build:
	@echo -e "===== Building Docker image =====\n"

	docker buildx build \
		--build-arg BASE_IMAGE=alpine:latest \
		--build-arg LLVM_VERSION=17 \
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
		gradle check; \
	"

	@echo -e "\n\n"

