#!/bin/sh

# exit on error and bad variables
set -eu

# capture first argument as the jar file
JAR_FILE="$1"

# shift it out of the args list
shift

# pass the remaining args to java
java "$@" -jar "${JAR_FILE}"
