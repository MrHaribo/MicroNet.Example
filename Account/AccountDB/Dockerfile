FROM postgres

# Change Password as soon as possible
ENV POSTGRES_PASSWORD=1234

EXPOSE 5432

RUN mkdir ~/AccountDB_data
VOLUME ~/AccountDB_data:/var/lib/postgresql/data
COPY init_db.sql /docker-entrypoint-initdb.d/init_db.sql