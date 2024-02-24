FROM openjdk:17-jdk-alpine
COPY /build/libs/CrackHashWorker-0.0.1-SNAPSHOT.jar /worker.jar
ENTRYPOINT ["java", "-jar", "worker.jar"]