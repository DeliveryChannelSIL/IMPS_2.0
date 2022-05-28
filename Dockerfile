FROM openjdk:8-jdk-alpine
RUN jar cfm CbsForIMPS.jar MANIFEST.txt -C bin/ .
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} CbsForIMPS.jar
ENTRYPOINT ["java","-jar","CbsForIMPS.jar"]