#!/usr/bin/env bash

docker run -i --rm=true \
    -e "KINIT_PASSWORD=${KINIT_PASSWORD}" \
    -e "FILES_TO_DELETE=${FILES_TO_DELETE}" \
    -p 4050:4052 \
    -t registry.prod.factual.com/hdfs-cleaner-delete