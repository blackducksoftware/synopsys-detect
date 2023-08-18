FROM gradle:5.2.0-jdk8-slim

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src
#RUN mkdir /opt/detect
#COPY synopsys-detect-9.0.0-SIGQA10-SNAPSHOT.jar /opt/detect
ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2"
USER root

# Install git
RUN apt-get update
RUN apt-get install -y git bash

# Install Go
WORKDIR /usr/local
RUN wget -q https://golang.org/dl/go1.16.6.linux-amd64.tar.gz
RUN mkdir go1.16.6
RUN tar -C go1.16.6 -xzf go1.16.6.linux-amd64.tar.gz
RUN rm go1.16.6.linux-amd64.tar.gz

# Set up the test project
RUN mkdir -p ${SRC_DIR}
RUN git clone -b v1.5.4 --depth 1 https://github.com/Masterminds/squirrel.git ${SRC_DIR}