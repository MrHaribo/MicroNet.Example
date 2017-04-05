FROM postgres
RUN mkdir ~/avatar_data
ENV POSTGRES_PASSWORD=1234
VOLUME ~/avatar_data:/var/lib/postgresql/data
COPY avatar_db.sql /docker-entrypoint-initdb.d/avatar_db.sql