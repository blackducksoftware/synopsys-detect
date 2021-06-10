FROM mcr.microsoft.com/dotnet/sdk:5.0

RUN apt update

# Install git
RUN apt install -y git

# Set up the test project
RUN mkdir -p /opt/project

RUN cd /opt/project \
   && git clone https://github.com/GaProgMan/dwCheckApi.git

RUN mv /opt/project/dwCheckApi /opt/project/src

RUN dotnet build