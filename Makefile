# !/usr/bin/make - f

# https://github.com/utensils/docker-opengl

# MIT License

# Copyright (c) 2018 James Brink

# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:

# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

SHELL                   := /usr/bin/env bash
SED                     := $(shell [[ `command -v gsed` ]] && echo gsed || echo sed)
REPO_API_URL            ?= https://hub.docker.com/v2
REPO_NAMESPACE          ?= utensils
REPO_USERNAME           ?= utensils
IMAGE_NAME              ?= opengl
BASE_IMAGE              ?= alpine:latest
LLVM_VERSION            ?= 17
TAG_SUFFIX              ?= $(shell echo "-$(BASE_IMAGE)" | $(SED) 's|:|-|g' | $(SED) 's|/|_|g' 2>/dev/null )
VCS_REF                 := $(shell git rev-parse --short HEAD)
BUILD_DATE              := $(shell date -u +"%Y-%m-%dT%H:%M:%SZ")
PLATFORMS               ?= linux/amd64
RELEASES                ?= latest stable 24.2.6
STABLE                  ?= 24.2.6
LATEST                  ?= 24.2.6
BUILD_PROGRESS          ?= auto
BUILD_TYPE              ?= release
BUILD_OPTIMIZATION      ?= 3

# Default target is to build all defined Mesa releases.
.PHONY: default
default: $(STABLE)

.PHONY: all
all: $(RELEASES)

# Build base images for all releases using buildx.
.PHONY: $(RELEASES)
.SILENT: $(RELEASES)
$(RELEASES):
	if [ "$(@)" == "stable" ]; \
	then \
		MESA_VERSION="$(STABLE)"; \
	elif [ "$(@)" == "latest" ]; \
	then \
		MESA_VERSION="$(LATEST)"; \
	else \
		MESA_VERSION="$(@)"; \
	fi; \
	docker buildx build \
		--build-arg BASE_IMAGE=$(BASE_IMAGE) \
		--build-arg BUILD_DATE=$(BUILD_DATE) \
		--build-arg BUILD_OPTIMIZATION=$(BUILD_OPTIMIZATION) \
		--build-arg BUILD_TYPE=$(BUILD_TYPE) \
		--build-arg LLVM_VERSION=$(LLVM_VERSION) \
		--build-arg MESA_VERSION="$$MESA_VERSION"  \
		--build-arg VCS_REF=$(VCS_REF) \
		--cache-from $(REPO_NAMESPACE)/$(IMAGE_NAME):$(@)$(TAG_SUFFIX) \
		--tag $(REPO_NAMESPACE)/$(IMAGE_NAME):$(@)$(TAG_SUFFIX) \
		--tag $(REPO_NAMESPACE)/$(IMAGE_NAME):$(@) \
		--platform=$(PLATFORMS) \
		--progress=$(BUILD_PROGRESS) \
		--file Dockerfile .

