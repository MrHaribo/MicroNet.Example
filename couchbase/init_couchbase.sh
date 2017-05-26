#!/bin/bash
# Init Couchbase

/wait-for-it.sh  127.0.0.1:8091 -- \

couchbase-cli cluster-init \
	--cluster-username=Administrator \
	--cluster-password=password \
	--services=data,index,query,fts \
	--cluster-ramsize=300 \
	--cluster-index-ramsize=256 \
	--cluster-fts-ramsize=256 \
	--index-storage-setting=memopt


couchbase-cli bucket-create -c 127.0.0.1:8091 \
	--bucket=user_connections \
	--bucket-type=couchbase \
	--bucket-ramsize=100 \
	--bucket-replica=1 \
	--bucket-priority=high \
	--bucket-eviction-policy=valueOnly \
	-u Administrator -p password
	
couchbase-cli bucket-create -c 127.0.0.1:8091 \
	--bucket=instance_connections \
	--bucket-type=couchbase \
	--bucket-ramsize=100 \
	--bucket-replica=1 \
	--bucket-priority=high \
	--bucket-eviction-policy=valueOnly \
	-u Administrator -p password