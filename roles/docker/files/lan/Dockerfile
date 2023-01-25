FROM openjdk as builder

RUN mkdir -p /app/

COPY . /app/
WORKDIR /app/

RUN ./mvnw clean package

FROM openjdk

COPY --from=builder /app/target/lan-0.0.1-SNAPSHOT.jar /app/lan-0.0.1-SNAPSHOT.jar

COPY ./certs /cert/ 
RUN keytool -importcert -keypass changeit -alias ROOT -file /cert/ca.pem -keystore $JAVA_HOME/lib/security/cacerts -noprompt -storepass changeit

ENTRYPOINT ["java", "-jar", "/app/lan-0.0.1-SNAPSHOT.jar"] 