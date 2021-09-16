FROM mcr.microsoft.com/dotnet/sdk:5.0

RUN apt update

# Install java
RUN mkdir /usr/share/man/man1/
# Above due to: https://github.com/geerlingguy/ansible-role-java/issues/64
RUN apt-get install -y openjdk-11-jre

# Install git
RUN apt install -y git

# Set up the test project
RUN mkdir -p /opt/project

RUN cd /opt/project \
   && git clone https://github.com/GaProgMan/dwCheckApi.git

RUN mv /opt/project/dwCheckApi /opt/project/src

RUN cd /opt/project/src && dotnet build