FROM --platform=linux/amd64 openjdk:8-jdk

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src

# Install git
RUN apt-get update
RUN apt-get install -y git bash

# Install Go@1.16.6
WORKDIR /usr/local
RUN wget -q "https://golang.org/dl/go1.16.6.linux-amd64.tar.gz"
RUN mkdir go1.16.6
RUN tar -C go1.16.6 -xzf go1.16.6.linux-amd64.tar.gz
RUN rm go1.16.6.linux-amd64.tar.gz

# Set up the test project
RUN mkdir -p ${SRC_DIR}

RUN git clone --depth 1 https://github.com/Masterminds/squirrel.git ${SRC_DIR}