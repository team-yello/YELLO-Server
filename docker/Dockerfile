FROM azul/zulu-openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} yello.jar
ENTRYPOINT ["java","-jar","yello.jar"]
