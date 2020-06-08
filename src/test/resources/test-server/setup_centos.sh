#!/bin/bash

echo "Setting up (centos) system for Detect"
echo "Requirements: CentOS 7 with 100G disk"

sudo yum -y update
sudo yum -y install rsync
sudo yum -y install git
sudo yum -y install java-1.8.0-openjdk

# Install maven
cd /tmp
wget http://mirrors.ocf.berkeley.edu/apache/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz
cd /opt
sudo mkdir maven
cd maven
sudo tar xvf /tmp/apache-maven-3.6.0-bin.tar.gz
sudo ln -s /opt/maven/apache-maven-3.6.0/bin/mvn /usr/local/bin/mvn

# Install Docker
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install docker-ce
sudo usermod -aG docker $(whoami)
sudo systemctl enable docker.service
sudo systemctl start docker.service


# 
# Create some projects
sudo mkdir -p /opt/blackduck/projects/maven-repos
sudo mkdir -p /opt/blackduck/projects/gradle-repos
sudo sudo chmod -R a+rwx /opt/blackduck
cd /opt/blackduck/projects/maven-repos
git clone https://github.com/junit-team/junit4.git
cd junit4
mvn clean package

cd /opt/blackduck/projects/gradle-repos
git clone https://github.com/cbeust/testng.git
cd testng
./gradlew clean build

# Install Detect
cd /opt/blackduck
curl -O https://detect.synopsys.com/detect.sh; chmod +x detect.sh

# Create README.txt
echo "Currently installed: one Maven project, and one Gradle project." > README.txt
echo "The Maven project is in /opt/blackduck/projects/maven-repos" >> README.txt
echo "The Gradle project is in /opt/blackduck/projects/gradle-repos" >> README.txt
echo "" >> README.txt
echo "You should be able to execute the latest released version of Detect by doing something like this:" >> README.txt
echo "	cd /opt/blackduck/projects/<repos dir>/<project>" >> README.txt
echo "	/opt/blackduck/detect.sh --blackduck.url=https://int-hub04.dc1.lan --blackduck.username=sysadmin --blackduck.password=blackduck --blackduck.trust.cert=true" >> README.txt
echo "" >> README.txt
echo "" >> README.txt
echo "You may want to download a newer Detect snapshot version to test. You can do this with a command like:" >> README.txt
echo "    curl -O https://sig-repo.synopsys.com/bds-integrations-snapshot/com/synopsys/integration/synopsys-detect/6.4.0-SNAPSHOT/synopsys-detect-6.4.0-20200515.045330-70.jar" >> README.txt
echo "To determine the URL to the .jar, point your browser to: https://sig-repo.synopsys.com/bds-integrations-snapshot/com/synopsys/integration/synopsys-detect/" >> README.txt
echo "" >> README.txt
echo "Then you could execute that early version by doing something like this:" >> README.txt
echo "	cd /opt/blackduck/projects/<repos-dir>/<project>" >> README.txt
echo "	java -jar /opt/blackduck/<the .jar you downloaded> --blackduck.url=https://int-hub04.dc1.lan --blackduck.username=sysadmin --blackduck.password=blackduck --blackduck.trust.cert=true" >> README.txt
echo "" >> README.txt
echo "To run Detect on a docker image:" >> README.txt
echo "" >> README.txt
echo "One time setup:" >> README.txt
echo "	1. sudo usermod -aG docker \$(whoami)" >> README.txt
echo "	2. logout, and log back in" >> README.txt
echo "" >> README.txt
echo "Then run Detect with the following additional arguments (<image> and <tag> identifies the Docker image you want to inspect):" >> README.txt
echo "	--detect.docker.image=<image>:<tag>" >> README.txt

echo "Log out and log back in (so docker will work)"

