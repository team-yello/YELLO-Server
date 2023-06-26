FROM amazoncorretto:17.0.7-alpine
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} yello.jar
ENTRYPOINT ["java","-jar","yello.jar"]