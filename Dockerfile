FROM openjdk:8-jdk-alpine
COPY MANIFEST.txt MANIFEST.txt
COPY bin/ bin/
RUN jar cfm CbsForIMPS.jar MANIFEST.txt -C bin/ .
ENTRYPOINT ["java","-jar","CbsForIMPS.jar"]
EXPOSE 80 9094
