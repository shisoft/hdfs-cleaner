#!/usr/bin/env bash

docker build --rm=true \
    -t registry.prod.factual.com/hdfs-cleaner-delete ./
