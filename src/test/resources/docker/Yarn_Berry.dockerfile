FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-ubuntu-slim

RUN apt update
RUN apt install -y curl apt-transport-https make g++

# Install git
RUN apt install -y git

# Install npm
RUN curl -sL https://deb.nodesource.com/setup_current.x | bash -
RUN apt-get install -y nodejs

# Install yarn
RUN npm install -g yarn

# Set up the test project
RUN mkdir -p /opt/project

RUN cd /opt/project \
   && git clone -b @yarnpkg/shell/2.4.1 https://github.com/yarnpkg/berry.git

RUN mv /opt/project/berry /opt/project/src

RUN cd /opt/project/src \
   && yarn build:clipref