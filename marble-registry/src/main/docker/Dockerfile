FROM java:8
VOLUME /tmp
ADD marble-registry-1.0.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 1111
ENTRYPOINT ["java","-jar","/app.jar"]
