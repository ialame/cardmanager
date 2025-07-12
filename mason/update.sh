#!/usr/bin/env bash

if [ "$1" == "version" ]; then
  if [ -z "$2" ]; then
    echo "Usage: $0 version <version>"
    exit 1
  fi
  mvn versions:set versions:commit -DnewVersion=$2 -DprocessAllModules
  exit 0
fi

mvn versions:use-latest-releases versions:update-parent versions:display-plugin-updates versions:update-properties versions:commit
