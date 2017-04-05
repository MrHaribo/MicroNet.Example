FROM postgres
RUN mkdir ~/account_data
ENV POSTGRES_PASSWORD=1234
VOLUME ~/account_data:/var/lib/postgresql/data
COPY account_db.sql /docker-entrypoint-initdb.d/account_db.sql