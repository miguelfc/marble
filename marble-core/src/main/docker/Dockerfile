FROM java:8
VOLUME /tmp
ADD app.jar app.jar
RUN bash -c 'touch /app.jar'
CMD cd config & java -jar ../app.jar
