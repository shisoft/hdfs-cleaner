#!/usr/bin/env bash
echo "search la.prod.factual.com factual.com prod.factual.com corp.factual.com os.prod.factual.com" >> /etc/resolv.conf

# Setup Hadoop
echo "  Setting Hadoop Config ..."
curl -s http://resources.prod.factual.com/services/hadoop/cdh5/scripts/get_configs.sh | bash

# Set up Kerberos credentials(as stitch) for Hadoop
echo "  Setting Kerberos ..."
STITCH_PRINCIPLE=stitch@FACTUAL.COM
STITCH_KEYTAB=/etc/krb5.keytab
curl -s https://keyserver.prod.factual.com/hadoop/services/stitch/stitch.stitch-server.keytab.sec | openssl des3 -d -k "$STITCH_KEY_PASSPHRASE" > $STITCH_KEYTAB
KINIT_CMD="kinit -l 25h -kt $STITCH_KEYTAB $STITCH_PRINCIPLE"
$KINIT_CMD

echo "*/1 * * * * root ${KINIT_CMD} >/dev/null 2>&1" >> /etc/crontab
cron

DFS_NAMESERVICE=${DFS_NAMESERVICE:-dev}

export DFS_ADDRESS=hdfs://${DFS_NAMESERVICE}