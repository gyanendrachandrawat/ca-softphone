FROM public.ecr.aws/docker/library/maven:3.9.3-amazoncorretto-11 AS build
COPY . /home/src
WORKDIR /home/src
ENV AWS_REGION=us-east-1
RUN mvn clean package

FROM public.ecr.aws/amazoncorretto/amazoncorretto:11
EXPOSE 8080
COPY --from=build /home/src/target/twilio-softphone-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=${ENV_NAME}","/app.jar"]