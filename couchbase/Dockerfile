FROM couchbase:latest

EXPOSE 8091-8094:8091-8094
EXPOSE 11210:11210

COPY init_couchbase.sh /init_couchbase.sh
RUN chmod +x /init_couchbase.sh

COPY start_couchbase.sh /start_couchbase.sh
RUN chmod +x /start_couchbase.sh

RUN wget https://raw.githubusercontent.com/MrHaribo/MicroNet.Example/master/wait-for-it.sh
RUN chmod +x wait-for-it.sh

ENTRYPOINT /start_couchbase.sh