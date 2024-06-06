FROM node:latest

RUN npm install -g npm@10

RUN mkdir -p /opt/project/src

RUN wget https://github.com/blackducksoftware/github-action/archive/refs/tags/v2.2.zip 
RUN unzip v2.2.zip -d /opt/project/src
RUN rm v2.2.zip

WORKDIR /opt/project/src/github-action-2.2
RUN rm package-lock.json
RUN rm -rf node_modules

RUN sed -i '' 's/\^//g' package.json
RUN npm install .

# We have to remove the new package lock or else the package lock detector will run
# instead of the CLI one.
RUN rm package-lock.json