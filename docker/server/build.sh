#!/usr/bin/env bash

mkdir source
cp ../../project.clj source/project.clj
cp ../../config.json source/config.json
cp -r ../../src source/src

docker build --rm=true \
    -t registry.prod.factual.com/hdfs-cleaner ./

rm -r source