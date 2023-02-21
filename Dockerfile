# syntax=docker/dockerfile:1

# This template uses the latest Maven 3 release, e.g., 3.8.6, and OpenJDK 17 (LTS)
# for verifying and deploying images
# Maven 3.8.x REQUIRES HTTPS repositories.
# See https://maven.apache.org/docs/3.8.1/release-notes.html#how-to-fix-when-i-get-a-http-repository-blocked for more.
FROM maven:3-openjdk-17 AS builder

# `showDateTime` will show the passed time in milliseconds. You need to specify `--batch-mode` to make this work.
ENV MAVEN_OPTS -Dhttps.protocols=TLSv1.2 \
    -Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository \
    -Dorg.slf4j.simpleLogger.showDateTime=true \
    -Djava.awt.headless=true

# As of Maven 3.3.0 instead of this you MAY define these options in `.mvn/maven.config` so the same config is used
# when running from the command line.
# As of Maven 3.6.1, the use of `--no-tranfer-progress` (or `-ntp`) suppresses download and upload messages. The use
# of the `Slf4jMavenTransferListener` is no longer necessary.
# `installAtEnd` and `deployAtEnd` are only effective with recent version of the corresponding plugins.
ENV MAVEN_CLI_OPTS --batch-mode \
    --errors \
    --fail-never \
    --show-version \
    --no-transfer-progress

WORKDIR /app

ADD pom.xml .
RUN mvn $MAVEN_CLI_OPTS verify

ADD . .
RUN mvn clean package -DskipTests=true

FROM openjdk:17-jdk

ARG DEBUG
ARG ENV
ARG JAR_FILE=target/*.jar

ENV ENV=${ENV:-local}
ENV DEBUG=${DEBUG:-true}

EXPOSE 8182

COPY --from=builder /app/${JAR_FILE} marketplace.jar

ENTRYPOINT java -jar /marketplace.jar
