FROM gradle:8.2.1-jdk11

ARG artifactory_url

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src

ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2"

# Set up the test project
RUN mkdir -p ${SRC_DIR}

RUN wget ${artifactory_url}/artifactory/detect-generic-qa-local/gradle-rich-versions-project.zip
RUN unzip gradle-rich-versions-project.zip -d /opt/project/src
RUN rm gradle-rich-versions-project.zip

RUN cd ${SRC_DIR}