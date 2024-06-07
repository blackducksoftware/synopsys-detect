FROM openjdk:11-jdk-slim
#FROM node:22

RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget -qO- https://deb.nodesource.com/setup_22.x | bash - && \
    apt-get install -y nodejs && \
    npm install -g npm@10

#RUN npm install -g npm@10

#RUN apt-get update && apt-get install -y openjdk-17-jdk

RUN mkdir -p /opt/project/src

RUN wget -q https://github.com/blackducksoftware/github-action/archive/refs/tags/v2.2.zip -O v2.2.zip
RUN unzip v2.2.zip -d /opt/project/src
RUN rm v2.2.zip

WORKDIR /opt/project/src/github-action-2.2
RUN rm package-lock.json
RUN rm -rf node_modules

RUN sed -i 's/\^//g' package.json
RUN npm install .

# We have to remove the new package lock or else the package lock detector will run
# instead of the CLI one.
RUN rm package-lock.json