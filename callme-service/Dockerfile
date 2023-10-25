FROM azul/zulu-openjdk:21-jdk-crac-latest
#ENV JAVA_OPTS -XX:CRaCRestoreFrom=/crac
COPY target/callme-service-1.1.0.jar /app/callme-service-1.1.0.jar
#ENTRYPOINT ["sh", "-c"]
#CMD ["java $JAVA_OPTS -jar /app/callme-service-1.1.0.jar"]
#CMD ["java $JAVA_OPTS"]

#RUN mkdir -p /app
COPY src/scripts/entrypoint.sh /app/entrypoint.sh
RUN chmod 755 /app/entrypoint.sh
#ENTRYPOINT /app/entrypoint.sh