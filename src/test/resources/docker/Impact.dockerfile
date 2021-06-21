FROM adoptopenjdk/maven-openjdk11

# Install git
RUN apt update
RUN apt install -y git

# Set up the test project
RUN mkdir -p /opt/project

RUN cd /opt/project \
   && git clone https://github.com/oktadev/example-maven-plugin

RUN mv /opt/project/example-maven-plugin /opt/project/src

RUN cd /opt/project/src \
   && mvn clean install