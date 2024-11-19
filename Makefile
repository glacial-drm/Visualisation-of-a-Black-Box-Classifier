# These need to be set as env variables
REGISTRY ?= registry
REGISTRY_USER ?= user
REGISTRY_PASSWD ?= password

TAG = utensils/opengl
PUBLISH_TAG = $(REGISTRY)/$(REGISTRY_USER)/$(TAG)

build:
	docker buildx build \
		--build-arg BASE_IMAGE=alpine:latest \
		--build-arg LLVM_VERSION=17 \
		--build-arg MESA_VERSION=24.2.6 \
		--tag $(TAG) \
		--file Dockerfile .

login:
	docker login "$(REGISTRY)" \
		--username "$(REGISTRY_USER)" \
		--password "$(REGISTRY_PASSWD)"

upload: build login
	docker tag $(TAG) $(PUBLISH_TAG)
	docker image push $(PUBLISH_TAG)

test: login
	docker run --rm -v ./:/src -i "$(REGISTRY)/$(REGISTRY_USER)/$(TAG)" " \
		cd /src; \
		GRADLE_USER_HOME="$(shell pwd)/.gradle"; \
		export GRADLE_USER_HOME; \
		gradle check; \
	"

