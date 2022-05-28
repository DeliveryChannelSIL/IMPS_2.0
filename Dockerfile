FROM openjdk:8-jdk-alpine
COPY MANIFEST.txt MANIFEST.txt
COPY bin/ bincode/
COPY lib/ lib/
COPY ini/ ini/
RUN jar cfm CbsForIMPS.jar MANIFEST.txt -C bincode/ .
RUN ls -la /*
ENTRYPOINT ["java","-jar","CbsForIMPS.jar"]
EXPOSE 80 9094
