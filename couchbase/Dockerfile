FROM couchbase:latest

EXPOSE 8091-8094:8091-8094
EXPOSE 11210:11210

COPY couchbase_init.sh /couchbase_init.sh
RUN chmod +x /couchbase_init.sh

COPY couchbase_start.sh /couchbase_start.sh
RUN chmod +x /couchbase_start.sh

RUN wget https://raw.githubusercontent.com/MrHaribo/MicroNet.Example/master/wait-for-it.sh
RUN chmod +x wait-for-it.sh

ENTRYPOINT /couchbase_start.sh