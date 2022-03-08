FROM mcr.microsoft.com/dotnet/sdk:5.0

# Do not change SRC_DIR, value is expected by tests
ENV SRC_DIR=/opt/project/src

RUN apt-get update

# Install java
RUN mkdir -p /usr/share/man/man1/
# Above due to: https://github.com/geerlingguy/ansible-role-java/issues/64
RUN apt-get install -y openjdk-11-jre

# Install git
RUN apt-get install -y git

# Set up the test project
RUN mkdir -p ${SRC_DIR}

RUN git clone --depth 1 https://github.com/GaProgMan/dwCheckApi.git ${SRC_DIR}

RUN cd ${SRC_DIR} \
   && dotnet build