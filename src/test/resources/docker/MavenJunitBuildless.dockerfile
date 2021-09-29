FROM maven:3-openjdk-8-slim

# Install git
RUN apt update
RUN apt install -y git

# Set up the test project
RUN mkdir -p /opt/project

RUN cd /opt/project \
   && git clone -b r4.13.2 https://github.com/junit-team/junit4.git

RUN mv /opt/project/junit4 /opt/project/src

RUN cd /opt/project/src