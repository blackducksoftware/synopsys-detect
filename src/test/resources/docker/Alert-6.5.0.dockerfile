FROM gradle:6.8.2-jdk11

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src
ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2"

USER root

# Install git
RUN apt-get update
RUN apt-get install -y git

# Install node
RUN apt-get install -y build-essential apt-transport-https lsb-release ca-certificates curl
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash -
RUN apt-get install -y nodejs

# Set up the test project
RUN mkdir -p ${SRC_DIR}

RUN git clone --depth 1 -b 6.5.0 https://github.com/blackducksoftware/blackduck-alert ${SRC_DIR}

RUN cd ${SRC_DIR} \
   && ./gradlew build