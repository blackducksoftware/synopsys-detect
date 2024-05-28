FROM debian:bookworm-20240110-slim

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get -y install gawk wget git-core \
    diffstat unzip texinfo build-essential \
    chrpath socat cpio python3 python3-pip \
    python3-pexpect xz-utils debianutils iputils-ping \
    libsdl1.2-dev xterm tar locales file lz4 zstd curl \
    locales

# Set the locale
RUN sed -i '/en_US.UTF-8/s/^# //g' /etc/locale.gen && locale-gen
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

# Install Java
RUN apt-get -y install openjdk-17-jdk

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src

# Set up the test project
RUN mkdir -p ${SRC_DIR}
RUN git clone -b yocto-4.3.1 --depth 1 https://github.com/yoctoproject/poky.git ${SRC_DIR}

RUN cd ${SRC_DIR} \
  && sed -i 's/INHERIT += "sanity"/#INHERIT += "sanity"/g' meta/conf/sanity.conf
