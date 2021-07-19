FROM gradle:5.2.0-jdk8-slim

USER root

# Install git
RUN apt update
RUN apt install -y git

# Set up the test project
RUN mkdir -p /opt/project

RUN cd /opt/project \
   && git clone https://github.com/jabedhasan21/java-hello-world-with-gradle

RUN mv /opt/project/java-hello-world-with-gradle /opt/project/src

ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2"

RUN cd /opt/project/src \
   && ./gradlew build