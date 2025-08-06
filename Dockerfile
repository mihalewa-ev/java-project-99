FROM eclipse-temurin:21.0.2_13-jdk

ARG GRADLE_VERSION=8.7

RUN apt-get update && apt-get install -yq unzip wget

RUN wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip \
    && rm gradle-${GRADLE_VERSION}-bin.zip

ENV GRADLE_HOME=/opt/gradle
RUN mv gradle-${GRADLE_VERSION} ${GRADLE_HOME}
ENV PATH=$PATH:$GRADLE_HOME/bin

WORKDIR /

COPY ./ .

RUN gradle installDist

CMD ./build/install/app/bin/app --spring.profiles.active=prod