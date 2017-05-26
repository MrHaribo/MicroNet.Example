#!/bin/bash
# Start Couchbase

/init_couchbase.sh &

/entrypoint.sh couchbase-server
