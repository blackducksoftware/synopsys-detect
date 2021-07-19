FROM gradle:5.2.0-jdk11-slim

USER root

# Install git
RUN apt update
RUN apt install -y git

# Set up the test project
RUN mkdir -p /opt/project

RUN cd /opt/project \
   && git clone -b 7.1.0 https://github.com/blackducksoftware/synopsys-detect

RUN mv /opt/project/synopsys-detect /opt/project/src

ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2"

RUN cd /opt/project/src \
   && ./gradlew build