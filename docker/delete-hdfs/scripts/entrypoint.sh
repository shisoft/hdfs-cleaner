#!/usr/bin/env bash

# Setup Hadoop
echo "  Setting Hadoop Config ..."
curl -s http://resources.prod.factual.com/services/hadoop/cdh5/scripts/get_configs.sh | bash

echo ${KINIT_PASSWORD} | kinit ${KINIT_USER}

hadoop fs -rm -r -f ${FILES_TO_DELETE}