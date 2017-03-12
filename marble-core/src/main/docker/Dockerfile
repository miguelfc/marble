FROM java:8
VOLUME /tmp
ADD marble-core-1.0.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 1111
CMD cd config & java -jar ../app.jar
