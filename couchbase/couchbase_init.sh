#!/bin/bash
# Init Couchbase

couchbase_address=127.0.0.1:8091

/wait-for-it.sh $couchbase_address -- \

couchbase-cli cluster-init \
	--cluster-username=Administrator \
	--cluster-password=password \
	--services=data,index,query,fts \
	--cluster-ramsize=500 \
	--cluster-index-ramsize=256 \
	--cluster-fts-ramsize=256 \
	--index-storage-setting=memopt


couchbase-cli bucket-create -c $couchbase_address \
	--bucket=user_connections \
	--bucket-type=couchbase \
	--bucket-ramsize=100 \
	--bucket-replica=1 \
	--bucket-priority=high \
	--bucket-eviction-policy=valueOnly \
	-u Administrator -p password
	
couchbase-cli bucket-create -c $couchbase_address \
	--bucket=instance_connections \
	--bucket-type=couchbase \
	--bucket-ramsize=100 \
	--bucket-replica=1 \
	--bucket-priority=high \
	--bucket-eviction-policy=valueOnly \
	-u Administrator -p password
	
couchbase-cli bucket-create -c $couchbase_address \
	--bucket=entities \
	--bucket-type=couchbase \
	--bucket-ramsize=300 \
	--bucket-replica=1 \
	--bucket-priority=high \
	--bucket-eviction-policy=valueOnly \
	-u Administrator -p password