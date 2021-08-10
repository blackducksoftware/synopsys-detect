FROM gradle:5.2.0-jdk11-slim

USER root

# Install git
RUN apt update
RUN apt install -y git

# Install node
RUN apt install -y build-essential apt-transport-https lsb-release ca-certificates curl
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash -
RUN apt install -y nodejs

# Set up the test project
RUN mkdir -p /opt/project

RUN cd /opt/project \
   && git clone -b 6.5.0 https://github.com/blackducksoftware/blackduck-alert

RUN mv /opt/project/blackduck-alert /opt/project/src

ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2"

RUN cd /opt/project/src \
   && ./gradlew build