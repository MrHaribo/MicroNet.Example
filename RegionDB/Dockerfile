FROM postgres
RUN mkdir ~/region_data
ENV POSTGRES_PASSWORD=1234
VOLUME ~/region_data:/var/lib/postgresql/data
COPY region_db.sql /docker-entrypoint-initdb.d/region_db.sql