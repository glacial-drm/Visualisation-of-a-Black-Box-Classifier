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

# Very heavily edited by Arlo Blythe
# Converted to ubuntu from alpine, among other things

ARG BASE_IMAGE=ubuntu:latest
FROM ${BASE_IMAGE} AS builder

# Enable source repos
RUN set -xe; sed -i 's/Types: deb/Types: deb deb-src/g' /etc/apt/sources.list.d/ubuntu.sources;
# Install mesa build deps
RUN set -xe; apt-get update;
RUN set -xe; apt-get build-dep -y mesa;
RUN set -xe; apt-get install -y git;

# Clone Mesa source repo
ARG MESA_VERSION
RUN set -xe; \
    mkdir -p /var/tmp/build; \
    cd /var/tmp/build/; \
    git clone --depth=1 --branch=mesa-${MESA_VERSION} https://gitlab.freedesktop.org/mesa/mesa.git;

# Build Mesa from source.
ARG BUILD_TYPE=release
ARG BUILD_OPTIMIZATION=3
RUN set -xe; \
    cd /var/tmp/build/mesa; \
    libtoolize; \
    if [ "$(uname -m)" ==  "aarch64" ] || [ "$(uname -m)" == "armv7l" ]; \
    then \
        galium_drivers=swrast,llvmpipe; \
    else \
        galium_drivers=swrast,llvmpipe; \
    fi ;\
    meson setup \
        --buildtype=${BUILD_TYPE} \
        --prefix=/usr/local \
        --sysconfdir=/etc \
        -D b_ndebug=true \
        -D egl=enabled \
        -D gallium-nine=false \
        -D gbm=enabled \
        -D gles1=disabled \
        -D gles2=enabled \
        -D opengl=true \
        -D dri-drivers-path=/usr/local/lib/xorg/modules/dri \
        -D dri3=enabled  \
        -D egl=enabled \
        -D gallium-drivers="$galium_drivers" \
        -D gbm=enabled \
        -D glx=dri \
        -D llvm=enabled \
        -D lmsensors=disabled \
        -D optimization=${BUILD_OPTIMIZATION} \
        -D platforms=x11,wayland \
        -D shared-glapi=enabled \
        -D shared-llvm=enabled \
        -D vulkan-drivers= \
        build/; \
    ninja -C build/ -j $(getconf _NPROCESSORS_ONLN); \
    ninja -C build/ install;

# Create fresh image
ARG BASE_IMAGE=ubuntu:latest
FROM ${BASE_IMAGE}

# Copy the Mesa build & entrypoint script from previous stage
COPY --from=builder /usr/local /usr/local

# Install runtime dependencies for Mesa and link xorg dri modules
RUN set -xe; apt-get update;
RUN set -xe; \
    apt-get install -y \
        openjdk-21-jre \
        x11-xkb-utils \
        x11-utils \
        x11-xserver-utils \
        libxrandr2 \
        xvfb \
        libzstd1;
RUN set -xe; apt-get clean;
RUN set -xe; mkdir -p /usr/lib/xorg/modules/dri;
RUN set -xe; ln -sf /usr/local/lib/xorg/modules/dri/* /usr/lib/xorg/modules/dri/;

# Labels / Metadata.
ARG MESA_VERSION
LABEL \
    org.opencontainers.image.authors="James Brink <brink.james@gmail.com>" \
    org.opencontainers.image.description="Mesa3D OpenGL Software Drivers." \
    org.opencontainers.image.source="https://github.com/utensils/docker-opengl" \
    org.opencontainers.image.title="Mesa3D OpenGL ${MESA_VERSION}" \
    org.opencontainers.image.vendor="Utensils" \
    org.opencontainers.image.version="${MESA_VERSION}"

# Setup our environment variables.
ENV \
    DISPLAY=":99" \
    GALLIUM_DRIVER="llvmpipe" \
    LD_LIBRARY_PATH="/usr/local/lib:$LD_LIBRARY_PATH" \
    LIBGL_ALWAYS_SOFTWARE="1" \
    LP_DEBUG="" \
    LP_NO_RAST="false" \
    LP_NUM_THREADS="" \
    LP_PERF="" \
    MESA_VERSION="${MESA_VERSION}" \
    XVFB_WHD="1920x1080x24"

# Copy our entrypoint into the container.
COPY ./entrypoint.sh /usr/local/bin/entrypoint.sh

# Set the entrypoint script
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]

