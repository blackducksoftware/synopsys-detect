FROM adoptopenjdk/maven-openjdk11

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src

# Install git
RUN apt-get update
RUN apt-get install -y git

# Set up the test project
RUN mkdir -p ${SRC_DIR}

RUN git clone --depth 1 https://github.com/oktadev/example-maven-plugin ${SRC_DIR}

RUN cd ${SRC_DIR} \
   && mvn clean install