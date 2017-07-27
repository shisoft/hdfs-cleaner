#!/usr/bin/env bash

docker run -i --rm=true \
    -e "STITCH_KEY_PASSPHRASE=${STITCH_KEY_PASSPHRASE}" \
    -p 4050:4052 \
    -t registry.prod.factual.com/hdfs-cleaner