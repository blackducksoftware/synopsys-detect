FROM openjdk:8-jdk

RUN apt update \
   && apt install -y vim

ENV SRC_DIR=/opt/project/src

ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2"

RUN mkdir -p ${SRC_DIR}

RUN git clone https://github.com/tarides/opam-monorepo.git ${SRC_DIR} \

RUN cd ${SRC_DIR}