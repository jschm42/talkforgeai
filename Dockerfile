FROM amazoncorretto:19-alpine-jdk

# 8090 is the port number the application will use
EXPOSE 8090

# Install dependencies
# freetype is required for the font rendering in PlantUML
RUN apk add --no-cache graphviz ttf-dejavu fontconfig freetype

# Create empty properties file
RUN mkdir -p /usr/local/talkforgeai
RUN touch /usr/local/talkforgeai/talkforgeai.properties

# Set up a volume for the data directory
VOLUME /data

# copy jar
COPY ./app/target/talkforgeai-*.jar /usr/local/lib/talkforgeai.jar

# startup command
CMD java -DTALKFORGEAI_DATADIR=/data -jar /usr/local/lib/talkforgeai.jar --spring.config.additional-location=/usr/local/talkforgeai/talkforgeai.properties
