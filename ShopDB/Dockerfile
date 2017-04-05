FROM postgres
RUN mkdir ~/shop_data
ENV POSTGRES_PASSWORD=1234
VOLUME ~/shop_data:/var/lib/postgresql/data
COPY shop_db.sql /docker-entrypoint-initdb.d/shop_db.sql