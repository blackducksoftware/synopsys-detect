FROM openjdk:8-jdk

ENV SRC_DIR=/opt/project/src

ENV JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2"

RUN mkdir -p ${SRC_DIR}

RUN curl -s -L -O https://github.com/ocaml/opam/releases/download/2.1.6/opam-2.1.6-x86_64-linux \
    && install opam-2.1.6-x86_64-linux /usr/local/bin/opam \
    && rm opam-2.1.6-x86_64-linux

RUN opam init -y --disable-sandboxing --shell-setup

RUN git clone https://github.com/squirrel-prover/squirrel-prover.git ${SRC_DIR} \
   && opam switch create squirrel-prover 5.1.1 \
   && eval $(opam env --switch=squirrel) \
   && opam install . -y --with-test --with-doc

RUN cd ${SRC_DIR}