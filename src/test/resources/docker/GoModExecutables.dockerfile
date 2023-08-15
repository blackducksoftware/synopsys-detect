FROM maven:3-jdk-8-alpine

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src

# Install git
#RUN apt-get update
#RUN apt-get install -y git
RUN apk update && apk upgrade && \
    apk add --no-cache bash git openssh

# Install Go@1.20
WORKDIR /home/app
RUN wget -q "https://golang.org/dl/go1.16.6.linux-amd64.tar.gz"
RUN tar -xzf go1.16.6.linux-amd64.tar.gz
RUN rm go1.16.6.linux-amd64.tar.gz
ENV PATH="/home/app/go1.16.6/go/bin/go:${PATH}"

#RUN wget https://golang.org/dl/go1.16.6.linux-amd64.tar.gz \
#    && mkdir /usr/local/go1.16.6 \
#    && tar -C /usr/local/go1.16.6 -xzf go1.16.6.linux-amd64.tar.gz \
#    && rm go1.16.6.linux-amd64.tar.gz

# Set up the test project
RUN mkdir -p ${SRC_DIR}

RUN git clone --depth 1 https://github.com/Masterminds/squirrel.git ${SRC_DIR}

#RUN cd ${SRC_DIR} \
#   && go mod tidy
