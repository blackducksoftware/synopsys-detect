FROM openjdk:8-jdk-slim

ARG ARTIFACTORY_URL
ARG PIP_VERSION=24.1.2

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src

ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2"

RUN apt-get update -y
RUN apt-get install -y git bash wget unzip
RUN apt-get install -y python3 python3-pip
RUN pip install --upgrade "pip==${PIP_VERSION}"

# Set up the test project
#RUN mkdir -p ${SRC_DIR}
#WORKDIR ${SRC_DIR}
#RUN wget ${ARTIFACTORY_URL}/artifactory/detect-generic-qa-local/pip-test-project.zip
#RUN unzip pip-test-project.zip -d /opt/project/src
#RUN mv pip-test-project/* .
#RUN rm -r pip-test-project pip-test-project.zip

RUN mkdir -p /opt/detect
COPY synopsys-detect-10.0.0-SIGQA4-SNAPSHOT.jar /opt/detect

RUN mkdir -p ${SRC_DIR}
WORKDIR ${SRC_DIR}
RUN git clone -b v2.17.3 --depth 1 https://github.com/ansible/ansible.git ${SRC_DIR}