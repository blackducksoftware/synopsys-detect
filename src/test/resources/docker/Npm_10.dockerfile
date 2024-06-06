FROM node:22

RUN npm install -g npm@10

RUN apt-get update && apt-get install -y openjdk-17-jdk

RUN mkdir -p /opt/project/src

RUN wget https://github.com/blackducksoftware/github-action/archive/refs/tags/v2.2.zip 
RUN unzip v2.2.zip -d /opt/project/src
RUN rm v2.2.zip

RUN cd /opt/project/src/github-action-2.2 && \
  rm package-lock.json && \
  rm -rf node_modules && \
  sed -i 's/\^//g' package.json && \
  npm install . && \
  rm package-lock.json

# We have to remove the new package lock or else the package lock detector will run
# instead of the CLI one.
