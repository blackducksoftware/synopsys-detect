FROM --platform=linux/amd64 openjdk:8-jdk

ARG goVersion=1.16.6

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src

# Install git
RUN apt-get update
RUN apt-get install -y git bash

# Install Go@1.16.6
WORKDIR /usr/local
RUN wget -q https://golang.org/dl/go${goVersion}.linux-amd64.tar.gz
RUN mkdir go${goVersion}
RUN tar -C go${goVersion} -xzf go${goVersion}.linux-amd64.tar.gz
RUN rm go${goVersion}.linux-amd64.tar.gz

# Set up the test project
RUN mkdir -p ${SRC_DIR}

RUN git clone --depth 1 https://github.com/Masterminds/squirrel.git ${SRC_DIR}