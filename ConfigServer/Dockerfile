FROM eclipse-temurin:22-jdk-jammy
RUN apt-get update && apt-get install -y curl
VOLUME /tmp
ARG JAR_FILE=target/ConfigServer-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]