FROM gradle:8.2.1-jdk11

ARG artifactory_url

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src

ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2"

RUN apt-get update

RUN apt-get install -y maven
# Set up the test project
RUN mkdir -p ${SRC_DIR}

RUN wget ${artifactory_url}/artifactory/detect-generic-qa-local/autonomous-project-test.zip
RUN unzip autonomous-project-test.zip -d /opt/project/src
RUN rm autonomous-project-test.zip

RUN cd ${SRC_DIR}