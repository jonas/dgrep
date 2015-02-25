#!/bin/sh
#
# Install dependencies for DGrep on Ubuntu

###############################################################################
# Based on the Oracle Java 7 Dockerfile
#  - https://github.com/dockerfile/java/tree/master/oracle-java7

# Update system and pull in basic utilities.
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update && \
  apt-get -y upgrade && \
  apt-get install -y build-essential && \
  apt-get install -y software-properties-common && \
  apt-get install -y byobu curl git htop man unzip vim wget && \
  rm -rf /var/lib/apt/lists/*

# Install Java.
  echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java7-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk7-installer

echo "Remember to add the following to your ~/.bashrc:"
echo " export JAVA_HOME=\"/usr/lib/jvm/java-7-oracle\""

###############################################################################
# Based on the Scala/SBT Dockerfile
#  - https://registry.hub.docker.com/u/hseeberger/scala-sbt/dockerfile/

# Install sbt
  curl -L -o sbt-0.13.7.deb https://dl.bintray.com/sbt/debian/sbt-0.13.7.deb && \
  dpkg -i sbt-0.13.7.deb && \
  rm sbt-0.13.7.deb && \
  apt-get update && \
  apt-get install sbt

echo "Remember to add the following to your ~/.bashrc:"
echo "  export SBT_OPTS=-Xmx1024M"
