#!/bin/bash
# Start Couchbase

/couchbase_init.sh &

/entrypoint.sh couchbase-server
