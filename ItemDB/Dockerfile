FROM postgres
RUN mkdir ~/item_data
ENV POSTGRES_PASSWORD=1234
VOLUME ~/item_data:/var/lib/postgresql/data
COPY item_db.sql /docker-entrypoint-initdb.d/item_db.sql