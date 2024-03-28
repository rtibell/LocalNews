# Start with a base image containing Java runtime (adopt the version according to your needs)
FROM openjdk:21-jdk-slim
#FROM openjdk:21

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

#ENV MYSQL_HOST=192.168.86.253 MYSQL_PORT=3306 MYSQL_DATABASE=dev

# Add the application's jar to the container
ADD target/localnews-0.0.1-SNAPSHOT.jar myapplication.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "/myapplication.jar"]%
