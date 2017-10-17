FROM docker-internal.alfresco.com/alfresco/alfresco-base-java:0.1
VOLUME /tmp
ADD ${project.build.finalName}.jar ${project.build.finalName}.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","${project.build.finalName}.jar"]