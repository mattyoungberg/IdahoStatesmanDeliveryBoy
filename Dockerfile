FROM maven:3.8-amazoncorretto-11 AS builder

RUN mkdir /isdb

COPY ./pom.xml /isdb

COPY ./src /isdb/src

RUN mvn -f /isdb/pom.xml clean package

FROM amazoncorretto:11-alpine3.16
ARG TRUSTSTOREPASS

COPY ./boisepubliclibrary.cer /var/tmp/

RUN keytool -importcert -trustcacerts -cacerts -storepass changeit -noprompt \
    -alias boisepubliclibrary -file /var/tmp/boisepubliclibrary.cer

COPY --from=builder /isdb/target/IdahoStatesmanDeliveryBoy-2.0.jar /usr/local/bin

RUN keytool -storepasswd -cacerts -new ${TRUSTSTOREPASS} -storepass changeit

CMD java -jar /usr/local/bin/IdahoStatesmanDeliveryBoy-2.0.jar