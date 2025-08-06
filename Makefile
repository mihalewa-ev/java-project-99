setup:
	./gradlew wrapper

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew clean install

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain checkstyleTest

check-deps:
	./gradlew dependencyUpdates -Drevision=release

