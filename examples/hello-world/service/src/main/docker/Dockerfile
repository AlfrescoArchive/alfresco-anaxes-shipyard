FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD ${project.build.finalName}.jar ${project.build.finalName}.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","${project.build.finalName}.jar"]