# These need to be set as env variables
REGISTRY ?= registry
REGISTRY_USER ?= user
REGISTRY_PASSWD ?= password

TAG = utensils/opengl
PUBLISH_TAG = $(REGISTRY)/$(REGISTRY_USER)/$(TAG)

test:
	echo "$(REGISTRY)"
	echo "$(REGISTRY_USER)"
	echo "$(REGISTRY_PASSWD)"

build:
	docker buildx build \
		--build-arg BASE_IMAGE=alpine:latest \
		--build-arg LLVM_VERSION=17 \
		--build-arg MESA_VERSION=24.2.6 \
		--tag $(TAG) \
		--file Dockerfile .

upload: build
	docker tag $(TAG) $(PUBLISH_TAG)
	docker login "$(REGISTRY)" \
		--username "$(REGISTRY_USER)" \
		--password "$(REGISTRY_PASSWD)"
	docker image push $(PUBLISH_TAG)

