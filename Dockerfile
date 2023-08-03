# We will use OpenJDK 20
FROM openjdk:20

# 8080 is the port number the application will use
EXPOSE 8090

# copy jar
COPY ./app/target/app-*.jar /usr/local/lib/talkforgeai.jar

# startup command
ENTRYPOINT ["java","-jar","/usr/local/lib/talkforgeai.jar"]
