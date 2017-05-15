#!/bin/sh

# To override the default location, specify your own PACKMAN_FULL_JAR_PATH in your environment
# and *that* jar will be used. If it does not exist, an attempt to download the *.jar file will
# be made.

PACKMAN_FULL_JAR_PATH=${PACKMAN_FULL_JAR_PATH:-/tmp/hub-packman-jenkins-0.0.1-SNAPSHOT.jar}
PACKMAN_JAR_TO_GET=${PACKMAN_FULL_JAR_PATH##*/}

all_command_line_args=$@

run() {
  get_packman
  run_packman
}

get_packman() {
  if [ -f $PACKMAN_FULL_JAR_PATH ]; then
    echo "using local ${PACKMAN_FULL_JAR_PATH}"
  else
    echo "getting ${PACKMAN_JAR_TO_GET} from remote"
    curl -o $PACKMAN_FULL_JAR_PATH https://blackducksoftware.github.io/hub-packman/$PACKMAN_JAR_TO_GET
  fi
}

run_packman() {
  echo "running packman: ${PACKMAN_FULL_JAR_PATH} ${all_command_line_args}
  java -jar $PACKMAN_FULL_JAR_PATH $all_command_line_args
}

run
