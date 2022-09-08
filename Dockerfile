FROM maven:3.8.6-jdk-11 as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

FROM adoptopenjdk/openjdk11:alpine-slim

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/target/demoUser*.jar /demoUserpg.jar

# Run the web service on container startup.
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/demoUserpg.jar"]