# We will use OpenJDK 20
FROM ghcr.io/graalvm/jdk-community:20.0.1

# 8080 is the port number the application will use
EXPOSE 8090

# Set up a volume for the data directory
VOLUME /data

# copy jar
COPY ./app/target/app-*.jar /usr/local/lib/talkforgeai.jar

# startup command
CMD java -Dtalkforge.data-directory=/data -jar /usr/local/lib/talkforgeai.jar --spring.config.additional-location=/usr/local/talkforgeai/talkforgeai.properties
