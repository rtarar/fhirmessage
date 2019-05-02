# build stage
FROM maven:3-jdk-8 as builder
RUN mkdir -p /usr/src/app
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn clean package -DskipTests=true

# create Image stage
FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim
RUN apk --no-cache add curl
COPY --from=builder /usr/src/app/target/fhirmessage*.jar fhirmessage.jar
EXPOSE 9999
RUN sh -c 'touch ./hl7-incident-processor.jar'
ENTRYPOINT ["java","-jar","./fhirmessage.jar"]
