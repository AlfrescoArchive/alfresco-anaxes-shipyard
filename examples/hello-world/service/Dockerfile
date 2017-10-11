FROM java:8
VOLUME /tmp
ADD ./target/anaxes-hello-service-0.1-SNAPSHOT.jar anaxes-hello-service-0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/anaxes-hello-service-0.1-SNAPSHOT.jar"]