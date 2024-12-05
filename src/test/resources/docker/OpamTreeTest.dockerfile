FROM openjdk:8-jdk

RUN apt update \
   && apt install -y vim

RUN apt-get install -y patch bubblewrap gcc make

ENV SRC_DIR=/opt/project/src

ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2"

RUN mkdir -p ${SRC_DIR}

RUN curl -s -L -O https://github.com/ocaml/opam/releases/download/2.3.0/opam-2.3.0-x86_64-linux \
    && install opam-2.3.0-x86_64-linux /usr/local/bin/opam \
    && rm opam-2.3.0-x86_64-linux

RUN opam init -y --disable-sandboxing --shell-setup

RUN git clone https://github.com/aantron/dream.git ${SRC_DIR} \
   && opam switch create dream 5.1.1 \
   && eval $(opam env --switch=dream) \
   && opam install . -y --with-test --with-doc

RUN cd ${SRC_DIR}