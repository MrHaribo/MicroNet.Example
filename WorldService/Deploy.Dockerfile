FROM maven:latest

RUN mkdir --parents /usr/src/app
WORKDIR /usr/src/app

ENV message_broker_address="activemq:61616"

ADD . /usr/src/app

CMD java -cp ./target/classes:./target/lib/* ServiceImpl