FROM maven:3-openjdk-8-slim

# Install git
RUN apt update
RUN apt install -y git

# Set up the test project
RUN mkdir -p /opt/project

RUN cd /opt/project \
   && git clone https://github.com/pdurbin/maven-hello-world

RUN mv /opt/project/maven-hello-world/my-app /opt/project/src

RUN cd /opt/project/src \
   && mvn compile