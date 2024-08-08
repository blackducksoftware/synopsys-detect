FROM debian:bookworm-20240110-slim

RUN apt-get update && apt-get -y install wget unzip

RUN wget https://storage.googleapis.com/dart-archive/channels/stable/release/3.5.0/linux_packages/dart_3.5.0-1_amd64.deb

RUN dpkg -i dart_3.5.0-1_amd64.deb

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src

# Set up the test project
RUN mkdir -p ${SRC_DIR}

RUN dart create ${SRC_DIR} --force
