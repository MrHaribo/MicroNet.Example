FROM postgres
RUN mkdir ~/vehicle_data
ENV POSTGRES_PASSWORD=1234
VOLUME ~/vehicle_data:/var/lib/postgresql/data
COPY vehicle_db.sql /docker-entrypoint-initdb.d/vehicle_db.sql